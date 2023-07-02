package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.nodetype.NodeThatIsConstant;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class AfterVisitor extends ExprVisitorAdapter {

    /**
     * Will be reset to {@code true} after the first use, because we
     * only want to ignore the root node.
     * See {@link CodeGenUtil#wrapPhaseTransform(String)}.
     */
    private boolean wrapPhaseTransform = false;

    private final Deque<String> stack = new ArrayDeque<>();
    private final Deque<Boolean> isConstant = new ArrayDeque<>();

    private final Map<CGExpr, String> nodeToCodeGen;

    public AfterVisitor(Map<CGExpr, String> nodeToCodeGen) {
        this.nodeToCodeGen = nodeToCodeGen;
    }

    public String getResult() {
        return stack.peek();
    }

    @Override
    public void visit(BinNode binNode, Void arg) {
        CGExpr left = binNode.getLeft();
        CGExpr right = binNode.getRight();
        boolean wrapThisTime = wrapPhaseTransform;
        if (!wrapPhaseTransform) {
            wrapPhaseTransform = true;
        }
        left.accept(this, arg);
        String lCode = stack.pop();
        right.accept(this, arg);
        String rCode = stack.pop();
        ValType valType = binNode.getValType();
        String code = CodeGenUtil.makeNewBinNode(lCode, rCode, binNode, valType);
        if (wrapThisTime) {
            code = CodeGenUtil.wrapPhaseTransform(code);
        }
        stack.push(code);
    }

    @Override
    public void visit(ConNode conNode, Void arg) {
        stack.push(nodeToCodeGen.get(conNode));
        isConstant.push(true);
    }

    @Override
    public void visit(VarNode varNode, Void arg) {
        stack.push(nodeToCodeGen.get(varNode));
        isConstant.push(false);
    }

    @Override
    public void visit(LitNode litNode, Void arg) {
        String code = nodeToCodeGen.getOrDefault(litNode,
                CodeGenUtil.makeConFromLiteral(litNode.asStr(), litNode.getValType()));
        stack.push(code);
        isConstant.push(true);
    }

    @Override
    public void visit(UnaryNode unaryNode, Void arg) {
        // not expected as we have all unary nodes in part of constant
        // folding, for now.
        throw new RuntimeException(
                "Not expected to see a unary node in after. The node is: " + unaryNode);
    }

    @Override
    public void visit(ConstantFoldingNode constantFoldingNode, Void arg) {
        String code = constantFoldingNode.accept(new ExprReturnVisitor<>() {

            private Deque<Boolean> inBinOrUnary = new ArrayDeque<>();
            private Deque<Boolean> inAddOrSubOrMul = new ArrayDeque<>();

            @Override
            public String visit(BinNode binNode) {
                BinNode.Op operator = binNode.getOperator();
                boolean isSelfAddOrSubOrMul =
                        operator == BinNode.Op.ADD
                                || operator == BinNode.Op.SUB
                                || operator == BinNode.Op.MUL;
                inAddOrSubOrMul.push(isSelfAddOrSubOrMul);
                inBinOrUnary.push(true);
                String l = binNode.getLeft().accept(this);
                String r = binNode.getRight().accept(this);
                inAddOrSubOrMul.pop();
                inBinOrUnary.pop();
                if (l == null || r == null) {
                    return null;
                }
                String result = CodeGenUtil.evaluateBinaryExpr(l, r, operator);
                return wrapParenthesesWhenNecessary(result, isSelfAddOrSubOrMul);
            }

            @Override
            public String visit(ConNode conNode) {
                return conNode.asStr();
            }

            @Override
            public String visit(LitNode litNode) {
                String code = litNode.asStr();
                if (inAddOrSubOrMul.peek()) {
                    ValType valType = litNode.getValType();
                    code = CodeGenUtil.makeCastToJIntOrLong(code, valType);
                }
                return code;
            }

            @Override
            public String visit(UnaryNode unaryNode) {
                UnaryNode.Op operator = unaryNode.getOperator();
                inAddOrSubOrMul.push(false);
                inBinOrUnary.push(true);
                String operandCode = unaryNode.getOperand().accept(this);
                inAddOrSubOrMul.pop();
                inBinOrUnary.pop();
                String result = CodeGenUtil.evaluateUnaryExpr(operandCode, operator);
                return wrapParenthesesWhenNecessary(result, false);
            }

            @Override
            public String visit(VarNode varNode) {
                throw new RuntimeException("Not expected in ConstantFoldingNode: "  + varNode);
            }

            @Override
            public String visit(ConstantFoldingNode constantFoldingNode) {
                // Assume there is no nested constant folding node.
                return constantFoldingNode.getExpr().accept(this);
            }

            @Override
            public String visit(CallNode callNode) {
                throw new RuntimeException("Not expected in ConstantFoldingNode: "  + callNode);
            }

            @Override
            public String visit(TypeNode typeNode) {
                throw new RuntimeException("Not expected in ConstantFoldingNode: "  + typeNode);
            }

            @Override
            public String visit(OpNode opNode) {
                throw new RuntimeException("Not expected in ConstantFoldingNode: "  + opNode);
            }

            private String wrapParenthesesWhenNecessary(String code, boolean isSelfAddOrSubOrMul) {
                if (!isSelfAddOrSubOrMul
                        && !inBinOrUnary.isEmpty() && inBinOrUnary.peek()
                        && !inAddOrSubOrMul.isEmpty() && !inAddOrSubOrMul.peek()) {
                    // wrap parentheses when it is not add/sub/mul
                    // itself and (it is in a binary
                    // expression that is not add/sub/mul or in any
                    // unary expression)
                    code = CodeGenUtil.wrapParentheses(code);
                }
                return code;
            }
        });
        stack.push(CodeGenUtil.makeConFromLiteral(code, constantFoldingNode.getValType()));
    }

    /*--------------------------------------------------------------*/

    private String foldConstant(NodeThatIsConstant root) {
        if (!(root instanceof ConstantFoldingNode)) {
            // which is ConNode or LitNode
            return root.asStr();
        }
        CGExpr expr = ((ConstantFoldingNode) root).getExpr();
        String code;
        if (expr instanceof BinNode) {
            BinNode n = (BinNode) expr;
            code = foldConstant((NodeThatIsConstant) n.getLeft(), (NodeThatIsConstant) n.getRight(), n.getOperator());
        } else if (expr instanceof UnaryNode) {
            UnaryNode n = (UnaryNode) expr;
            code = foldConstant((NodeThatIsConstant) n.getOperand(), n.getOperator());
        } else {
            throw new RuntimeException("Unexpected node in ConstantFoldingNode: " + expr);
        }
        return code;
    }

    /**
     * Fold binary expressions.
     */
    private String foldConstant(NodeThatIsConstant left, NodeThatIsConstant right, BinNode.Op operator) {
        String l = foldConstant(left);
        String r = foldConstant(right);
        if (operator == BinNode.Op.ADD
                || operator == BinNode.Op.SUB
                || operator == BinNode.Op.MUL) {
            l = castLiteral(left, l);
            r = castLiteral(right, r);
        }
        return CodeGenUtil.evaluateBinaryExpr(l, r, operator);
    }

    /**
     * Fold unary expressions.
     */
    private String foldConstant(NodeThatIsConstant operand, UnaryNode.Op operator) {
        String constantCode = foldConstant(operand);
        if (operand instanceof ConstantFoldingNode) {
            CGExpr expr = ((ConstantFoldingNode) operand).getExpr();
            if (expr instanceof BinNode || expr instanceof UnaryNode) {
                // wrap parentheses
                constantCode = "(" + constantCode + ")";
            }
        }
        return CodeGenUtil.evaluateUnaryExpr(constantCode, operator);
    }

    private static String castLiteral(NodeThatIsConstant node, String code) {
        if (node instanceof LitNode) {
            ValType valType = ((LitNode) node).getValType();
            return CodeGenUtil.makeCastToJIntOrLong(code, valType);
        }
        return code;
    }
}
