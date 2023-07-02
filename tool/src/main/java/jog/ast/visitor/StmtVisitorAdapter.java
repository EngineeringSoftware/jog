package jog.ast.visitor;

import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;
import jog.ast.stmt.CGStmt;

public class StmtVisitorAdapter extends StmtVisitor {

    @Override
    public void visit(CGBeforeStmt cgBeforeStmt, Void arg) {
        cgBeforeStmt.getCondition().accept(this, arg);
        cgBeforeStmt.getThenStmt().accept(this, arg);
    }

    @Override
    public void visit(CGAfterStmt cgAfterStmt, Void arg) {
        cgAfterStmt.getExpression().accept(this, arg);
    }

    @Override
    public void visit(CGBlockStmt cgBlockStmt, Void arg) {
        for (CGStmt s : cgBlockStmt.getStmts()) {
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(CGGeneralStmt cgGeneralStmt, Void arg) {}

    @Override
    public void visit(CGIfStmt cgIfStmt, Void arg) {
        cgIfStmt.getCondition().accept(this, arg);
        cgIfStmt.getThenStmt().accept(this, arg);
        if (cgIfStmt.hasElseBranch()) {
            cgIfStmt.getElseStmt().accept(this, arg);
        }
    }

    @Override
    public void visit(CGReturnStmt cgReturnStmt, Void arg) {
        cgReturnStmt.getExpression().accept(this, arg);
    }

    @Override
    public void visit(CGTmpDeclStmt cgTmpDeclStmt, Void arg) {}
}
