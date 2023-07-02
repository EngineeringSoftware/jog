package jog.codegen;

import jog.ast.expr.BinNode.Op;
import jog.ast.expr.BinNode.NodeType;
import jog.ast.expr.UnaryNode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static jog.ast.visitor.CodeGenUtil.*;

public class MulNodeTest extends AbstractPatternVTest {

    public MulNodeTest() {
        super("MulNodeExample.java");
    }

    @Test
    @Ignore
    public void testP1() {
        String actual = translate("pMul1");
        String expected = "if (in(1)->Opcode() == Op_SubI\n"
                + "    && phase->type(in(1)->in(1)) == TypeInt::ZERO\n"
                + "    && in(2)->Opcode() == Op_SubI\n"
                + "    && in(1)->in(1) == in(2)->in(1)) {\n"
                + "return new MulINode(in(1)->in(2), in(2)->in(2));\n"
                + "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP2() {
        String actual = translate("pMul2");
        String expected = "if (in(1)->Opcode() == Op_MaxI\n"
                + "    && in(2)->Opcode() == Op_MinI) {\n"
                + "if ((phase->type(in(1)->in(1)) == phase->type(in(2)->in(1))) && (phase->type(in(1)->in(2)) == phase->type(in(2)->in(2)))) return new MulINode(in(1)->in(1), in(1)->in(2));\n"
                + "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP3() {
        String actual = translate("pMul3");
        String expected = "if (in(1)->Opcode() == Op_ConI) {\n"
                + "return new MulINode(in(2), in(1));\n"
                + "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP4() {
        String actual = translate("pMul4");
        String expected = "if (in(1)->Opcode() == Op_MulI\n"
                + "    && in(1)->in(2)->Opcode() == Op_ConI\n"
                + "    && in(2)->Opcode() == Op_ConI) {\n"
                + "jint c2 = phase->type(in(2))->isa_int()->get_con();\n"
                + "jint c1 = phase->type(in(1)->in(2))->isa_int()->get_con();\n"
                + "if (phase->type(in(2)) != Type::TOP) {\n"
                + "return new MulINode(in(1)->in(1), phase->intcon(java_multiply(c1, c2)));\n"
                + "}\n"
                + "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP5() {
        String actual = translate("pMul5");
        String expected = "if (in(1)->Opcode() == Op_AddI\n"
                + "    && in(1)->in(2)->Opcode() == Op_ConI\n"
                + "    && in(2)->Opcode() == Op_ConI) {\n"
                + "jint c2 = phase->type(in(2))->isa_int()->get_con();\n"
                + "jint c1 = phase->type(in(1)->in(2))->isa_int()->get_con();\n"
                + "if (phase->type(in(2)) != Type::TOP) {\n"
                + "return new AddINode(phase->transform(new MulINode(in(1)->in(1), in(2))), phase->intcon(java_multiply(c1, c2)));\n"
                + "}\n"
                + "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_NegX_AndMinus1() {
        String actual = translate("p_NegX_AndMinus1");
        String expected = "if (in(1)->Opcode() == Op_SubI\n" +
                "    && phase->type(in(1)->in(1)) == TypeInt::ZERO\n" +
                "    && phase->type(in(2)) == TypeInt::ONE) {\n" +
                "return new AndINode(in(1)->in(2), in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XPlusCon1_LShiftCon0() {
        String actual = translate("p_XPlusCon1_LShiftCon0");
        String expected = "if (in(1)->Opcode() == Op_AddI\n" +
                "    && in(1)->in(2)->Opcode() == Op_ConI\n" +
                "    && in(2)->Opcode() == Op_ConI) {\n" +
                "jint con0 = phase->type(in(2))->isa_int()->get_con();\n" +
                "jint con1 = phase->type(in(1)->in(2))->isa_int()->get_con();\n" +
                "if (con0 < 16) {\n" +
                "return new AddINode(phase->transform(new LShiftINode(in(1)->in(1), in(2))), phase->intcon(con1 << con0));\n" +
                "}\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XRShiftC0_LShiftC0() {
        String actual = translate("p_XRShiftC0_LShiftC0");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.SHIFTR, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(2)),
                        makeEqual(inVar(1, 2), inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(2)) + nL() +
                ret(newNodeInt(
                        NodeType.BIN_AND_NODE,
                        inVar(1, 1),
                        AbstractPatternVTest.makeIntConFromLiteral(evaluateUnaryExpr(
                                pEvaluateBinaryExpr("1", "c0", Op.SHIFTL),
                                UnaryNode.Op.MINUS)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XURShiftC0_LShiftC0() {
        String actual = translate("p_XURShiftC0_LShiftC0");
        String expected = "if (in(1)->Opcode() == Op_URShiftI\n" +
                "    && in(2)->Opcode() == Op_ConI\n" +
                "    && in(1)->in(2) == in(2)) {\n" +
                "jint c0 = phase->type(in(2))->isa_int()->get_con();\n" +
                "return new AndINode(in(1)->in(1), phase->intcon(-(1 << c0)));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP__XRShiftC0_AndY_LShiftC0() {
        String actual = translate("p__XRShiftC0_AndY_LShiftC0");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(1)->Opcode() == Op_RShiftI\n" +
                "    && in(2)->Opcode() == Op_ConI\n" +
                "    && in(1)->in(1)->in(2) == in(2)) {\n" +
                "return new AndINode(in(1)->in(1)->in(1), phase->transform(new LShiftINode(in(1)->in(2), in(2))));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP__XURShiftC0_AndY_LShiftC0() {
        String actual = translate("p__XURShiftC0_AndY_LShiftC0");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(1)->Opcode() == Op_URShiftI\n" +
                "    && in(2)->Opcode() == Op_ConI\n" +
                "    && in(1)->in(1)->in(2) == in(2)) {\n" +
                "return new AndINode(in(1)->in(1)->in(1), phase->transform(new LShiftINode(in(1)->in(2), in(2))));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP_XAndRightNBits_LShiftC0() {
        String actual = translate("p_XAndRightNBits_LShiftC0");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.BIN_AND, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(1, 2)),
                        AbstractPatternVTest.makeIsConInt(inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c1", inVar(1, 2)) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("c0", inVar(2)) + nL() +
                ifStart(evaluateBinaryExpr("c1",
                        // 1 << (32 - c0)) - 1
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr("1",
                                        pEvaluateBinaryExpr(AbstractPatternVTest.makeCastToJInt("32"), "c0", Op.SUB),
                                        Op.SHIFTL),
                                AbstractPatternVTest.makeCastToJInt("1"),
                                Op.SUB),
                        Op.EQ)) + nL() +
                ret(newNodeInt(NodeType.SHIFTL_NODE, inVar(1, 1), inVar(2))) + nL() +
                cB() + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XAndC0_RShiftC1() {
        String actual = translate("p_XAndC0_RShiftC1");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(2)->Opcode() == Op_ConI\n" +
                "    && in(2)->Opcode() == Op_ConI) {\n" +
                "jint mask = phase->type(in(1)->in(2))->isa_int()->get_con();\n" +
                "jint shift = phase->type(in(2))->isa_int()->get_con();\n" +
                "return new AndINode(phase->transform(new RShiftINode(in(1)->in(1), in(2))), phase->intcon(mask >> shift));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XURShiftA_URShiftB() {
        String actual = translate("p_XURShiftA_URShiftB");
        String expected = "if (in(1)->Opcode() == Op_URShiftI\n" +
                "    && in(1)->in(2)->Opcode() == Op_ConI\n" +
                "    && in(2)->Opcode() == Op_ConI) {\n" +
                "jint a = phase->type(in(1)->in(2))->isa_int()->get_con();\n" +
                "jint b = phase->type(in(2))->isa_int()->get_con();\n" +
                "a = a & 31;\n" +
                "if ((java_add(a, b)) < 32) {\n" +
                "return new URShiftINode(in(1)->in(1), phase->intcon(java_add(a, b)));\n" +
                "}\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testP__XLShiftZ_PlusY_URShiftZ() {
        String actual = translate("p__XLShiftZ_PlusY_URShiftZ");
        String expected = oB() + nL() +
                declInVar(1) + nL() +
                declInVar(1, 1) + nL() +
                declInVar(1, 1, 1) + nL() +
                declInVar(1, 1, 2) + nL() +
                declInVar(1, 2) + nL() +
                declInVar(2) + nL() +
                ifStart(checkOpInt(Op.ADD, 1),
                        checkOpInt(Op.SHIFTL, 1, 1),
                        AbstractPatternVTest.makeIsConInt(inVar(2)),
                        makeEqual(inVar(1, 1, 2), inVar(2))) + nL() +
                AbstractPatternVTest.makeNewIntConDeclStmt("Z", inVar(2)) + nL() +
                ret(newNodeInt(NodeType.BIN_AND_NODE,
                        tNewNodeInt(NodeType.ADD_NODE,
                                inVar(1, 1, 1),
                                tNewNodeInt(NodeType.SHIFTUR_NODE,
                                        inVar(1, 2),
                                        inVar(2))),
                        AbstractPatternVTest.makeIntConFromLiteral(evaluateBinaryExpr(
                                evaluateBinaryExpr(
                                        "1",
                                        evaluateBinaryExpr(
                                                AbstractPatternVTest.makeCastToJInt("32"),
                                                "Z",
                                                Op.SUB
                                        ),
                                        Op.SHIFTL
                                ),
                                AbstractPatternVTest.makeCastToJInt("1"),
                                Op.SUB)))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XAndMask_URShiftZ() {
        String actual = translate("p_XAndMask_URShiftZ");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(2)->Opcode() == Op_ConI\n" +
                "    && in(2)->Opcode() == Op_ConI) {\n" +
                "jint mask = phase->type(in(1)->in(2))->isa_int()->get_con();\n" +
                "jint z = phase->type(in(2))->isa_int()->get_con();\n" +
                "return new AndINode(phase->transform(new URShiftINode(in(1)->in(1), in(2))), phase->intcon(mask >> z));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XLShiftZ_URShiftZ() {
        String actual = translate("p_XLShiftZ_URShiftZ");
        String expected = "if (in(1)->Opcode() == Op_LShiftI\n" +
                "    && in(2)->Opcode() == Op_ConI\n" +
                "    && in(1)->in(2) == in(2)) {\n" +
                "jint z = phase->type(in(2))->isa_int()->get_con();\n" +
                "return new AndINode(in(1)->in(1), phase->intcon(java_subtract(1 << java_subtract(32, z), 1)));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testP_XRShiftN_URShift31() {
        String actual = translate("p_XRShiftN_URShift31");
        String expected = "if (in(1)->Opcode() == Op_RShiftI\n" +
                "    && in(1)->in(2)->Opcode() == Op_ConI\n" +
                "    && phase->type(in(2))->isa_int()->is_con(31)) {\n" +
                "return new URShiftINode(in(1)->in(1), in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPXRotateLeftC() {
        String actual = translate("pXRotateLeftC");
        String expected = "if (in(2)->Opcode() == Op_ConI) {\n" +
                "jint c = phase->type(in(2))->isa_int()->get_con();\n" +
                "return new RotateRightNode(in(1), phase->intcon(java_subtract(32, c & 31)), TypeInt::INT);\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNewDeMorganLawAndToOr() {
        String actual = translate("pNewDeMorganLawAndToOr");
        String expected = "if (in(1)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(1)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(2) == in(2)->in(2)) {\n" +
                "return new XorINode(phase->transform(new OrINode(in(1)->in(1), in(2)->in(1))), phase->intcon(-1));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNewDeMorganWithReassociationAndToOr() {
        String actual = translate("pNewDeMorganWithReassociationAndToOr");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(2)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(2)->in(2) == in(2)->in(2)) {\n" +
                "return new AndINode(in(1)->in(1), phase->transform(new XorINode(phase->transform(new OrINode(in(1)->in(2)->in(1), in(2)->in(1))), phase->intcon(-1))));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNewDeMorganWithReassociationAndToOrSym() {
        String actual = translate("pNewDeMorganWithReassociationAndToOrSym");
        String expected = "if (in(1)->Opcode() == Op_AndI\n" +
                "    && in(1)->in(1)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1)->in(2) == in(2)->in(2)) {\n" +
                "return new AndINode(in(1)->in(2), phase->transform(new XorINode(phase->transform(new OrINode(in(1)->in(1)->in(1), in(2)->in(1))), phase->intcon(-1))));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrB_And_NotAAndB_() {
        String actual = translate("pNew_AOrB_And_NotAAndB_");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(2)->Opcode() == Op_XorI\n" +
                "    && in(2)->in(1)->Opcode() == Op_AndI\n" +
                "    && phase->type(in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(1)->in(1) == in(2)->in(1)->in(1)\n" +
                "    && in(1)->in(2) == in(2)->in(1)->in(2)) {\n" +
                "return new XorINode(in(1)->in(1), in(1)->in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrB_And_NotAAndB_Sym() {
        String actual = translate("pNew_AOrB_And_NotAAndB_Sym");
        String expected = "if (in(1)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1)->Opcode() == Op_AndI\n" +
                "    && phase->type(in(1)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(1)->in(1) == in(2)->in(1)\n" +
                "    && in(1)->in(1)->in(2) == in(2)->in(2)) {\n" +
                "return new XorINode(in(2)->in(1), in(2)->in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrB_And_NotBAndA_() {
        String actual = translate("pNew_AOrB_And_NotBAndA_");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(2)->Opcode() == Op_XorI\n" +
                "    && in(2)->in(1)->Opcode() == Op_AndI\n" +
                "    && phase->type(in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(1)->in(1) == in(2)->in(1)->in(2)\n" +
                "    && in(1)->in(2) == in(2)->in(1)->in(1)) {\n" +
                "return new XorINode(in(1)->in(1), in(1)->in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrB_And_NotBAndA_Sym() {
        String actual = translate("pNew_AOrB_And_NotBAndA_Sym");
        String expected = "if (in(1)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1)->Opcode() == Op_AndI\n" +
                "    && phase->type(in(1)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(1)->in(1) == in(2)->in(2)\n" +
                "    && in(1)->in(1)->in(2) == in(2)->in(1)) {\n" +
                "return new XorINode(in(2)->in(1), in(2)->in(2));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrNotB_And_NotAOrB_1() {
        String actual = translate("pNew_AOrNotB_And_NotAOrB_1");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(2)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(1)->in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(2)->in(1)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1) == in(2)->in(1)->in(1)\n" +
                "    && in(1)->in(2)->in(1) == in(2)->in(2)\n" +
                "    && in(1)->in(2)->in(2) == in(2)->in(1)->in(2)) {\n" +
                "return new XorINode(phase->transform(new XorINode(in(1)->in(1), in(2)->in(2))), phase->intcon(-1));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrNotB_And_NotAOrB_2() {
        String actual = translate("pNew_AOrNotB_And_NotAOrB_2");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(2)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(1)->in(2)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(2)->in(2)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1) == in(2)->in(2)->in(1)\n" +
                "    && in(1)->in(2)->in(1) == in(2)->in(1)\n" +
                "    && in(1)->in(2)->in(2) == in(2)->in(2)->in(2)) {\n" +
                "return new XorINode(phase->transform(new XorINode(in(1)->in(1), in(2)->in(1))), phase->intcon(-1));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrNotB_And_NotAOrB_3() {
        String actual = translate("pNew_AOrNotB_And_NotAOrB_3");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(1)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(1)->in(1)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(2)->in(1)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1)->in(1) == in(2)->in(2)\n" +
                "    && in(1)->in(1)->in(2) == in(2)->in(1)->in(2)\n" +
                "    && in(1)->in(2) == in(2)->in(1)->in(1)) {\n" +
                "return new XorINode(phase->transform(new XorINode(in(1)->in(2), in(2)->in(2))), phase->intcon(-1));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testPNew_AOrNotB_And_NotAOrB_4() {
        String actual = translate("pNew_AOrNotB_And_NotAOrB_4");
        String expected = "if (in(1)->Opcode() == Op_OrI\n" +
                "    && in(1)->in(1)->Opcode() == Op_XorI\n" +
                "    && phase->type(in(1)->in(1)->in(2)) == TypeInt::MINUS_1\n" +
                "    && in(2)->Opcode() == Op_OrI\n" +
                "    && in(2)->in(2)->Opcode() == Op_XorI\n" +
                "    && in(1)->in(1)->in(1) == in(2)->in(1)\n" +
                "    && in(1)->in(1)->in(2) == in(2)->in(2)->in(2)\n" +
                "    && in(1)->in(2) == in(2)->in(2)->in(1)) {\n" +
                "return new XorINode(phase->transform(new XorINode(in(1)->in(2), in(2)->in(1))), phase->intcon(-1));\n" +
                "}\n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_And_Not_AAndC_OrB_() {
        String actual = translate("pNew_Not_AAndB_OrC_And_Not_AAndC_OrB_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_AND, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2)),
                                inVar(1, 1, 1, 1)),
                        inVar(1, 1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_And_Not_BAndC_OrA_() {
        String actual = translate("pNew_Not_AAndB_OrC_And_Not_BAndC_OrA_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_AND, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.XOR_NODE, inVar(2, 2), inVar(1, 2)),
                                inVar(1, 1, 1, 2)),
                        inVar(1, 1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_AndNot_AAndC_() {
        String actual = translate("pNew_Not_AAndB_OrC_AndNot_AAndC_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1, 1, 2), inVar(1, 2)),
                                inVar(2, 1, 1)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_AndNot_AAndC_Sym() {
        String actual = translate("pNew_Not_AAndB_OrC_AndNot_AAndC_Sym");
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
                        checkOpInt(Op.BIN_AND, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_AND, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(2, 1, 1, 2), inVar(2, 2)),
                                inVar(1, 1, 1)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_AndNot_BAndC_() {
        String actual = translate("pNew_Not_AAndB_OrC_AndNot_BAndC_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.XOR, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(1, 1, 1, 1), inVar(1, 2)),
                                inVar(2, 1, 1)),
                        inVar(2, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_Not_AAndB_OrC_AndNot_BAndC_Sym() {
        String actual = translate("pNew_Not_AAndB_OrC_AndNot_BAndC_Sym");
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
                        checkOpInt(Op.BIN_AND, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.XOR, 2, 1),
                        checkOpInt(Op.BIN_AND, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.XOR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
                                tNewNodeInt(NodeType.BIN_OR_NODE, inVar(2, 1, 1, 1), inVar(2, 2)),
                                inVar(1, 1, 1)),
                        inVar(1, 2))) + nL() +
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew__NotAOrB_OrC_AndNot__AAndB_AndC_() {
        String actual = translate("pNew__NotAOrB_OrC_AndNot__AAndB_AndC_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        checkOpInt(Op.BIN_AND, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)), // A
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)), // -1
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 1, 2)), // B
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() + // C
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1, 1, 1), // A
                                inVar(2, 2)), // -1
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1, 2), // B
                                inVar(1, 2)))) + nL() + // C
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew__NotAOrB_OrC_AndNot__AAndB_AndC_Sym() {
        String actual = translate("pNew__NotAOrB_OrC_AndNot__AAndB_AndC_Sym");
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
                        checkOpInt(Op.BIN_AND, 1, 1),
                        checkOpInt(Op.BIN_AND, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1, 1)), // A
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 1, 2)), // B
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)), // C
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() + // -1
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(1, 1, 1, 1), // A
                                inVar(1, 2)), // -1
                        tNewNodeInt(NodeType.XOR_NODE,
                                inVar(2, 1, 2), // B
                                inVar(2, 2)))) + nL() + // C
                cB() + nL() +
                cB() + nL();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPNew_NotAOrBOrC_And_Not_AAndB_() {
        String actual = translate("pNew_NotAOrBOrC_And_Not_AAndB_");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
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
    public void testPNew_NotAOrBOrC_And_Not_AAndB_Sym() {
        String actual = translate("pNew_NotAOrBOrC_And_Not_AAndB_Sym");
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
                        checkOpInt(Op.BIN_AND, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 1, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
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
    public void testPNew_NotAOrBOrC_And_Not_AAndC() {
        String actual = translate("pNew_NotAOrBOrC_And_Not_AAndC");
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
                ifStart(checkOpInt(Op.BIN_OR, 1),
                        checkOpInt(Op.BIN_OR, 1, 1),
                        checkOpInt(Op.XOR, 1, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(2, 2), -1),
                        checkOpInt(Op.XOR, 2),
                        checkOpInt(Op.BIN_AND, 2, 1),
                        makeEqual(inVar(1, 1, 1, 1), inVar(2, 1, 1)),
                        makeEqual(inVar(1, 1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
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
    public void testPNew_NotAOrBOrC_And_Not_AAndCSym() {
        String actual = translate("pNew_NotAOrBOrC_And_Not_AAndCSym");
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
                        checkOpInt(Op.BIN_AND, 1, 1),
                        AbstractPatternVTest.makeIsConLiteral(inVar(1, 2), -1),
                        checkOpInt(Op.BIN_OR, 2),
                        checkOpInt(Op.BIN_OR, 2, 1),
                        checkOpInt(Op.XOR, 2, 1, 1),
                        makeEqual(inVar(1, 1, 1), inVar(2, 1, 1, 1)),
                        makeEqual(inVar(1, 1, 2), inVar(2, 2)),
                        makeEqual(inVar(1, 2), inVar(2, 1, 1, 2))) + nL() +
                ret(newNodeInt(NodeType.BIN_OR_NODE,
                        tNewNodeInt(NodeType.BIN_AND_NODE,
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
