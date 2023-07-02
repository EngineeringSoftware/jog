package jog.ast.stmt;

import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

public class CGIfStmt extends CGStmt {

    private CGExpr condition;
    private CGStmt thenStmt;
    private CGStmt elseStmt;

    public CGIfStmt(CGExpr condition, CGStmt thenStmt) {
        this(condition, thenStmt, null);
    }

    public CGIfStmt(CGExpr condition, CGStmt thenStmt, CGStmt elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public boolean hasElseBranch() {
        return elseStmt != null;
    }

    public CGExpr getCondition() {
        return condition;
    }

    public CGStmt getThenStmt() {
        return thenStmt;
    }

    public CGStmt getElseStmt() {
        return elseStmt;
    }


    public void setCondition(CGExpr condition) {
        this.condition = condition;
    }

    public void setThenStmt(CGStmt thenStmt) {
        this.thenStmt = thenStmt;
    }

    public void setElseStmt(CGStmt elseStmt) {
        this.elseStmt = elseStmt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("if (").append(condition).append(") ").append(thenStmt);
        if (elseStmt != null) {
            sb.append(" else ").append(elseStmt);
        }
        return sb.toString();
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
