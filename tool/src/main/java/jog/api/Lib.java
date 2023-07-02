package jog.api;

import jog.ast.expr.BinNode;

public class Lib {

    public enum Operator {
        OP_ROTATE_RIGHT(BinNode.Op.ROTATE_RIGHT),
        OP_ROTATE_LEFT(BinNode.Op.ROTATE_LEFT);

        private final BinNode.Op binOp;
        Operator(BinNode.Op binOp) {
            this.binOp = binOp;
        }

        public BinNode.Op toBinOp() {
            return binOp;
        }
    }

    public enum Type {
        TOP, /** Corresponds to {@code Type::TOP}. */
        BOTTOM /** Corresponds to {@code Type::BOTTOM}. */
    }

    /** Corresponds to {@code phase->type(node)->isa_int()->_hi}. */
    public static int getHi(int node) {
        return 0;
    }

    /** Corresponds to {@code phase->type(node)->isa_long()->_hi}. */
    public static long getHi(long node) {
        return 0;
    }

    /** Corresponds to {@code phase->type(node)->isa_int()->_lo}. */
    public static int getLo(int node) {
        return 0;
    }

    /** Corresponds to {@code phase->type(node)->isa_long()->_lo}. */
    public static long getLo(long node) {
        return 0;
    }

    public static Type getType(int node) {
        return Type.TOP;
    }

    public static Type getType(long node) {
        return Type.TOP;
    }

    /** Corresponds to {@code ok_to_convert(n1, n2)}. */
    public static boolean okToConvert(int n1, int n2) {
        return false;
    }

    public static boolean okToConvert(long n1, long n2) {
        return false;
    }

    /** Corresponds to {@code node->outcnt()}. */
    public static int outcnt(int node) {
        return 0;
    }

    public static int outcnt(long node) {
        return 0;
    }

    /** Corresponds to
     * {@code Matcher::match_rule_supported(op)}. */
    public static boolean matchRuleSupported(Operator op) {
        return false;
    }

    /*
     * Operations that cannot be directly expressed in Java syntax
     * but needs to call special methods.
     *
     * Integer.rotateRight
     * Integer.rotateLeft
     * Math.max
     * Math.min
     */
}
