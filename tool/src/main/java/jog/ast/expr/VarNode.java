package jog.ast.expr;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

public class VarNode extends IdNode {

    public VarNode(String identifier, ValType valType) {
        super(identifier, valType);
    }

    @Override
    public boolean isCon() {
        return false;
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
        return "VarNode{" +
                '\"' + getIdentifier() + '\"' +
                '}';
    }
}
