package jog.ast.stmt;

import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

public class CGAfterStmt extends CGReturnStmt {

    public CGAfterStmt(CGExpr expression) {
        super(expression);
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
        return "after{" + super.toString() + "}";
    }
}
