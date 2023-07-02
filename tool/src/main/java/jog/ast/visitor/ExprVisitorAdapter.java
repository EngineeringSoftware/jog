package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.TypeNode;

public class ExprVisitorAdapter extends ExprVisitor {

    @Override
    public void visit(BinNode binNode, Void arg) {
        binNode.getLeft().accept(this, arg);
        binNode.getRight().accept(this, arg);
    }

    @Override
    public void visit(ConNode conNode, Void arg) {}

    @Override
    public void visit(VarNode varNode, Void arg) {}

    @Override
    public void visit(LitNode litNode, Void arg) {}

    @Override
    public void visit(UnaryNode unaryNode, Void arg) {
        unaryNode.getOperand().accept(this, arg);
    }

    @Override
    public void visit(ConstantFoldingNode constantFoldingNode, Void arg) {
        constantFoldingNode.getExpr().accept(this, arg);
    }

    @Override
    public void visit(CallNode callNode, Void arg) {
        callNode.getArguments().forEach(a -> a.accept(this, arg));
    }

    @Override
    public void visit(TypeNode typeNode, Void arg) {}

    @Override
    public void visit(OpNode opNode, Void arg) {}
}
