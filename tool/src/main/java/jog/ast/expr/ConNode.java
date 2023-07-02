package jog.ast.expr;

import jog.ast.nodetype.NodeThatIsConstant;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

public class ConNode extends IdNode implements NodeThatIsConstant {

    public ConNode(String identifier, ValType valType) {
        super(identifier, valType);
    }

    @Override
    public boolean isCon() {
        return true;
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
        return getIdentifier();
    }

    @Override
    public String toString() {
        return "ConNode{" +
                '\"' + getIdentifier() + '\"' +
                '}';
    }
}
