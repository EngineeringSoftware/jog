package jog.codegen;

import com.github.javaparser.StaticJavaParser;

import jog.Constants;
import jog.ast.expr.BinNode;
import jog.ast.expr.BinNode.Op;
import jog.ast.expr.CallNode;
import jog.ast.expr.UnaryNode;
import jog.log.Log;
import org.junit.Assert;
import org.junit.Test;

import static jog.ast.visitor.CodeGenUtil.*;

public class SubNodeTest extends AbstractPatternVTest {

    public SubNodeTest() {
        super("SubNodeExample.java");
    }

    @Test
    public void testPSub1() {
        String actual = translate("pSub1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(2)) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, inVar(1), AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr("c0", UnaryNode.Op.MINUS)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub1TestCodeGen() {
        generateTest("pSub1");
        Assert.assertEquals(
                StaticJavaParser.parseStatement("Asserts.assertEQ(a - " + Constants.START_NUMBER + ", testpSub1(a));"),
                getAssertion("pSub1"));
        Assert.assertEquals(StaticJavaParser.parseMethodDeclaration(
                "@Test\n" +
                "@IR(failOn = {IRNode.SUB})\n" +
                "@IR(counts = {IRNode.ADD, \"1\"})\n" +
                "public int testpSub1(int x) { return x - " +  Constants.START_NUMBER + "; }"),
                getTestMethod("pSub1").removeComment());
    }

    @Test
    public void testPSub2() {
        String actual = translate("pSub2");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(1, 2)) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.OK_TO_CONVERT, inVar(1), inVar(2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 1), inVar(2)), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub3() {
        String actual = translate("pSub3");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(2, 2)) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.OK_TO_CONVERT, inVar(2), inVar(1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1), inVar(2, 1)), AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr("c0", UnaryNode.Op.MINUS)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubXMinus_XPlusY_() {
        String actual = translate("pSubXMinus_XPlusY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub_XMinusY_MinusX() {
        String actual = translate("pSub_XMinusY_MinusX");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        makeEqual(inVar(1, 1), inVar(2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubXMinus_YPlusX_() {
        String actual = translate("pSubXMinus_YPlusX_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub7() {
        String actual = translate("pSub7");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConLiteral(inVar(1), 0),
                        checkOpInt(Op.SUB, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        makeType(inVar(2, 1)),
                        makeType(0),
                        Op.NE)) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(2, 2), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub8() {
        String actual = translate("pSub8");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConLiteral(inVar(1), 0),
                        checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con", inVar(2, 2)) + nL() +
                ifStart(evaluateBinaryExpr("con", "0", Op.NE)) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr("con", UnaryNode.Op.MINUS)), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub9() {
        String actual = translate("pSub9");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 2), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub10() {
        String actual = translate("pSub10");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 1), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub11() {
        String actual = translate("pSub11");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub12() {
        String actual = translate("pSub12");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 2), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSub13() {
        String actual = translate("pSub13");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 2)) + nL() +
                ifStart(evaluateBinaryExpr(AbstractPatternVTest.makeCallInt(CallNode.Name.OUT_CNT, inVar(2)), "1", Op.EQ)) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, tNewNodeInt(BinNode.NodeType.ADD_NODE, inVar(1), inVar(2, 2)), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubAssociative1() {
        String actual = translate("pSubAssociative1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MUL, 1),
                        checkOpInt(Op.MUL, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.MUL_NODE, inVar(1, 1), tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 2), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubAssociative2() {
        String actual = translate("pSubAssociative2");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MUL, 1),
                        checkOpInt(Op.MUL, 2),
                        makeEqual(inVar(1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.MUL_NODE, inVar(1, 2), tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 1), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubAssociative3() {
        String actual = translate("pSubAssociative3");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MUL, 1),
                        checkOpInt(Op.MUL, 2),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.MUL_NODE, inVar(1, 2), tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 1), inVar(2, 1)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubAssociative4() {
        String actual = translate("pSubAssociative4");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MUL, 1),
                        checkOpInt(Op.MUL, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.MUL_NODE, inVar(1, 1), tNewNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 2), inVar(2, 1)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPSubNegRShiftToURShift() {
        String actual = translate("pSubNegRShiftToURShift");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConLiteral(inVar(1), 0),
                        checkOpInt(Op.SHIFTR, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), 31)) + nL() +
                ret(newNodeInt(BinNode.NodeType.SHIFTUR_NODE, inVar(2, 1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSubAddSub1539() {
        String actual = translate("pNewSubAddSub1539");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 1), 0)) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, inVar(1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSubAddSub1560() {
        String actual = translate("pNewSubAddSub1560");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConLiteral(inVar(1), -1)) + nL() +
                ret(newNodeInt(BinNode.NodeType.XOR_NODE, inVar(2), inVar(1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSubAddSub1564() {
        String actual = translate("pNewSubAddSub1564");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConInt(inVar(1)),
                        checkOpInt(Op.XOR, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c", inVar(1)) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, inVar(2, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("c", AbstractPatternVTest.makeCastToJInt("1"), Op.ADD)))) + nL() + //                 "return new AddINode(in(2)->in(1), phase->intcon(java_add(c, 1)));\n" +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSubAddSub1574() {
        String actual = translate("pNewSubAddSub1574");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConInt(inVar(1)),
                        checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(2, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(1)) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.OK_TO_CONVERT, inVar(2), inVar(1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("c0", "c1", Op.SUB)), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_XOrY_Minus_XXorY_() {
        String actual = translate("pNewSub_XOrY_Minus_XXorY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.BIN_AND_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_AOrB_Minus_AAndB_() {
        String actual = translate("pNewSub_AOrB_Minus_AAndB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_APlusB_Minus_AOrB_() {
        String actual = translate("pNewSub_APlusB_Minus_AOrB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.BIN_OR, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.BIN_AND_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_APlusB_Minus_AXorB_() {
        String actual = translate("pNewSub_APlusB_Minus_AXorB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.BIN_OR_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_AAndB_Minus_AOrB_() {
        String actual = translate("pNewSub_AAndB_Minus_AOrB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.BIN_OR, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 1), inVar(1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSub_AXorB_Minus_AOrB_() {
        String actual = translate("pNewSub_AXorB_Minus_AOrB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), tNewNodeInt(BinNode.NodeType.BIN_AND_NODE, inVar(1, 1), inVar(1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewSubNotXMinusNotY() {
        String actual = translate("pNewSubNotXMinusNotY");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(2, 1), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSubPNewSubCMinus_C2MinuxX_() {
        String actual = translate("pNewSubCMinus_C2MinuxX_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConInt(inVar(1)),
                        checkOpInt(Op.SUB, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C", inVar(1)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C2", inVar(2, 1)) + nL() +
                ret(newNodeInt(BinNode.NodeType.ADD_NODE, inVar(2, 2), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("C", "C2", Op.SUB)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSubPNewSub_XOrY_MinusX() {
        String actual = translate("pNewSub_XOrY_MinusX");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        makeEqual(inVar(1, 1), inVar(2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.BIN_AND_NODE, tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2), AbstractPatternVTest.makeIntConFromLiteral("-1")), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Op1And_NegX__MinusOp1() {
        String actual = translate("pNewSub_Op1And_NegX__MinusOp1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.SUB, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2, 1), 0),
                        makeEqual(inVar(1, 1), inVar(2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE, inVar(1, 2, 1), tNewNodeInt(BinNode.NodeType.BIN_AND_NODE, inVar(2), tNewNodeInt(BinNode.NodeType.ADD_NODE, inVar(1, 2, 2), AbstractPatternVTest.makeIntConFromLiteral("-1"))))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Op1AndC_MinusOp1() {
        String actual = translate("pNewSub_Op1AndC_MinusOp1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        makeEqual(inVar(1, 1), inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C", inVar(1, 2)) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        AbstractPatternVTest.makeIntConFromLiteral("0"),
                        tNewNodeInt(BinNode.NodeType.BIN_AND_NODE,
                                inVar(2),
                                AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr("C", UnaryNode.Op.BITWISE_NOT))))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewXMinus_XAndY_() {
        String actual = translate("pNewSubXMinus_XAndY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1), inVar(2, 1))) + nL() +
                ret(newNodeInt(BinNode.NodeType.BIN_AND_NODE,
                        inVar(1),
                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2, 2), AbstractPatternVTest.makeIntConFromLiteral("-1")))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewNotXMinus_NotXMinY_() {
        String actual = translate("pNewNotXMinus_NotXMinY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.MIN, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MIN_NODE,
                                        inVar(1, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2))),
                                inVar(1, 2)),
                        inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewNotXMinus_NotXMinY_Sym() {
        String actual = translate("pNewNotXMinus_NotXMinY_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.MIN, 2),
                        checkOpInt(Op.XOR, 2, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MIN_NODE,
                                        inVar(1, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2, 1), inVar(1, 2))),
                                inVar(1, 2)),
                        inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotXMinY_MinusNotX() {
        String actual = translate("pNew_NotXMinY_MinusNotX");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MIN, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        inVar(2, 1),
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MIN_NODE,
                                        inVar(2, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 2), inVar(2, 2))),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotXMinY_MinusNotXSym() {
        String actual = translate("pNew_NotXMinY_MinusNotXSym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MIN, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 2, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        inVar(2, 1),
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MIN_NODE,
                                        inVar(2, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 1), inVar(2, 2))),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewNotXMinus_NotXMaxY_() {
        String actual = translate("pNewNotXMinus_NotXMaxY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.MAX, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MAX_NODE,
                                        inVar(1, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2))),
                                inVar(1, 2)),
                        inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testPNewNotXMinus_NotXMaxY_Sym() {
        String actual = translate("pNewNotXMinus_NotXMaxY_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.MAX, 2),
                        checkOpInt(Op.XOR, 2, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MAX_NODE,
                                        inVar(1, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(2, 1), inVar(1, 2))),
                                inVar(1, 2)),
                        inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotXMaxY_MinusNotX() {
        String actual = translate("pNew_NotXMaxY_MinusNotX");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MAX, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        inVar(2, 1),
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MAX_NODE,
                                        inVar(2, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 2), inVar(2, 2))),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotXMaxY_MinusNotXSym() {
        String actual = translate("pNew_NotXMaxY_MinusNotXSym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MAX, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 2, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(BinNode.NodeType.SUB_NODE,
                        inVar(2, 1),
                        tNewNodeInt(BinNode.NodeType.XOR_NODE,
                                tNewNodeInt(BinNode.NodeType.MAX_NODE,
                                        inVar(2, 1),
                                        tNewNodeInt(BinNode.NodeType.XOR_NODE, inVar(1, 1), inVar(2, 2))),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }
}
