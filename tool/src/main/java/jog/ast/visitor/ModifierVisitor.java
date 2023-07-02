package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;
import jog.ast.stmt.CGStmt;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.Node;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Visitor that modifies the AST tree. One should extend this visitor
 * to make changes to some specific nodes. Returns null will remove
 * the node.
 */
public class ModifierVisitor implements ReturnVisitor<Node> {

    @Override
    public Node visit(BinNode binNode) {
        CGExpr left = (CGExpr) binNode.getLeft().accept(this);
        CGExpr right = (CGExpr) binNode.getRight().accept(this);
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        binNode.setLeft(left);
        binNode.setRight(right);
        return binNode;
    }

    @Override
    public Node visit(ConNode conNode) {
        return conNode;
    }

    @Override
    public Node visit(VarNode varNode) {
        return varNode;
    }

    @Override
    public Node visit(LitNode litNode) {
        return litNode;
    }

    @Override
    public Node visit(UnaryNode unaryNode) {
        CGExpr operand = (CGExpr) unaryNode.getOperand().accept(this);
        if (operand == null) {
            return null;
        }
        unaryNode.setOperand(operand);
        return unaryNode;
    }

    @Override
    public Node visit(ConstantFoldingNode constantFoldingNode) {
        CGExpr expr = (CGExpr) constantFoldingNode.getExpr().accept(this);
        if (expr == null) {
            return null;
        }
        constantFoldingNode.setExpr(expr);
        return constantFoldingNode;
    }

    @Override
    public Node visit(CallNode callNode) {
        List<CGExpr> arguments = callNode.getArguments().stream()
                .map(a -> (CGExpr) a.accept(this))
                .collect(Collectors.toList());
        callNode.updateArguments(arguments);
        return callNode;
    }

    @Override
    public Node visit(TypeNode typeNode) {
        return typeNode;
    }

    @Override
    public Node visit(OpNode opNode) {
        return opNode;
    }

    @Override
    public Node visit(CGBeforeStmt cgBeforeStmt) {
        CGExpr condition = (CGExpr) cgBeforeStmt.getCondition().accept(this);
        CGStmt thenStmt = (CGStmt) cgBeforeStmt.getThenStmt().accept(this);
        if (condition == null || thenStmt == null) {
            return null;
        }
        cgBeforeStmt.setCondition(condition);
        cgBeforeStmt.setThenStmt(thenStmt);
        return cgBeforeStmt;
    }

    @Override
    public Node visit(CGAfterStmt cgAfterStmt) {
        return visit((CGReturnStmt) cgAfterStmt);
    }

    @Override
    public Node visit(CGBlockStmt cgBlockStmt) {
        List<CGStmt> stmts = cgBlockStmt.getStmts().stream()
                .map(e -> (CGStmt) e.accept(this))
                .collect(Collectors.toList());
        cgBlockStmt.updateStmts(stmts);
        return cgBlockStmt;
    }

    @Override
    public Node visit(CGGeneralStmt cgGeneralStmt) {
        return cgGeneralStmt;
    }

    @Override
    public Node visit(CGIfStmt cgIfStmt) {
        CGExpr condition = (CGExpr) cgIfStmt.getCondition().accept(this);
        CGStmt thenStmt = (CGStmt) cgIfStmt.getThenStmt().accept(this);
        if (condition == null || thenStmt == null) {
            return null;
        }
        CGStmt elseStmt = cgIfStmt.hasElseBranch() ?
                (CGStmt) cgIfStmt.getElseStmt().accept(this) :
                null;
        cgIfStmt.setCondition(condition);
        cgIfStmt.setThenStmt(thenStmt);
        cgIfStmt.setElseStmt(elseStmt);
        return cgIfStmt;
    }

    @Override
    public Node visit(CGReturnStmt cgReturnStmt) {
        CGExpr expr = (CGExpr) cgReturnStmt.getExpression().accept(this);
        if (expr == null) {
            return null;
        }
        cgReturnStmt.setExpression(expr);
        return cgReturnStmt;
    }

    @Override
    public Node visit(CGTmpDeclStmt cgTmpDeclStmt) {
        return cgTmpDeclStmt;
    }
}
