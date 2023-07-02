package jog.ast.expr.lib;

import jog.api.Lib;
import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

/**
 * Wrap Lib.Operator as a node.
 */
public class TypeNode extends CGExpr {

    private Lib.Type type;

    public TypeNode(Lib.Type type, ValType valType) {
        super(valType);
        this.type = type;
    }

    public Lib.Type getType() {
        return type;
    }

    public void setType(Lib.Type type) {
        this.type = type;
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
        return "TypeNode{" +
                type +
                '}';
    }
}
