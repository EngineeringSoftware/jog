package jog.ast.visitor;

import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;
import jog.ast.stmt.CGTmpDeclStmt;

public abstract class ExprReturnVisitor<R> implements ReturnVisitor<R> {

    @Override
    public R visit(CGBeforeStmt cgBeforeStmt) {
        return null;
    }

    @Override
    public R visit(CGAfterStmt cgAfterStmt) {
        return null;
    }

    @Override
    public R visit(CGBlockStmt cgBlockStmt) {
        return null;
    }

    @Override
    public R visit(CGGeneralStmt cgGeneralStmt) {
        return null;
    }

    @Override
    public R visit(CGIfStmt cgIfStmt) {
        return null;
    }

    @Override
    public R visit(CGReturnStmt cgReturnStmt) {
        return null;
    }

    @Override
    public R visit(CGTmpDeclStmt cgTmpDeclStmt) {
        return null;
    }
}
