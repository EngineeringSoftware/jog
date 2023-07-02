package jog.shadow;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.visitor.ExprReturnVisitor;
import jog.util.Z3Util;

import java.util.Map;

public class ConstantFoldingNodeZ3Builder extends ExprReturnVisitor<Expr> {

    private final Z3Solver z3Solver;
    private final Context ctx;
    private final int bitWidth;
    private final Map<CGExpr, Expr> astNodeToZ3ValueVar;

    public ConstantFoldingNodeZ3Builder(Z3Solver z3Solver, Map<CGExpr, Expr> astNodeToZ3ValueVar) {
        this.z3Solver = z3Solver;
        this.ctx = z3Solver.getContext();
        this.bitWidth = z3Solver.getBitWidth();
        this.astNodeToZ3ValueVar = astNodeToZ3ValueVar;
    }

    @Override
    public Expr visit(BinNode binNode) {
        Expr l = binNode.getLeft().accept(this);
        Expr r = binNode.getRight().accept(this);
        if (l == null || r == null) {
            return null;
        }
        return Z3Util.mkBVBinaryExpr(ctx, l, r, binNode.getOperator());
    }

    @Override
    public Expr visit(ConNode conNode) {
        return astNodeToZ3ValueVar.get(conNode);
    }

    @Override
    public Expr visit(VarNode varNode) {
        // should not reach
        return null;
    }

    @Override
    public Expr visit(LitNode litNode) {
        return Z3Util.mkBV(ctx, litNode.getValue(), bitWidth);
    }

    @Override
    public Expr visit(UnaryNode unaryNode) {
        Expr o = unaryNode.getOperand().accept(this);
        if (o == null) {
            return null;
        }
        return Z3Util.mkBVUnaryExpr(ctx, o, unaryNode.getOperator());
    }

    @Override
    public Expr visit(ConstantFoldingNode constantFoldingNode) {
        return constantFoldingNode.getExpr().accept(this);
    }

    @Override
    public Expr visit(CallNode callNode) {
        // should not reach
        return null;
    }

    @Override
    public Expr visit(TypeNode typeNode) {
        // should not reach
        return null;
    }

    @Override
    public Expr visit(OpNode opNode) {
        // should not reach
        return null;
    }
}
