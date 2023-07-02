package jog.ast.expr;

import jog.ast.nodetype.LeafNode;
import jog.ast.nodetype.NodeThatIsConstant;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

public class ConstantFoldingNode extends CGExpr
        implements NodeThatIsConstant, LeafNode { // TODO: a special leaf node?

    private CGExpr expr;

    public ConstantFoldingNode(CGExpr expr, ValType valType) {
        super(valType);
        this.expr = expr;
    }

    public CGExpr getExpr() {
        return expr;
    }

    public void setExpr(CGExpr expr) {
        this.expr = expr;
    }

    @Override
    public String asStr() {
        // Should not be invoked
        return null;
    }

    @Override
    public <R> R accept(ReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <A> void accept(VoidVisitor<A> visitor, A arg) {
        visitor.visit(this, arg);
    }

    @Override
    public String toString() {
        return "ConstantFoldingNode{" +
                expr +
                '}';
    }
}
