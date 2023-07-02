package jog.codegen;

import jog.api.Lib;
import jog.ast.expr.BinNode.Op;
import jog.ast.expr.BinNode.NodeType;
import jog.ast.expr.CallNode;
import jog.ast.expr.UnaryNode;
import org.junit.Assert;
import org.junit.Test;

import static jog.ast.visitor.CodeGenUtil.*;

public class AddNodeTest extends AbstractPatternVTest {

    public AddNodeTest() {
        super("AddNodeExample.java");
    }

    @Test
    public void testPAdd1() {
        String actual = translate("pAdd1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1)),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con1", inVar(1, 1)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con2", inVar(2)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("con1", "con2", Op.ADD)), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd2() {
        String actual = translate("pAdd2");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        checkOpInt(Op.SUB, 2)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, tNewNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2, 1)),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 2), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd3() {
        String actual = translate("pAdd3");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals("pAdd3", expected, actual);
    }

    @Test
    public void testPAdd3Sym() {
        String actual = translate("pAdd3Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.SUB, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(2, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd4() {
        String actual = translate("pAdd4");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        checkOpInt(Op.ADD, 2),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd4Sym() {
        String actual = translate("pAdd4Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.SUB, 2),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(2, 1), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd5() {
        String actual = translate("pAdd5");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        checkOpInt(Op.SUB, 2),
                        makeEqual(inVar(1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1, 1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd6() {
        String actual = translate("pAdd6");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        checkOpInt(Op.SUB, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(2, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd7() {
        String actual = translate("pAdd7");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 1), 0)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd8() {
        String actual = translate("pAdd8");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1), 0)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(2), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAdd9() {
        String actual = translate("pAdd9");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SHIFTUR, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("z", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("y", inVar(2)) + nL() +
                "z = z & 0x1f;" + nL() +
                // (z < 5 && -5 < y && y < 0 && x >= -(y << z))
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("z", "5", Op.LT),
                                        pEvaluateBinaryExpr("-5", "y", Op.LT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr("y", "0", Op.LT),
                                Op.LOGIC_AND),
                        pEvaluateBinaryExpr(
                                AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                pEvaluateUnaryExpr(
                                        pEvaluateBinaryExpr("y", "z", Op.SHIFTL),
                                        UnaryNode.Op.MINUS),
                                Op.GE),
                        Op.LOGIC_AND)) + nL() +
                ret(newNodeInt(NodeType.SHIFTUR_NODE,
                        tNewNodeInt(NodeType.ADD_NODE,
                                inVar(1, 1),
                                AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("y", "z", Op.SHIFTL))),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddAssociative1() {
        String actual = translate("pAddAssociative1");
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
                ret(newNodeInt(NodeType.MUL_NODE,
                        inVar(1, 1),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 2), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddAssociative2() {
        String actual = translate("pAddAssociative2");
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
                ret(newNodeInt(NodeType.MUL_NODE,
                        inVar(1, 2),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddAssociative3() {
        String actual = translate("pAddAssociative3");
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
                ret(newNodeInt(NodeType.MUL_NODE,
                        inVar(1, 2),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2, 1)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddAssociative4() {
        String actual = translate("pAddAssociative4");
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
                ret(newNodeInt(NodeType.MUL_NODE,
                        inVar(1, 1),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 2), inVar(2, 1)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1165() {
        String actual = translate("pNewAddAddSub1165");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1), 0),
                        checkOpInt(Op.SUB, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1, 1), tNewNodeInt(NodeType.ADD_NODE, inVar(1, 2), inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1156() {
        String actual = translate("pNewAddAddSub1156");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                ifStart(makeEqual(inVar(1), inVar(2))) + nL() +
                ret(newNodeInt(NodeType.SHIFTL_NODE, inVar(1), AbstractPatternVTest.makeIntConFromLiteral("1"))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void TestPAddURShiftThenLShiftToRRotation() {
        String actual = translate("pAddURShiftThenLShiftToRRotation");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_RIGHT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTUR, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.SHIFTL, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("lshift", inVar(2, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("rshift", inVar(1, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("lshift", "31", Op.BIN_AND),
                        pEvaluateBinaryExpr(
                                AbstractPatternVTest.makeCastToJInt("32"),
                                pEvaluateBinaryExpr("rshift", "31", Op.BIN_AND),
                                Op.SUB),
                        Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_RIGHT_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("rshift", "31", Op.BIN_AND)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void TestPAddURShiftThenLShiftToRRotationSym() {
        String actual = translate("pAddURShiftThenLShiftToRRotationSym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_RIGHT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTL, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.SHIFTUR, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("lshift", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("rshift", inVar(2, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("lshift", "31", Op.BIN_AND),
                        pEvaluateBinaryExpr(
                                AbstractPatternVTest.makeCastToJInt("32"),
                                pEvaluateBinaryExpr("rshift", "31", Op.BIN_AND),
                                Op.SUB),
                        Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_RIGHT_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("rshift", "31", Op.BIN_AND)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddNotXPlusOne() {
        String actual = translate("pAddNotXPlusOne");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2), 1)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral("0"), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddNotXPlusOneToNegX1() {
        String actual = translate("pNewAddNotXPlusOneToNegX1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), 1),
                        checkOpInt(Op.XOR, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1, 1), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddNotXPlusOneToNegX2() {
        String actual = translate("pNewAddNotXPlusOneToNegX2");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), 1)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(2, 1), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddNotXPlusOneToNegX3() {
        String actual = translate("pNewAddNotXPlusOneToNegX3");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2), 1)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1, 2), inVar(1, 1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddNotXPlusOneToNegX4() {
        String actual = translate("pNewAddNotXPlusOneToNegX4");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2, 2), -1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2), 1)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, inVar(1, 1), inVar(1, 2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1202() {
        String actual = translate("pNewAddAddSub1202");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c", inVar(2)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE, AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("c", AbstractPatternVTest.makeCastToJInt("1"), Op.SUB)), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1295() {
        String actual = translate("pNewAddAddSub1295");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1295Sym() {
        String actual = translate("pNewAddAddSub1295Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1309() {
        String actual = translate("pNewAddAddSub1309");
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
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1309Sym() {
        String actual = translate("pNewAddAddSub1309Sym");
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
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAdd_APlusC1_Plus_C2MinusB_() {
        String actual = translate("pNewAdd_APlusC1_Plus_C2MinusB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.SUB, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C1", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C2", inVar(2, 1)) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE,
                        tNewNodeInt(NodeType.SUB_NODE, inVar(1, 1), inVar(2, 2)),
                        AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("C1", "C2", Op.ADD)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddXModC0PlusXDivC0ModC1MulC0() {
        String actual = translate("pNewAddXModC0PlusXDivC0ModC1MulC0");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MOD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.MUL, 2),
                        checkOpInt(Op.MOD, 2, 1),
                        checkOpInt(Op.DIV, 2, 1, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 1, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C0", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C1", inVar(2, 1, 2)) + nL() +
                ret(newNodeInt(NodeType.MOD_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("C0", "C1", Op.MUL)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddMoveConstantRight() {
        String actual = translate("pAddMoveConstantRight");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                ifStart(AbstractPatternVTest.makeIsConInt(inVar(1))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(2), inVar(1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddConstantAssociative() {
        String actual = translate("pAddConstantAssociative");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con1", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con2", inVar(2)) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("con1", "con2", Op.ADD)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddPushConstantDown() {
        String actual = translate("pAddPushConstantDown");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, tNewNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(2)), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddPushConstantDownSym() {
        String actual = translate("pAddPushConstantDownSym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, tNewNodeInt(NodeType.ADD_NODE, inVar(1), inVar(2, 1)), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1040() {
        String actual = translate("pNewAddAddSub1040");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1, 1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1, 2)),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), 1)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c2", inVar(1, 1, 1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(1, 1, 2)) + nL() +
                ifStart(evaluateBinaryExpr("c2", pEvaluateUnaryExpr("c1", UnaryNode.Op.BITWISE_NOT), Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE,
                        inVar(2),
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                inVar(1, 1, 1, 1),
                                inVar(1, 1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewAddAddSub1043() {
        String actual = translate("pNewAddAddSub1043");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1, 1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1, 2)),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), 1)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c2", inVar(1, 1, 1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(1, 1, 2)) + nL() +
                ifStart(evaluateBinaryExpr("c2", "c1", Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.SUB_NODE,
                        inVar(2),
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(1, 1, 1, 1),
                                AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr("c1", UnaryNode.Op.BITWISE_NOT))))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_AMaxB_Plus_AMinB_() {
        String actual = translate("p_AMaxB_Plus_AMinB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MAX, 1),
                        checkOpInt(Op.MIN, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_AMaxB_Plus_AMinB_Sym() {
        String actual = translate("p_AMaxB_Plus_AMinB_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.MIN, 1),
                        checkOpInt(Op.MAX, 2),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_XOrC2_PlusC() {
        String actual = translate("pNew_XOrC2_PlusC");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C2", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("C", inVar(2)) + nL() +
                ifStart(evaluateBinaryExpr("C2", pEvaluateUnaryExpr("C", UnaryNode.Op.MINUS), Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1), inVar(1, 2)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XLShiftC1_Or_XURShiftC2_() {
        String actual = translate("p_XLShiftC1_Or_XURShiftC2_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_LEFT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTL, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.SHIFTUR, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("lshift", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("rshift", inVar(2, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("lshift", "31", Op.BIN_AND),
                        pEvaluateBinaryExpr(
                                AbstractPatternVTest.makeCastToJInt("32"),
                                pEvaluateBinaryExpr("rshift", "31", Op.BIN_AND),
                                Op.SUB),
                        Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_LEFT_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("lshift", "31", Op.BIN_AND)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XURShiftC1_Or_XLShiftC2_() {
        String actual = translate("p_XURShiftC1_Or_XLShiftC2_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_RIGHT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTUR, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.SHIFTL, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("rshift", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("lshift", inVar(2, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("rshift", "31", Op.BIN_AND),
                        pEvaluateBinaryExpr(
                                AbstractPatternVTest.makeCastToJInt("32"),
                                pEvaluateBinaryExpr("lshift", "31", Op.BIN_AND),
                                Op.SUB),
                        Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_RIGHT_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("rshift", "31", Op.BIN_AND)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewXPlus_ConMinusY_() {
        String actual = translate("pNewXPlus_ConMinusY_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.SUB, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, tNewNodeInt(NodeType.SUB_NODE, inVar(1), inVar(2, 2)), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewXPlus_ConMinusY_Sym() {
        String actual = translate("pNewXPlus_ConMinusY_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SUB, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 1))) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, tNewNodeInt(NodeType.SUB_NODE, inVar(2), inVar(1, 2)), inVar(1, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XLShiftS_Or_XURShift_ConMinusS__() {
        String actual = translate("p_XLShiftS_Or_XURShift_ConMinusS__");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_LEFT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTL, 1),
                        checkOpInt(Op.SHIFTUR, 2),
                        checkOpInt(Op.SUB, 2, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2, 1)),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con", inVar(2, 2, 1)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("con", "0", Op.EQ),
                        pEvaluateBinaryExpr("con", "32", Op.EQ),
                        Op.LOGIC_OR)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_LEFT_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XURShiftS_Or_XLShift_ConMinusS__() {
        String actual = translate("p_XURShiftS_Or_XLShift_ConMinusS__");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(AbstractPatternVTest.makeCallInt(CallNode.Name.MATCH_RULE_SUPPORTED, opToCodeGenInt(Lib.Operator.OP_ROTATE_RIGHT.toBinOp()))) + nL() +
                ifStart(checkOpInt(Op.SHIFTUR, 1),
                        checkOpInt(Op.SHIFTL, 2),
                        checkOpInt(Op.SUB, 2, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2, 1)),
                        makeEqual(inVar(1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 2, 2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("con", inVar(2, 2, 1)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr("con", "0", Op.EQ),
                        pEvaluateBinaryExpr("con", "32", Op.EQ),
                        Op.LOGIC_OR)) + nL() +
                ret(newNodeInt(NodeType.ROTATE_RIGHT_NODE, inVar(1, 1), inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPMinAssociative() {
        String actual = translate("pMinAssociative");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.MIN, 1)) + nL() +
                ret(newNodeInt(NodeType.MIN_NODE, inVar(1, 1), tNewNodeInt(NodeType.MIN_NODE, inVar(1, 2), inVar(2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAddDistributiveOverMin() {
        String actual = translate("pAddDistributiveOverMin");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.ADD, 2),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(2, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c0", "0", Op.GT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                        "c0",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                Op.GT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c0", "0", Op.LT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                        "c0",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                Op.LT),
                                        Op.LOGIC_AND),
                                Op.LOGIC_OR),
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c1", "0", Op.GT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                        "c1",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                Op.GT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c1", "0", Op.LT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                        "c1",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                Op.LT),
                                        Op.LOGIC_AND),
                                Op.LOGIC_OR),
                        Op.LOGIC_AND)) + nL() +
                ret(newNodeInt(NodeType.ADD_NODE, inVar(1, 1), AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("c0", "c1", Op.MIN)))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPAssociativeThenAddDistributiveOverMin() {
        String actual = translate("pAssociativeThenAddDistributiveOverMin");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        checkOpInt(Op.MIN, 2),
                        checkOpInt(Op.ADD, 2, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(2, 1, 2)),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(2, 1, 2)) + nL() +
                ifStart(evaluateBinaryExpr(
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c0", "0", Op.GT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                        "c0",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                Op.GT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c0", "0", Op.LT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                        "c0",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                Op.LT),
                                        Op.LOGIC_AND),
                                Op.LOGIC_OR),
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c1", "0", Op.GT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                        "c1",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetHighInt(inVar(1, 1)),
                                                Op.GT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("c1", "0", Op.LT),
                                        pEvaluateBinaryExpr(
                                                pEvaluateBinaryExpr(
                                                        AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                        "c1",
                                                        Op.ADD),
                                                AbstractPatternVTest.makeGetLowInt(inVar(1, 1)),
                                                Op.LT),
                                        Op.LOGIC_AND),
                                Op.LOGIC_OR),
                        Op.LOGIC_AND)) + nL() +
                ret(newNodeInt(NodeType.MIN_NODE,
                        tNewNodeInt(NodeType.ADD_NODE,
                                inVar(1, 1),
                                AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr("c0", "c1", Op.MIN))),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewDeMorganLawOrToAnd() {
        String actual = translate("pNewDeMorganLawOrToAnd");
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
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE, inVar(1, 1), inVar(2, 1)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewDeMorganWithReassociationOrToAnd() {
        String actual = translate("pNewDeMorganWithReassociationOrToAnd");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 2, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        inVar(1, 1),
                        tNewNodeInt(NodeType.XOR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(1, 2, 1), inVar(2, 1)),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNewDeMorganWithReassociationOrToAndSym() {
        String actual = translate("pNewDeMorganWithReassociationOrToAndSym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        inVar(1, 2),
                        tNewNodeInt(NodeType.XOR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(1, 1, 1), inVar(2, 1)),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndB_OrNot_AOrB_() {
        String actual = translate("pNew_AAndB_OrNot_AOrB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1),
                                inVar(1, 2)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndB_OrNot_AOrB_Sym() {
        String actual = translate("pNew_AAndB_OrNot_AOrB_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(2, 1),
                                inVar(2, 2)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndB_OrNot_BOrA_() {
        String actual = translate("pNew_AAndB_OrNot_BOrA_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1),
                                inVar(1, 2)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndB_OrNot_BOrA_Sym() {
        String actual = translate("pNew_AAndB_OrNot_BOrA_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(2, 1),
                                inVar(2, 2)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AXorB_OrNot_AOrB_() {
        String actual = translate("pNew_AXorB_OrNot_AOrB_");
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
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                inVar(1, 1),
                                inVar(1, 2)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AXorB_OrNot_AOrB_Sym() {
        String actual = translate("pNew_AXorB_OrNot_AOrB_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                inVar(2, 1),
                                inVar(2, 2)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AXorB_OrNot_BOrA_() {
        String actual = translate("pNew_AXorB_OrNot_BOrA_");
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
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                inVar(1, 1),
                                inVar(1, 2)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AXorB_OrNot_BOrA_Sym() {
        String actual = translate("pNew_AXorB_OrNot_BOrA_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                inVar(2, 1),
                                inVar(2, 2)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndNotB_Or_NotAAndB_1() {
        String actual = translate("pNew_AAndNotB_Or_NotAAndB_1");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        makeEqual(inVar(1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 2, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 2, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE, inVar(1, 1), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndNotB_Or_NotAAndB_2() {
        String actual = translate("pNew_AAndNotB_Or_NotAAndB_2");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(1, 2, 1) + nL() +
                declInVar(1, 2, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 2),
                        makeEqual(inVar(1, 1), inVar(2, 2, 1)),
                        makeEqual(inVar(1, 2, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 2, 2), inVar(2, 2, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE, inVar(1, 1), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndNotB_Or_NotAAndB_3() {
        String actual = translate("pNew_AAndNotB_Or_NotAAndB_3");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE, inVar(1, 2), inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_AAndNotB_Or_NotAAndB_4() {
        String actual = translate("pNew_AAndNotB_Or_NotAAndB_4");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 2) + nL() +
                declInVar(2, 2, 1) + nL() +
                declInVar(2, 2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 2),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 2, 1))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE, inVar(1, 2), inVar(2, 1))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_Or_Not_AOrC_AndB_() {
        String actual = translate("pNew_Not_AOrB_AndC_Or_Not_AOrC_AndB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2)),
                        tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 1, 1), inVar(1, 1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_Or_Not_BOrC_AndA_() {
        String actual = translate("pNew_Not_AOrB_AndC_Or_Not_BOrC_AndA_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2)),
                        tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 1, 2), inVar(1, 1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_OrNot_AOrC_() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_AOrC_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(1, 1, 1, 2), inVar(1, 2)),
                                inVar(2, 1, 1)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_OrNot_AOrC_Sym() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_AOrC_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(2, 1, 1, 2), inVar(2, 2)),
                                inVar(1, 1, 1)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_OrNot_BOrC_() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_BOrC_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(1, 1, 1, 1), inVar(1, 2)),
                                inVar(2, 1, 1)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AOrB_AndC_OrNot_BOrC_Sym() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_BOrC_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                tNewNodeInt(NodeType.BIN_AND_NODE, inVar(2, 1, 1, 1), inVar(2, 2)),
                                inVar(1, 1, 1)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_Not_AOrB_AndC_OrNot_COr_AXorB__() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_COr_AXorB__");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 1, 2, 1) + nL() +
                declInVar(2, 1, 2, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 2),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 2, 1)), // A
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 2, 2)), // B
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)), // -1
                        makeEqual(inVar(1, 2), inVar(2, 1, 1))) + nL() + // C
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1, 1, 1), inVar(1, 1, 1, 2)),
                                tNewNodeInt(NodeType.BIN_OR_NODE,
                                        inVar(1, 2),
                                        tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 1, 1), inVar(1, 1, 1, 2)))),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_Not_AOrB_AndC_OrNot_COr_AXorB__Sym() {
        String actual = translate("pNew_Not_AOrB_AndC_OrNot_COr_AXorB__Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 1, 2, 1) + nL() +
                declInVar(1, 1, 2, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 2),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 2)), // C
                        makeEqual(inVar(1, 1, 2, 1), inVar(2, 1, 1, 1)), // A
                        makeEqual(inVar(1, 1, 2, 2), inVar(2, 1, 1, 2)), // B
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() + // -1
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1, 2, 1), inVar(1, 1, 2, 2)),
                                tNewNodeInt(NodeType.BIN_OR_NODE,
                                        inVar(2, 2),
                                        tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 2, 1), inVar(1, 1, 2, 2)))),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew__NotAAndB_AndC_OrNot__AOrB_OrC_() {
        String actual = translate("pNew__NotAAndB_AndC_OrNot__AOrB_OrC_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.BIN_AND, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        checkOpInt(Op.BIN_OR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)), // A
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)), // -1
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 1, 2)), // B
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() + // C
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(1, 1, 1, 1), // A
                                tNewNodeInt(NodeType.XOR_NODE,
                                        inVar(1, 1, 2), // B
                                        inVar(1, 2))), // C
                        inVar(2, 2))) + nL() + // -1
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew__NotAAndB_AndC_OrNot__AOrB_OrC_Sym() {
        String actual = translate("pNew__NotAAndB_AndC_OrNot__AOrB_OrC_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        checkOpInt(Op.BIN_OR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)), // A
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 2)), // B
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)), // C
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() + // -1
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(1, 1, 1, 1), // A
                                tNewNodeInt(NodeType.XOR_NODE,
                                        inVar(2, 1, 2), // B
                                        inVar(2, 2))), // C
                        inVar(1, 2))) + nL() + // -1
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotAAndBAndC_Or_Not_AOrB_() {
        String actual = translate("pNew_NotAAndBAndC_Or_Not_AOrB_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.BIN_AND, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(1, 2),
                                tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 2), inVar(2, 2))),
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(2, 1, 1),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotAAndBAndC_Or_Not_AOrB_Sym() {
        String actual = translate("pNew_NotAAndBAndC_Or_Not_AOrB_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(2, 2),
                                tNewNodeInt(NodeType.XOR_NODE, inVar(1, 1, 2), inVar(1, 2))),
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1, 1),
                                inVar(1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotAAndBAndC_Or_Not_AOrC_() {
        String actual = translate("pNew_NotAAndBAndC_Or_Not_AOrC_");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 1, 1) + nL() +
                declInVar(1, 1, 1, 2) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        checkOpInt(Op.BIN_AND, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(1, 1, 2),
                                tNewNodeInt(NodeType.XOR_NODE, inVar(1, 2), inVar(2, 2))),
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(2, 1, 1),
                                inVar(2, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotAAndBAndC_Or_Not_AOrC_Sym() {
        String actual = translate("pNew_NotAAndBAndC_Or_Not_AOrC_Sym");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                declInVar(2, 1) + nL() +
                declInVar(2, 1, 1) + nL() +
                declInVar(2, 1, 1, 1) + nL() +
                declInVar(2, 1, 1, 2) + nL() +
                declInVar(2, 1, 2) + nL() +
                declInVar(2, 2) + nL() +
                ifStart(checkOpInt(Op.XOR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_AND, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.BIN_OR_NODE,
                                inVar(2, 1, 2),
                                tNewNodeInt(NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2))),
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1, 1),
                                inVar(1, 2)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }
}
