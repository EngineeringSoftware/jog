package jog.ast.visitor;

import jog.api.Lib;
import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.UnaryNode;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CodeGenUtil {

    public enum ValType {
        INT("int", "Integer"),
        LONG("long", "Long");

        private String typeName;
        private String boxedTypeName;

        ValType(String typeName, String boxedTypeName) {
            this.typeName = typeName;
            this.boxedTypeName = boxedTypeName;
        }

        public static ValType of(String type) {
            switch (type) {
            case "int":
                return INT;
            case "long":
                return LONG;
            default:
                throw new RuntimeException("Unexpected val type: " + type);
            }
        }

        /**
         * int or long.
         */
        public String getTypeName() {
            return typeName;
        }

        public String getBoxedTypeName() {
            return boxedTypeName;
        }

        /**
         * isa_int() or isa_long().
         */
        public String getCastToTypeMethodCall() {
            return "isa_" + getTypeName() + "()";
        }

        /**
         * jint or jlong.
         */
        public String getJTypeName() {
            return "j" + getTypeName();
        }

        /**
         * TypeInt::INT or TypeLong:LONG.
         */
        public String getTypeIntOrLong() {
            return "Type" + getCapitalizedTypeName() + "::" + name();
        }

        /**
         * Op_ConI or Op_ConL.
         */
        public String getOpCon() {
            return "Op_Con" + getTypeLetter();
        }

        /**
         * I or L.
         */
        public String getTypeLetter() {
            return name().substring(0, 1);
        }

        /**
         * Int or Long.
         */
        public String getCapitalizedTypeName() {
            return getTypeLetter() + getTypeName().substring(1);
        }
    }

    private static final Map<Number, String> LITERALS = Map.of(
            0, "TypeInt::ZERO",
            1, "TypeInt::ONE",
            -1, "TypeInt::MINUS_1",
            0L, "TypeLong::ZERO",
            1L, "TypeLong::ONE",
            -1L, "TypeLong::MINUS_1"
    );

    private static final Map<Lib.Type, String> LIB_TYPE_TO_TYPE = Map.of(
            Lib.Type.TOP, "Type::TOP"
    );

    public static String makeCall(CallNode node, List<String> arguments, ValType type) {
        return makeCall(node.getName(), arguments, type);
    }

    public static String makeCall(CallNode.Name name, List<String> arguments, ValType type) {
        StringJoiner sj = new StringJoiner(", ");
        arguments.forEach(sj::add);
        switch (name) {
        case GET_HI:
            return makeGetHigh(sj.toString(), type); // should be only one arg
        case GET_LO:
            return makeGetLow(sj.toString(), type); // should be only one arg
        case GET_TYPE:
            return makeType(sj.toString()); // should be only one arg
        case OK_TO_CONVERT:
            // should be only two args
            return "ok_to_convert(" + sj + ")";
        case MATCH_RULE_SUPPORTED:
            // should be only one arg
            return "Matcher::match_rule_supported(" + sj + ")";
        case OUT_CNT:
            return sj + "->outcnt()";
        default:
            throw new RuntimeException("Unexpected call name: " + name);
        }
    }

    public static String makeGetHigh(String nodeCode, ValType type) {
        return makeCastToTypeIntOrLong(nodeCode, type) + "->_hi" ;
    }

    public static String makeGetLow(String nodeCode, ValType type) {
        return makeCastToTypeIntOrLong(nodeCode, type) + "->_lo" ;
    }

    public static String makeNewConDeclStmt(String identifier, String nodeCode, ValType type) {
        return type.getJTypeName() + " " + identifier + " = " + makeGetCon(nodeCode, type) + ";";
    }

    private static String makeGetCon(String nodeCode, ValType type) {
        return makeCastToTypeIntOrLong(nodeCode, type) + "->get_con()";
    }

    public static String makeNewBinNode(
            String l, String r, BinNode node, ValType type) {
        return makeNewBinNode(l, r, node.getNodeType(), type);
    }

    public static String makeNewBinNode(
            String l, String r, BinNode.NodeType nodeType, ValType type) {
        if (nodeType == BinNode.NodeType.ROTATE_RIGHT_NODE
                || nodeType == BinNode.NodeType.ROTATE_LEFT_NODE) {
            return "new " + nodeType.toCodeGen(type) + "(" +
                    l + ", " + r + ", "+ type.getTypeIntOrLong() + ")";
        } else {
            return "new " + nodeType.toCodeGen(type) + "(" +
                    l + ", " + r + ")";
        }
    }

    public static String makeConFromLiteral(String value, ValType type) {
        return "phase->" + type.getTypeName() + "con(" + value + ")";
    }

    public static String makeOperatorCheck(String nodeCode, BinNode.Op op, ValType type) {
        return makeEqual(makeGetOperator(nodeCode), op.toCodeGen(type));
    }

    public static String makeIsConLiteral(String nodeCode, Number value, ValType type) {
        return LITERALS.containsKey(value) ?
                makeEqual(makeType(nodeCode),  LITERALS.get(value)):
                makeCastToTypeIntOrLong(nodeCode, type) + "->is_con(" + value + ")";
    }

    private static String makeCastToTypeIntOrLong(String nodeCode, ValType type) {
        return makeType(nodeCode) + "->" + type.getCastToTypeMethodCall();
    }

    public static String makeType(String nodeCode) {
        return "phase->type(" + nodeCode + ")";
    }

    public static String makeType(Number value) {
        if (!LITERALS.containsKey(value)) {
            throw new RuntimeException("No type for this literal: " + value);
        }
        return LITERALS.get(value);
    }

    public static String makeType(Lib.Type libType) {
        return LIB_TYPE_TO_TYPE.get(libType);
    }

    public static String makeIsCon(String nodeCode, ValType type) {
        return makeEqual(makeGetOperator(nodeCode), type.getOpCon());
    }

    private static String makeGetOperator(String nodeCode) {
        return nodeCode + "->Opcode()";
    }

    public static String makeEqual(String l, String r) {
        return makeBinaryExpr(l, r, BinNode.Op.EQ.asStr());
    }

    /**
     * Call 'phase->transform(X)' on any new Nodes X made.
     * https://github.com/openjdk/jdk/blob/f830cbec909b91ad0f00f46a3496d83ecb5912ed/src/hotspot/share/opto/node.cpp#L1180
     * <pre>
     * You must call 'phase->transform(X)' on any new Nodes X you make, except
     * for the returned root node.  Example: reshape "X*31" with "(X<<5)-X".
     *    Node *shift=phase->transform(new LShiftINode(in(1),phase->intcon(5)));
     *    return new AddINode(shift, in(1));
     *
     * When making a Node for a constant use 'phase->makecon' or 'phase->intcon'.
     * These forms are faster than 'phase->transform(new ConNode())' and Do
     * The Right Thing with def-use info.
     * </pre>
     */
    public static String wrapPhaseTransform(String newNodeX) {
        return "phase->transform(" + newNodeX + ")";
    }

    public static String wrapParentheses(String s) {
        return "(" + s + ")";
    }

    public static String makeCastToJIntOrLong(String s, ValType type) {
        return "(" + type.getJTypeName() + ") " + s;
    }

    public static String evaluateBinaryExpr(String l, String r, BinNode.Op operator) {
        switch (operator) {
        case SUB:
            return useJavaMacro(l, r, "java_subtract");
        case ADD:
            return useJavaMacro(l, r, "java_add");
        case MUL:
            return useJavaMacro(l, r, "java_multiply");
        case MIN:
            return useJavaMacro(l, r, "MIN2");
        case MAX:
            return useJavaMacro(l, r, "MAX2");
        case SHIFTL:
        case SHIFTR:
        case SHIFTUR:
        case BIN_AND:
        case BIN_OR:
        case LOGIC_AND:
        case LOGIC_OR:
        case EQ:
        case NE:
        case LT:
        case LE:
        case GT:
        case GE:
            return makeBinaryExpr(l, r, operator.asStr());
        default:
            throw new RuntimeException("Non-supported operator in evaluating binary expression: " + operator);
        }
    }

    public static String evaluateUnaryExpr(String operand, UnaryNode.Op operator) {
        switch (operator) {
        case MINUS:
        case BITWISE_NOT:
            return makeUnaryExpr(operand, operator.asStr());
        default:
            throw new RuntimeException("Non-supported operator in evaluating unary expression: " + operator);
        }
    }

    private static String useJavaMacro(String l, String r, String operator) {
        return operator + "(" + l + ", " + r + ")";
    }

    private static String makeBinaryExpr(String l, String r, String op) {
        return l + " " + op + " " + r;
    }

    private static String makeUnaryExpr(String operand, String operator) {
        return operator + operand;
    }
}