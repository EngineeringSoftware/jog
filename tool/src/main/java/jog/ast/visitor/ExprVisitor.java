package jog.ast.visitor;

import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;

/**
 * Abstract visitor that should only visit expression nodes, doing
 * nothing for statement nodes.
 */
public abstract class ExprVisitor implements VoidVisitor<Void> {

    @Override
    public void visit(CGBeforeStmt cgBeforeStmt, Void arg) {}

    @Override
    public void visit(CGAfterStmt cgAfterStmt, Void arg) {}

    @Override
    public void visit(CGBlockStmt cgBlockStmt, Void arg) {}

    @Override
    public void visit(CGGeneralStmt cgGeneralStmt, Void arg) {}

    @Override
    public void visit(CGIfStmt cgIfStmt, Void arg) {}

    @Override
    public void visit(CGReturnStmt cgReturnStmt, Void arg) {}

    @Override
    public void visit(CGTmpDeclStmt cgTmpDeclStmt, Void arg) {}
}
