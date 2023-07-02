package jog.ast.visitor;

import jog.ast.Node;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;
import jog.ast.stmt.CGStmt;
import jog.ast.stmt.CGTmpDeclStmt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CloneVisitor implements ReturnVisitor<Node> {

    // Some nodes could have multiple parents, so we want to reuse
    // cloned nodes.
    private final Map<Node, Node> clonesOfNode = new HashMap<>();

    public Map<Node, Node> getClonesOfNode() {
        return clonesOfNode;
    }

    @Override
    public Node visit(BinNode binNode) {
        if (clonesOfNode.containsKey(binNode)) {
            return clonesOfNode.get(binNode);
        }
        CGExpr left = cloneNode(binNode.getLeft());
        CGExpr right = cloneNode(binNode.getRight());
        BinNode clone = new BinNode(left, right, binNode.getOperator(), binNode.getValType());
        return saveCloneAndReturn(binNode, clone);
    }

    @Override
    public Node visit(ConNode conNode) {
        if (clonesOfNode.containsKey(conNode)) {
            return clonesOfNode.get(conNode);
        }
        ConNode clone = new ConNode(conNode.getIdentifier(), conNode.getValType());
        return saveCloneAndReturn(conNode, clone);
    }

    @Override
    public Node visit(VarNode varNode) {
        if (clonesOfNode.containsKey(varNode)) {
            return clonesOfNode.get(varNode);
        }
        VarNode clone = new VarNode(varNode.getIdentifier(), varNode.getValType());
        return saveCloneAndReturn(varNode, clone);
    }

    @Override
    public Node visit(LitNode litNode) {
        if (clonesOfNode.containsKey(litNode)) {
            return clonesOfNode.get(litNode);
        }
        LitNode clone = new LitNode(litNode.getValue(), litNode.getValType());
        return saveCloneAndReturn(litNode, clone);
    }

    @Override
    public Node visit(UnaryNode unaryNode) {
        if (clonesOfNode.containsKey(unaryNode)) {
            return clonesOfNode.get(unaryNode);
        }
        CGExpr operand = cloneNode(unaryNode.getOperand());
        UnaryNode clone = new UnaryNode(operand, unaryNode.getOperator(), unaryNode.getValType());
        return saveCloneAndReturn(unaryNode, clone);
    }

    @Override
    public Node visit(ConstantFoldingNode constantFoldingNode) {
        if (clonesOfNode.containsKey(constantFoldingNode)) {
            return clonesOfNode.get(constantFoldingNode);
        }
        CGExpr expr = cloneNode(constantFoldingNode.getExpr());
        ConstantFoldingNode clone = new ConstantFoldingNode(expr, constantFoldingNode.getValType());
        return saveCloneAndReturn(constantFoldingNode, clone);
    }

    @Override
    public Node visit(CallNode callNode) {
        if (clonesOfNode.containsKey(callNode)) {
            return clonesOfNode.get(callNode);
        }
        List<CGExpr> arguments = callNode.getArguments().stream()
                .map(this::cloneNode)
                .collect(Collectors.toList());
        CallNode clone = new CallNode(callNode.getName(), arguments, callNode.getValType());
        return saveCloneAndReturn(callNode, clone);
    }

    @Override
    public Node visit(TypeNode typeNode) {
        if (clonesOfNode.containsKey(typeNode)) {
            return clonesOfNode.get(typeNode);
        }
        TypeNode clone = new TypeNode(typeNode.getType(), typeNode.getValType());
        return saveCloneAndReturn(typeNode, clone);
    }

    @Override
    public Node visit(OpNode opNode) {
        if (clonesOfNode.containsKey(opNode)) {
            return clonesOfNode.get(opNode);
        }
        OpNode clone = new OpNode(opNode.getOperator(), opNode.getValType());
        return saveCloneAndReturn(opNode, clone);
    }

    /* Statement nodes should not be shared by multiple parents. */

    @Override
    public Node visit(CGBeforeStmt cgBeforeStmt) {
        return visit((CGIfStmt) cgBeforeStmt);
    }

    @Override
    public Node visit(CGAfterStmt cgAfterStmt) {
        return visit((CGReturnStmt) cgAfterStmt);
    }

    @Override
    public Node visit(CGBlockStmt cgBlockStmt) {
        List<CGStmt> stmts = cgBlockStmt.getStmts().stream()
                .map(this::cloneNode)
                .collect(Collectors.toList());
        return new CGBlockStmt(stmts);
    }

    @Override
    public Node visit(CGGeneralStmt cgGeneralStmt) {
        return new CGGeneralStmt(cgGeneralStmt.getStmt());
    }

    @Override
    public Node visit(CGIfStmt cgIfStmt) {
        CGExpr condition = cloneNode(cgIfStmt.getCondition());
        CGStmt thenStmt = cloneNode(cgIfStmt.getThenStmt());
        CGStmt elseStmt = cloneNode(cgIfStmt.getElseStmt());
        return new CGIfStmt(condition, thenStmt, elseStmt);
    }

    @Override
    public Node visit(CGReturnStmt cgReturnStmt) {
        CGExpr expr = cgReturnStmt.getExpression();
        return new CGReturnStmt(expr);
    }

    @Override
    public Node visit(CGTmpDeclStmt cgTmpDeclStmt) {
        return new CGTmpDeclStmt(cgTmpDeclStmt.getStmt());
    }

    @SuppressWarnings("unchecked")
    private <T extends Node> T cloneNode(T node) {
        if (node == null) {
            return null;
        }
        return (T) node.accept(this);
    }

    private Node saveCloneAndReturn(Node node, Node clone) {
        clonesOfNode.put(node, clone);
        return clone;
    }
}
