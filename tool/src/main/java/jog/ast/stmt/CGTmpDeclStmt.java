package jog.ast.stmt;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

/**
 * The temporary variable declaration statement will be hidden from
 * code generation.
 */
public class CGTmpDeclStmt extends CGStmt {

    private String stmt;

    public CGTmpDeclStmt(String stmt) {
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
