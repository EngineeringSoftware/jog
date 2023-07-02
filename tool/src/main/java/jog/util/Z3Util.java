package jog.util;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import jog.ast.expr.BinNode;
import jog.ast.expr.UnaryNode;

@SuppressWarnings("unchecked")
public class Z3Util {

    public static Expr mkBVBinaryExpr(Context ctx, Expr l, Expr r, BinNode.Op binOp) {
        switch (binOp) {
        case ADD:
            return ctx.mkBVAdd(l, r);
        case SUB:
            return ctx.mkBVSub(l, r);
        case MUL:
            return ctx.mkBVMul(l, r);
        case DIV:
            return ctx.mkBVSDiv(l, r);
        case MOD:
            return ctx.mkBVSMod(l, r);
        case SHIFTL:
            return ctx.mkBVSHL(l, r);
        case SHIFTR:
            return ctx.mkBVASHR(l, r);
        case SHIFTUR:
            return ctx.mkBVLSHR(l, r);
        case ROTATE_RIGHT:
            return ctx.mkBVRotateRight(l, r);
        case ROTATE_LEFT:
            return ctx.mkBVRotateLeft(l, r);
        case BIN_AND:
            return ctx.mkBVAND(l, r);
        case BIN_OR:
            return ctx.mkBVOR(l, r);
        case XOR:
            return ctx.mkBVXOR(l, r);
        case MIN:
            return ctx.mkITE(ctx.mkBVSLT(l, r), l, r);
        case MAX:
            return ctx.mkITE(ctx.mkBVSGT(l, r), l, r);
        case LOGIC_AND:
            return ctx.mkAnd(l, r);
        case LOGIC_OR:
            return ctx.mkOr(l, r);
        case EQ:
            return ctx.mkEq(l, r);
        case NE:
            return ctx.mkNot(ctx.mkEq(l, r));
        case LT:
            return ctx.mkBVSLT(l, r);
        case LE:
            return ctx.mkBVSLE(l, r);
        case GT:
            return ctx.mkBVSGT(l, r);
        case GE:
            return ctx.mkBVSGE(l, r);
        default:
            throw new RuntimeException("Unsupported operator in Z3 solver: " + binOp);
        }
    }

    public static Expr mkBVUnaryExpr(Context ctx, Expr operand, UnaryNode.Op operator) {
        switch (operator) {
        case MINUS:
            return ctx.mkBVNeg(operand);
        case BITWISE_NOT:
            return ctx.mkBVNot(operand);
        default:
            throw new RuntimeException("Unsupported operator in Z3 solver: " + operator);
        }
    }

    public static Expr mkBV(Context ctx, Number value, int size) {
        if (value instanceof Integer)
            return ctx.mkBV((Integer) value, size);
        else if (value instanceof Long)
            return ctx.mkBV((Long) value, size);
        else
            throw new RuntimeException("Unsupported number type: " + value.getClass());
    }
}
