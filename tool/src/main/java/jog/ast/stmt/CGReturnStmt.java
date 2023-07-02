package jog.ast.stmt;

import jog.ast.expr.CGExpr;
import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

public class CGReturnStmt extends CGStmt {

    private CGExpr expression;

    public CGReturnStmt(CGExpr expression) {
        this.expression = expression;
    }

    public CGExpr getExpression() {
        return expression;
    }

    public void setExpression(CGExpr expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "return " + expression + ";";
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
