package jog.ast.visitor;

import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGReturnStmt;
import jog.ast.expr.ConNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;

public interface VoidVisitor<A> {

    void visit(BinNode binNode, A arg);

    void visit(ConNode conNode, A arg);

    void visit(VarNode varNode, A arg);

    void visit(LitNode litNode, A arg);

    void visit(UnaryNode unaryNode, A arg);

    void visit(ConstantFoldingNode constantFoldingNode, A arg);

    void visit(CallNode callNode, A arg);

    void visit(TypeNode typeNode, A arg);

    void visit(OpNode opNode, A arg);

    void visit(CGBeforeStmt cgBeforeStmt, A arg);

    void visit(CGAfterStmt cgAfterStmt, A arg);

    void visit(CGBlockStmt cgBlockStmt, A arg);

    void visit(CGGeneralStmt cgGeneralStmt, A arg);

    void visit(CGIfStmt cgIfStmt, A arg);

    void visit(CGReturnStmt cgReturnStmt, A arg);

    void visit(CGTmpDeclStmt cgTmpDeclStmt, A arg);
}
