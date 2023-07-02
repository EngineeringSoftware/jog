package jog.ast.expr;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

public class BinNode extends CGExpr {

    public enum Op {
        SUB("-", "Op_Sub%s", NodeType.SUB_NODE),
        ADD("+", "Op_Add%s", NodeType.ADD_NODE),
        MUL("*", "Op_Mul%s", NodeType.MUL_NODE),
        DIV("/", "Op_Div%s", NodeType.DIV_NODE),
        MOD("%", "Op_Mod%s", NodeType.MOD_NODE),
        SHIFTL("<<", "Op_LShift%s", NodeType.SHIFTL_NODE),
        SHIFTR(">>", "Op_RShift%s", NodeType.SHIFTR_NODE),
        SHIFTUR(">>>", "Op_URShift%s", NodeType.SHIFTUR_NODE),
        ROTATE_RIGHT("Op_RotateRight", "Op_RotateRight", NodeType.ROTATE_RIGHT_NODE),
        ROTATE_LEFT("Op_RotateLeft", "Op_RotateLeft", NodeType.ROTATE_LEFT_NODE),
        BIN_AND("&", "Op_And%s", NodeType.BIN_AND_NODE),
        BIN_OR("|", "Op_Or%s", NodeType.BIN_OR_NODE),
        XOR("^", "Op_Xor%s", NodeType.XOR_NODE),
        MIN("min", "Op_Min%s", NodeType.MIN_NODE),
        MAX("max", "Op_Max%s", NodeType.MAX_NODE),
        /* The below operators have no corresponding NodeType (for
         * now). */
        LOGIC_AND("&&", null, null),
        LOGIC_OR("||", null, null),
        EQ("==", null, null),
        NE("!=", null, null),
        LT("<", null, null),
        LE("<=", null, null),
        GT(">", null, null),
        GE(">=", null, null);

        private final String name;
        private final String code;
        private final NodeType nodeType;

        Op(String name, String code, NodeType nodeType) {
            this.name = name;
            this.code = code;
            this.nodeType = nodeType;
        }

        public NodeType toNodeType() {
            return nodeType;
        }

        public String toCodeGen(ValType valType) {
            return String.format(code, valType.getTypeLetter());
        }

        public String asStr() {
            return name;
        }

        @Override
        public String toString() {
            return "Op{" +
                    "\"" + name + '\"' +
                    '}';
        }
    }

    public enum NodeType {
        SUB_NODE("Sub%sNode", "SUB"),
        ADD_NODE("Add%sNode", "ADD"),
        MUL_NODE("Mul%sNode", "MUL"),
        DIV_NODE("Div%sNode", "DIV"),
        MOD_NODE("Mod%sNode", "MOD"), // not defined in IRNode.java of JDK yet
        SHIFTL_NODE("LShift%sNode", "LSHIFT"),
        SHIFTR_NODE("RShift%sNode", "RSHIFT"),
        SHIFTUR_NODE("URShift%sNode", "URSHIFT"),
        ROTATE_RIGHT_NODE("RotateRightNode", "ROTATE_RIGHT"), // not defined in IRNode.java of JDK yet
        ROTATE_LEFT_NODE("RotateLeftNode", "ROTATE_LEFT"), // not defined in IRNode.java of JDK yet
        BIN_AND_NODE("And%sNode", "AND"),
        BIN_OR_NODE("Or%sNode", "OR"), // not defined in IRNode.java of JDK yet
        XOR_NODE("Xor%sNode", "XOR"),
        MIN_NODE("Min%sNode", "MIN"), // not defined in IRNode.java of JDK yet
        MAX_NODE("Max%sNode", "MAX"); // not defined in IRNode.java of JDK yet

        private final String code;
        private final String testCode;

        NodeType(String code, String testCode) {
            this.code = code;
            this.testCode = testCode;
        }

        public String toCodeGen(ValType valType) {
            return String.format(code, valType.getTypeLetter());
        }

        public String toTestCodeGen() {
            return testCode;
        }

        public String toCppFileName() {
            return String.format(code, "").toLowerCase();
        }

        @Override
        public String toString() {
            return "NodeType{" +
                    code +
                    '}';
        }
    }

    private final Op operator;
    private final NodeType nodeType;
    private CGExpr left;
    private CGExpr right;

    public BinNode(CGExpr left, CGExpr right, Op operator, ValType valType) {
        super(valType);
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.nodeType = operator.toNodeType();
    }

    public Op getOperator() {
        return operator;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public CGExpr getLeft() {
        return left;
    }

    public CGExpr getRight() {
        return right;
    }

    public void setLeft(CGExpr left) {
        this.left = left;
    }

    public void setRight(CGExpr right) {
        this.right = right;
    }

    @Override
    public <R> R accept(ReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <A> void accept(VoidVisitor<A> visitor, A arg) {
        visitor.visit(this, arg);
    }

    @Override
    public String toString() {
        return "BinNode{" +
                left +
                operator +
                right +
                '}';
    }
}
