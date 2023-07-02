package jog.ast.expr.lib;

import jog.api.Lib;
import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

/**
 * Wrap Lib.Operator as a node.
 */
public class OpNode extends CGExpr {

    private Lib.Operator operator;

    public OpNode(Lib.Operator operator, ValType valType) {
        super(valType);
        this.operator = operator;
    }

    public Lib.Operator getOperator() {
        return operator;
    }

    public void setOperator(Lib.Operator operator) {
        this.operator = operator;
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
        return "OpNode{" +
                operator +
                '}';
    }
}
