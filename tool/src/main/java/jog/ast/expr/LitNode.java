package jog.ast.expr;

import jog.ast.nodetype.LeafNode;
import jog.ast.nodetype.NodeThatIsConstant;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

/**
 * A number literal.
 */
public class LitNode extends CGExpr implements NodeThatIsConstant, LeafNode {

    private final Number value;

    public LitNode(Number value, ValType valType) {
        super(valType);
        this.value = value;
    }

    public Number getValue() {
        return value;
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
    public String asStr() {
        return value.toString();
    }

    @Override
    public String toString() {
        return "LitNode{" +
                value +
                '}';
    }
}
