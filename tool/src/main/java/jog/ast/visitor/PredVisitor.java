package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.visitor.CodeGenUtil.ValType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/*
 * Extra predicate besides before.
 */
public class PredVisitor extends ExprVisitorAdapter {

    private final Map<CGExpr, String> nodeToCodeGen;

    private final Deque<String> stack;

    // Track the operator of a BinNode we are visiting
    // null: not in a BinNode, otherwise: indicate the operator
    private CGExpr parent;

    private boolean inCallNode;

    public PredVisitor(Map<CGExpr, String> nodeToCodeGen) {
        this.nodeToCodeGen = nodeToCodeGen;
        this.stack = new ArrayDeque<>();
        this.inCallNode = false;
    }

    public String getResult() {
        return stack.peek();
    }

    @Override
    public void visit(BinNode binNode, Void arg) {
        if (inCallNode) {
            stack.push(nodeToCodeGen.get(binNode));
            return;
        }

        CGExpr left = binNode.getLeft();
        CGExpr right = binNode.getRight();
        CGExpr parentBackup = parent;
        parent = binNode;

        // case 1:
        // var </<= lit OR lit >/>= var
        // -> nodeCode(var)->isa_int()->_high </<= lit
        // OR lit >/>= nodeCode(var)->isa_int()->_high

        // case 2:
        // var >/>= lit OR lit </<= var
        // -> nodeCode(var)->isa_int()->_low >/>= lit
        // OR lit </<= nodeCode(var)->isa_int()->_low

        // case 3:
        // con <op> lit
        // -> con <op> lit.getValue()

        // case 4:
        // var ==/!= lit
        // -> type(var) ==/!= type(lit)

        left.accept(this, arg);
        String lCode = wrapParenthesesIfNeeded(left, stack.pop());

        right.accept(this, arg);
        String rCode = wrapParenthesesIfNeeded(right, stack.pop());

        BinNode.Op operator = binNode.getOperator();
        // These operators would invoke special functions, e.g.,
        // java_add, and we need to cast int literals to jint.
        if (operator == BinNode.Op.ADD
                || operator == BinNode.Op.SUB
                || operator == BinNode.Op.MUL) {
            lCode = castLiteral(left, lCode);
            rCode = castLiteral(right, rCode);
        }
        String code = CodeGenUtil.evaluateBinaryExpr(lCode, rCode, operator);
        stack.push(code);

        // reset parent
        parent = parentBackup;
    }

    @Override
    public void visit(UnaryNode unaryNode, Void arg) {
        if (inCallNode) {
            stack.push(nodeToCodeGen.get(unaryNode));
            return;
        }
        CGExpr operand = unaryNode.getOperand();
        operand.accept(this, arg);
        String opCode = wrapParenthesesIfNeeded(operand, stack.pop());
        String code = CodeGenUtil.evaluateUnaryExpr(opCode, unaryNode.getOperator());
        stack.push(code);
    }

    @Override
    public void visit(ConNode conNode, Void arg) {
        if (inCallNode) {
            stack.push(nodeToCodeGen.get(conNode));
        } else {
            stack.push(conNode.getIdentifier());
        }
    }

    @Override
    public void visit(VarNode varNode, Void arg) {
        if (inCallNode) {
            stack.push(nodeToCodeGen.get(varNode));
            return;
        }
        String code = null;
        if (inEQOrNERelation()) {
            code = CodeGenUtil.makeType(nodeToCodeGen.get(varNode));
        }
        ValType valType = varNode.getValType();
        if ((inLessRelation() && isLeft(varNode))
                || (inGreaterRelation() && isRight(varNode))) {
            code = CodeGenUtil.makeGetHigh(nodeToCodeGen.get(varNode), valType);
        }
        if ((inGreaterRelation() && isLeft(varNode))
                || (inLessRelation() && isRight(varNode))) {
            code = CodeGenUtil.makeGetLow(nodeToCodeGen.get(varNode), valType);
        }
        stack.push(code);
    }

    @Override
    public void visit(LitNode litNode, Void arg) {
        if (inEQOrNERelation() && isOtherOperandAVarNode(litNode)) {
            stack.push(CodeGenUtil.makeType(litNode.getValue()));
            return;
        }
        // not possible in a call node
        stack.push(litNode.asStr());
    }

    @Override
    public void visit(CallNode callNode, Void arg) {
        inCallNode = true;

        List<String> aCode = new ArrayList<>();
        for (CGExpr a : callNode.getArguments()) {
            a.accept(this, arg);
            aCode.add(stack.pop());
        }
        String code = CodeGenUtil.makeCall(callNode, aCode, callNode.getValType());
        stack.push(code);

        // reset flag
        inCallNode = false;
    }

    @Override
    public void visit(TypeNode typeNode, Void arg) {
        stack.push(CodeGenUtil.makeType(typeNode.getType()));
    }

    @Override
    public void visit(OpNode opNode, Void arg) {
        stack.push(opNode.getOperator().toBinOp().toCodeGen(opNode.getValType()));
    }

    /*--------------------------------------------------------------*/

    private static String castLiteral(CGExpr node, String code) {
        if (node instanceof LitNode) {
            return CodeGenUtil.makeCastToJIntOrLong(code, node.getValType());
        }
        return code;
    }

    private String wrapParenthesesIfNeeded(CGExpr node, String code) {
        if (node instanceof BinNode || node instanceof UnaryNode) {
            return CodeGenUtil.wrapParentheses(code);
        }
        return code;
    }

    private BinNode.Op getParentOperator() {
        return ((BinNode) parent).getOperator();
    }

    private boolean isLeft(CGExpr node) {
        return node == ((BinNode) parent).getLeft();
    }

    private boolean isRight(CGExpr node) {
        return node == ((BinNode) parent).getRight();
    }

    private boolean isOtherOperandAVarNode(CGExpr self) {
        if (isLeft(self)) {
            return ((BinNode) parent).getRight() instanceof VarNode;
        }
        if (isRight(self)) {
            return ((BinNode) parent).getLeft() instanceof VarNode;
        }
        // not reached
        return false;
    }

    private boolean inEQOrNERelation() {
        return getParentOperator() == BinNode.Op.EQ || getParentOperator() == BinNode.Op.NE;
    }

    private boolean inLessRelation() {
        return getParentOperator() == BinNode.Op.LT || getParentOperator() == BinNode.Op.LE;
    }

    private boolean inGreaterRelation() {
        return getParentOperator() == BinNode.Op.GT || getParentOperator() == BinNode.Op.GE;
    }
}
