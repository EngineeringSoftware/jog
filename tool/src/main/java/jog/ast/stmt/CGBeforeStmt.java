package jog.ast.stmt;

import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

/**
 * A special CGIfStmt.
 */
public class CGBeforeStmt extends CGIfStmt {

    public CGBeforeStmt(CGExpr before, CGStmt thenStmt) {
        super(before, thenStmt);
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
        return "before{" + super.toString() + "}";
    }
}
