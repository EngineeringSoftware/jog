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

public interface ReturnVisitor<R> {

    R visit(BinNode binNode);

    R visit(ConNode conNode);
    
    R visit(VarNode varNode);

    R visit(LitNode litNode);

    R visit(UnaryNode unaryNode);

    R visit(ConstantFoldingNode constantFoldingNode);

    R visit(CallNode callNode);

    R visit(TypeNode typeNode);

    R visit(OpNode opNode);

    R visit(CGBeforeStmt cgBeforeStmt);

    R visit(CGAfterStmt cgAfterStmt);

    R visit(CGBlockStmt cgBlockStmt);

    R visit(CGGeneralStmt cgGeneralStmt);

    R visit(CGIfStmt cgIfStmt);

    R visit(CGReturnStmt cgReturnStmt);

    R visit(CGTmpDeclStmt cgTmpDeclStmt);
}
