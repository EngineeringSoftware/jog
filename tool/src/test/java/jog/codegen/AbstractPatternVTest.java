package jog.codegen;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.UnaryNode;
import jog.ast.visitor.CodeGenUtil;
import jog.ast.visitor.NodeCodeGenBuilder;
import jog.ast.visitor.CodeGenUtil.ValType;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractPatternVTest {

    private final PatternFile patternFile;

    protected AbstractPatternVTest(String exampleFileName) {
        patternFile = new PatternFile("src/test/java/" + exampleFileName);
    }

    protected void generateTest(String pattern) {
        PatternV patternV = patternFile.getPattern(pattern);
        patternV.createTestMethodGen();
    }

    protected Statement getAssertion(String pattern) {
        PatternV patternV = patternFile.getPattern(pattern);
        TestMethodGen tmg = patternV.getTestMethodGen();
        return tmg.getAssertion();
    }

    protected MethodDeclaration getTestMethod(String pattern) {
        PatternV patternV = patternFile.getPattern(pattern);
        TestMethodGen tmg = patternV.getTestMethodGen();
        return tmg.getTestMethod();
    }

    protected String translate(String pattern) {
        PatternV patternV = patternFile.getPattern(pattern);
        patternV.translate();
        return patternV.getTranslation();
    }

    public static String opToCodeGenInt(BinNode.Op op) {
        return op.toCodeGen(ValType.INT);
    }

    public static String makeIsConInt(String nodeCode) {
        return CodeGenUtil.makeIsCon(nodeCode, CodeGenUtil.ValType.INT);
    }

    public static String makeIntConFromLiteral(String value) {
        return CodeGenUtil.makeConFromLiteral(value, CodeGenUtil.ValType.INT);
    }

    public static String makeNewIntConDeclStmt(String identifier, String nodeCode) {
        return CodeGenUtil.makeNewConDeclStmt(identifier, nodeCode, CodeGenUtil.ValType.INT);
    }

    public static String makeCastToJInt(String s) {
        return CodeGenUtil.makeCastToJIntOrLong(s, CodeGenUtil.ValType.INT);
    }

    public static String makeIsConLiteral(String nodeCode, int value) {
        return CodeGenUtil.makeIsConLiteral(nodeCode, value, CodeGenUtil.ValType.INT);
    }

    public static String makeGetLowInt(String nodeCode) {
        return CodeGenUtil.makeGetLow(nodeCode, CodeGenUtil.ValType.INT);
    }

    public static String makeGetHighInt(String nodeCode) {
        return CodeGenUtil.makeGetHigh(nodeCode, CodeGenUtil.ValType.INT);
    }

    public static String makeCallInt(CallNode.Name name, String... arguments) {
        return CodeGenUtil.makeCall(name, List.of(arguments), CodeGenUtil.ValType.INT);
    }

    public static String ifStart(String... conds) {
        return "if (" + and(conds) + ") {";
    }

    public static String and(String... conds) {
        return String.join("\n    && ", conds);
    }

    public static String checkOpInt(BinNode.Op op, int... pos) {
        return CodeGenUtil.makeOperatorCheck(inVar(pos), op, ValType.INT);
    }

    public static String ret(String s) {
        return "return " + s + ";";
    }

    public static String tNewNodeInt(BinNode.NodeType nodeType, String l, String r) {
        return CodeGenUtil.wrapPhaseTransform(newNodeInt(nodeType, l, r));
    }

    public static String newNodeInt(BinNode.NodeType nodeType, String l, String r) {
        return CodeGenUtil.makeNewBinNode(l, r, nodeType, ValType.INT);
    }

    public static String declInVar(int... pos) {
        // Assume pos.length >= 1
        Deque<Integer> seq = new ArrayDeque<>(Arrays.stream(pos).boxed().collect(Collectors.toList()));
        String varName = NodeCodeGenBuilder.seqToVar(seq);
        String initialization = NodeCodeGenBuilder.seqToCode(seq);
        return "Node* " + varName + " = " + initialization + ";";
    }

    public static String inVar(int... pos) {
        Deque<Integer> seq = new ArrayDeque<>(Arrays.stream(pos).boxed().collect(Collectors.toList()));
        return NodeCodeGenBuilder.seqToVar(seq);
    }

    /** With parentheses. */
    public static String pEvaluateBinaryExpr(String l, String r, BinNode.Op operator) {
        String code = CodeGenUtil.evaluateBinaryExpr(l, r, operator);
        return CodeGenUtil.wrapParentheses(code);
    }

    /** With parentheses. */
    public static String pEvaluateUnaryExpr(String operand, UnaryNode.Op operator) {
        return pEvaluateUnaryExpr(operand, operator, true);
    }

    public static String pEvaluateUnaryExpr(String operand, UnaryNode.Op operator, boolean parentheses) {
        String code = CodeGenUtil.evaluateUnaryExpr(operand, operator);
        if (parentheses) {
            code = CodeGenUtil.wrapParentheses(code);
        }
        return code;
    }

    public static String oB() {
        return "{";
    }

    public static String cB() {
        return "}";
    }

    public static String nL() {
        return "\n";
    }
}
