package jog.ast.stmt;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

/**
 * Wrap any string into a statement.
 */
public class CGGeneralStmt extends CGStmt {

    private String stmt;

    public CGGeneralStmt(String stmt) {
        this.stmt = stmt;
    }

    public String getStmt() {
        return stmt;
    }

    public void setStmt(String stmt) {
        this.stmt = stmt;
    }

    @Override
    public String toString() {
        return stmt;
    }

    @Override
    public <R> R accept(ReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <A> void accept(VoidVisitor<A> visitor, A arg) {
        visitor.visit(this, arg);
    }
}
