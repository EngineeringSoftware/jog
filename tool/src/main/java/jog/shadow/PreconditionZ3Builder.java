package jog.shadow;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import jog.api.Lib;
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

@SuppressWarnings("unchecked")
public class PreconditionZ3Builder extends ExprReturnVisitor<Expr> {

    private final Z3Solver z3Solver;
    private final Map<CGExpr, Expr> astNodeToZ3ShapeVar;
    private final Map<CGExpr, Expr> astNodeToZ3ValueVar;

    private final Context ctx;
    private final FuncDecl valDecl;
    private final int bitWidth;

    // TODO: We have to ignore most of lib calls when compositing
    //   two patterns otherwise we would see new node in preconditons,
    //   which throws NullPointerException. See
    //   visit(CallNode callNode).
    private final boolean ignoreLibCall;

    public PreconditionZ3Builder(
            Z3Solver z3Solver,
            Map<CGExpr, Expr> astNodeToZ3ShapeVar,
            Map<CGExpr, Expr> astNodeToZ3ValueVar) {
        this(z3Solver, astNodeToZ3ShapeVar, astNodeToZ3ValueVar, false);
    }

    public PreconditionZ3Builder(
            Z3Solver z3Solver,
            Map<CGExpr, Expr> astNodeToZ3ShapeVar,
            Map<CGExpr, Expr> astNodeToZ3ValueVar,
            boolean ignoreLibCall) {
        this.z3Solver = z3Solver;
        this.ctx = z3Solver.getContext();
        this.bitWidth = z3Solver.getBitWidth();
        this.valDecl = z3Solver.getValDecl();
        this.astNodeToZ3ShapeVar = astNodeToZ3ShapeVar;
        this.astNodeToZ3ValueVar = astNodeToZ3ValueVar;
        this.ignoreLibCall = ignoreLibCall;
    }

    @Override
    public Expr visit(BinNode binNode) {
        Expr left = binNode.getLeft().accept(this);
        Expr right = binNode.getRight().accept(this);
        if (left == null || right == null) {
            return null;
        }
        return Z3Util.mkBVBinaryExpr(ctx, left, right, binNode.getOperator());
    }

    @Override
    public Expr visit(ConNode conNode) {
        return astNodeToZ3ValueVar.get(conNode);
    }

    @Override
    public Expr visit(VarNode varNode) {
        return astNodeToZ3ValueVar.get(varNode);
    }

    @Override
    public Expr visit(LitNode litNode) {
        return Z3Util.mkBV(ctx, litNode.getValue(), bitWidth);
    }

    @Override
    public Expr visit(UnaryNode unaryNode) {
        Expr operand = unaryNode.getOperand().accept(this);
        if (operand == null) {
            return null;
        }
        return Z3Util.mkBVUnaryExpr(ctx, operand, unaryNode.getOperator());
    }

    @Override
    public Expr visit(CallNode callNode) {
        CallNode.Name name = callNode.getName();
        switch (name) {
        case OK_TO_CONVERT: {
            if (ignoreLibCall) {
                break;
            } else {
                CGExpr left = callNode.getArguments().get(0);
                Expr l = astNodeToZ3ShapeVar.get(left);
                CGExpr right = callNode.getArguments().get(1);
                Expr r = astNodeToZ3ShapeVar.get(right);
                if (l == null) {
                    // should be only literal
                    throw new RuntimeException("No z3 variable for node: " + left);
                }
                if (r == null) {
                    throw new RuntimeException("No z3 variable for node: " + right);
                }
                return ctx.mkApp(z3Solver.getLibCallDecls().get(name), l, r);
            }
        }
        case OUT_CNT: {
            if (ignoreLibCall) {
                break;
            } else {
                Expr a = astNodeToZ3ShapeVar.get(callNode.getArguments().get(0));
                return ctx.mkApp(z3Solver.getLibCallDecls().get(name), a);
            }
        }
        case MATCH_RULE_SUPPORTED: {
            if (ignoreLibCall) {
                break;
            } else {
                OpNode opNode = (OpNode) callNode.getArguments().get(0);
                Lib.Operator op = opNode.getOperator();
                Expr a = z3Solver.getLibOpToEnumExpr().get(op);
                return ctx.mkApp(z3Solver.getLibCallDecls().get(name), a);
            }
        }
        case GET_TYPE: {
            if (ignoreLibCall) {
                break;
            } else {
                CGExpr aNode = callNode.getArguments().get(0);
                Expr a = astNodeToZ3ShapeVar.get(aNode);
                return ctx.mkApp(z3Solver.getLibCallDecls().get(name), a);
            }
        }
        case GET_HI:
        case GET_LO:
            // TODO: do not know how to encode; for now let's just
            //  use the value of the variable.
            CGExpr aNode = callNode.getArguments().get(0);
            return astNodeToZ3ValueVar.get(aNode);
        default:
            throw new RuntimeException("Unsupported call name when building z3 formulas from preconditions: " + name);
        }
        return null;
    }

    @Override
    public Expr visit(TypeNode typeNode) {
        return z3Solver.getLibTypeToEnumExpr().get(typeNode.getType());
    }

    @Override
    public Expr visit(ConstantFoldingNode constantFoldingNode) {
        throw new RuntimeException("Not expected ConstantFoldingNode in a precondition: " + constantFoldingNode);
    }

    @Override
    public Expr visit(OpNode opNode) {
        throw new RuntimeException("Not expected OpNode in a precondition: " + opNode);
    }
}
