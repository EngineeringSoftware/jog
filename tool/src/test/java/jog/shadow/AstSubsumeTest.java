package jog.shadow;

import jog.api.Lib;
import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.codegen.PatternV;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AstSubsumeTest extends AstRelationAbstractTest {

    public AstSubsumeTest() {
        super();
    }

    /**
     * Make an AstSubsume instance with p1's before as ast1 and p2's
     * before as ast2.
     */
    private AstSubsume makeSubsume(String p1Name, String p2Name) {
        PatternV p1 = patternsByName.get(p1Name);
        CGExpr ast1 = p1.getBeforeNode();
        PatternV p2 = patternsByName.get(p2Name);
        CGExpr ast2 = p2.getBeforeNode();
        return new AstSubsume(ast1, ast2, p1.getPreconditions(), p2.getPreconditions());
    }

    /**
     * AddNode pSub3's shadows AddNode pNewSubAddSub1574
     * x - (y + c0) // pre: Lib.okToConvert(y + c0, x)
     * ->
     * c0 - (x + c1) // pre: Lib.okToConvert(x + c1, c0)
     */
    @Test
    public void testPSub3ShadowPNewSubAddSub1574() {
        AstSubsume as = makeSubsume("pSub3", "pNewSubAddSub1574");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode PNewAddAddSub1202's before subsumes AddNode pAddNotXPlusOne's before.
     * (x ^ -1) + c -> (x ^ -1) + 1
     */
    @Test
    public void testPNewAddAddSub1202ShadowspAddNotXPlusOne() {
        AstSubsume as = makeSubsume("pNewAddAddSub1202", "pAddNotXPlusOne");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode P2's before subsumes AddNode P5's before.
     * (a - b) + (c - d) -> (a - b) + (b - c)
     */
    @Test
    public void testPAdd2ShadowsPAdd5() {
        AstSubsume as = makeSubsume("pAdd2", "pAdd5");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode P2's before subsumes AddNode P6's before.
     * (a - b) + (c - d) -> (a - b) + (c - a)
     */
    @Test
    public void testPAddShadowsPAdd6() {
        AstSubsume as = makeSubsume("pAdd2", "pAdd6");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode P2's before subsumes AddNode PNewAddSub1165's before.
     * (a - b) + (c - d) -> (0 - a) + (0 - b)
     */
    @Test
    public void testPAdd2ShadowsPNewAddSub1165() {
        AstSubsume as = makeSubsume("pAdd2", "pNewAddAddSub1165");
        Assert.assertTrue(as.isOK());
    }

    /**
     * SubNode PNewAddSub1574's before does not subsume SubNode P8's before because of preconditions.
     * c0 - (x + c1) // pre: Lib.okToConvert(x + c1, c0)
     * -/->
     * 0 - (x + con)// pre: con != 0
     */
    @Test
    public void testPNewAddSub1574DoesNotShadowPSub8() {
        AstSubsume as = makeSubsume("pNewSubAddSub1574", "pSub8");
        Assert.assertTrue(as.isNotOK());
    }


    /**
     * SubNode P13's before subsumes SubNode PNewAddSub1539's before because of preconditions.
     * A - (B - C) // pre: Lib.outcnt(B - C) == 1
     * ->
     * x - (0 - y)
     */
    @Test
    public void testPSub13DoesNotShadowPNewAddSub1539() {
        AstSubsume as = makeSubsume("pSub13", "pNewSubAddSub1539");
        Assert.assertTrue(as.isNotOK());
    }

    /**
     * SubNode P10's before does not subsume SubNode P9's before.
     * (A + X) - (B + X) -/-> (X + A) - (X + B)
     */
    @Test
    public void testPSub10DoesNotShadowPSub9() {
        AstSubsume as = makeSubsume("pSub10", "pSub9");
        Assert.assertTrue(as.isNotOK());
    }

    /**
     * AddNode pNewXPlus_ConMinusY_'s before subsumes AddNode pAdd7's before.
     * x + (con - y) -> x + (0 - y)
     */
    @Test
    public void testPNewXPlus_ConMinusY_ShadowsPAdd7() {
        AstSubsume as = makeSubsume("pNewXPlus_ConMinusY_", "pAdd7");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode pNewXPlus_ConMinusY_'s before subsumes AddNode pNewAddAddSub1165's before.
     * x + (con - y) -> (0 - a) + (0 - b)
     */
    @Test
    public void testPNewXPlus_ConMinusY_ShadowsPNewAddAddSub1165() {
        AstSubsume as = makeSubsume("pNewXPlus_ConMinusY_", "pNewAddAddSub1165");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode pNewXPlus_ConMinusY_'s before does NOT subsume AddNode pNewAddAddSub1165's before.
     * (con - y) + x -> (0 - a) + (0 - b)
     */
    @Test
    public void testPNewXPlus_ConMinusY_SymShadowsPNewAddAddSub1165() {
        AstSubsume as = makeSubsume("pNewXPlus_ConMinusY_Sym", "pNewAddAddSub1165");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode pNewXPlus_ConMinusY_Sym's before subsumes AddNode pAdd1's before.
     * (con - y) + x -> (con1 - x) + con2
     */
    @Test
    public void testPNewXPlus_ConMinusY_SymShadowsPAdd1() {
        AstSubsume as = makeSubsume("pNewXPlus_ConMinusY_Sym", "pAdd1");
        Assert.assertTrue(as.isOK());
    }

    /**
     * SubNode pNewXPlus_ConMinusY_Sym's before subsumes AddNode pAdd8's before.
     * (con - y) + x -> (0 - y) + x
     */
    @Test
    public void testPNewXPlus_ConMinusY_SymShadowsPAdd8() {
        AstSubsume as = makeSubsume("pNewXPlus_ConMinusY_Sym", "pAdd8");
        Assert.assertTrue(as.isOK());
    }

    /**
     * AddNode pAdd7 does not shadow AddNode pAdd9
     * x + (0 - y) -/-> (x >>> z) + y
     */
    @Test
    public void testPAdd7NotShadowPAdd9() {
        AstSubsume as = makeSubsume("pAdd7", "pAdd9");
        Assert.assertTrue(as.isNotOK());
    }

    /*------------------------- Make-up. ---------------------------*/

    @Test
    public void testImplicitPreconditionSubsume() {
        // u & v // pre: u ^ v == 0
        // x & x
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast1 = new BinNode(u, v, BinNode.Op.BIN_AND, ValType.INT);
        CGExpr pre1 = new BinNode(new BinNode(u, v, BinNode.Op.XOR, ValType.INT), new LitNode(0, ValType.INT), BinNode.Op.EQ, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr ast2 = new BinNode(x, x, BinNode.Op.BIN_AND, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, List.of(pre1), null);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testLibApiOutcntAsPreconditionSubsume() {
        // a + b
        // x + y // pre: Lib.outcnt(x) == 1
        CGExpr ast1 = new BinNode(new VarNode("a", ValType.INT),
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr ast2 = new BinNode(x,
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre2 = new BinNode(
                new CallNode(CallNode.Name.OUT_CNT, ValType.INT, x),
                new LitNode(1, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, null, List.of(pre2));
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testLibApiOutcntAsPreconditionUnsubsume() {
        // a + b // pre: Lib.outcnt(a) == 1
        // x + y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr ast1 = new BinNode(a,
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre1 = new BinNode(
                new CallNode(CallNode.Name.OUT_CNT, ValType.INT, a),
                new LitNode(1, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        CGExpr ast2 = new BinNode(new VarNode("x", ValType.INT),
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, List.of(pre1), null);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLibApiGetTypeAsPreconditionSubsume() {
        // a + b
        // x + y // pre: Lib.getType(x) == Lib.TOP
        CGExpr ast1 = new BinNode(new VarNode("a", ValType.INT),
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr ast2 = new BinNode(x,
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre2 = new BinNode(
                new CallNode(CallNode.Name.GET_TYPE, ValType.INT, x),
                new TypeNode(Lib.Type.TOP, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, null, List.of(pre2));
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testLibApiGetTypeAsPreconditionUnsubsume() {
        // a + b // pre: Lib.getType(a) == Lib.TOP
        // x + y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr ast1 = new BinNode(a,
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre1 = new BinNode(
                new CallNode(CallNode.Name.GET_TYPE, ValType.INT, a),
                new TypeNode(Lib.Type.TOP, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        CGExpr ast2 = new BinNode(new VarNode("x", ValType.INT),
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, List.of(pre1), null);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLibApiMatchRuleSupportedAsPreconditionSubsume() {
        // a << b
        // x << y // pre: Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_LEFT)
        CGExpr ast1 = new BinNode(new VarNode("a", ValType.INT),
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr ast2 = new BinNode(new VarNode("x", ValType.INT),
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre2 = new CallNode(CallNode.Name.MATCH_RULE_SUPPORTED,
                ValType.INT, new OpNode(Lib.Operator.OP_ROTATE_LEFT, ValType.INT));
        AstSubsume as = new AstSubsume(ast1, ast2, null, List.of(pre2));
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testLibApiMatchRuleSupportedAsPreconditionUnsubsume() {
        // a << b // pre: Lib.matchRuleSupported(Lib.Operator.OP_ROTATE_LEFT)
        // x << y
        CGExpr ast1 = new BinNode(new VarNode("a", ValType.INT),
                new VarNode("b", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        CGExpr pre1 = new CallNode(CallNode.Name.MATCH_RULE_SUPPORTED,
                ValType.INT, new OpNode(Lib.Operator.OP_ROTATE_LEFT, ValType.INT));
        CGExpr ast2 = new BinNode(new VarNode("x", ValType.INT),
                new VarNode("y", ValType.INT),
                BinNode.Op.SHIFTL, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, List.of(pre1), null);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testShapeMatchAndPreconditionMatch() {
        // a + b
        // x + y // pre: x != 0
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr ast1 = new BinNode(a, b, BinNode.Op.ADD, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(x, y, BinNode.Op.ADD, ValType.INT);
        CGExpr pre2 = new BinNode(x, new LitNode(0, ValType.INT), BinNode.Op.NE, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, null, List.of(pre2));
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testShapeMatchButPreconditionDoesNotMatch() {
        // a + b // pre: a != 0
        // x + y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr ast1 = new BinNode(a, b, BinNode.Op.ADD, ValType.INT);
        CGExpr pre1 = new BinNode(a, new LitNode(0, ValType.INT), BinNode.Op.NE, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(x, y, BinNode.Op.ADD, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2, List.of(pre1), null);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testSubsumesTwoSameInternalNodesComplex() {
        // a + a
        // (z + (x + y)) + (z + (x + y))
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr ast1 = new BinNode(a, a, BinNode.Op.ADD, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(z, new BinNode(x, y, BinNode.Op.ADD, ValType.INT), BinNode.Op.ADD, ValType.INT),
                new BinNode(z, new BinNode(x, y, BinNode.Op.ADD, ValType.INT), BinNode.Op.ADD, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testTwoSameInternalNodesDoesNotSubsume() {
        // a + a
        // (x + y) + (y + x)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr ast1 = new BinNode(a, a, BinNode.Op.ADD, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new BinNode(y, x, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testSubsumesTwoSameInternalNodes() {
        // a + a
        // (x + y) + (x + y)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr ast1 = new BinNode(a, a, BinNode.Op.ADD, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testShapeMatchWhenLeafMapsToInternal() {
        // (a + b) - b
        // (z + (x + y)) - (x + y)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), b, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(z, new BinNode(x, y, BinNode.Op.ADD, ValType.INT), BinNode.Op.ADD, ValType.INT),
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testShapeMatchWithSharedLeaf() {
        // (a + b) - b
        // (x + y) - y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), b, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(x, y, BinNode.Op.ADD, ValType.INT), y, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testShapeNotMatchWithSharedLeaf() {
        // (a + a) - c
        // (x + y) - y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, a, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(x, y, BinNode.Op.ADD, ValType.INT), y, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testInternalDoesNotSubsumeLeaf() {
        // (a + b) - (b + c)
        // x - (y + z)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(
                new BinNode(a, b, BinNode.Op.ADD, ValType.INT),
                new BinNode(b, c, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new ConNode("y", ValType.INT);
        CGExpr z = new ConNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(x, new BinNode(y, z, BinNode.Op.ADD, ValType.INT), BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testVarSubsumesInternal() {
        // a - c
        // (x + y) - (y + z)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(a, c, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new BinNode(y, z, BinNode.Op.SUB, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testVarSubsumesVar() {
        // (a + b) - c
        // (x + y) - y
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(x, y, BinNode.Op.ADD, ValType.INT), y, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testVarSubsumesCon() {
        // (a + b) - c
        // (con1 + con2) - con3
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr con1 = new VarNode("con1", ValType.INT);
        CGExpr con2 = new VarNode("con2", ValType.INT);
        CGExpr con3 = new VarNode("con3", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(con1, con2, BinNode.Op.ADD, ValType.INT), con3, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testVarSubsumesLit() {
        // (a + b) - c
        // (1 + 2) - 3
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr two = new LitNode(2, ValType.INT);
        CGExpr three = new LitNode(3, ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(one, two, BinNode.Op.ADD, ValType.INT), three, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }

    @Test
    public void testConDoesNotSubsumeInternal() {
        // (a + b) - con1
        // (x + y) - (u + v)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr con1 = new ConNode("con1", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), con1, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new BinNode(u, v, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testConDoesNotSubsumeVar() {
        // (con1 + b) - c
        // (x + y) - y
        CGExpr con1 = new ConNode("con1", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(con1, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(x, y, BinNode.Op.ADD, ValType.INT), y, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testConSubsumesLitAndCon() {
        // (con1 + b) - con2
        // (1 + y) - conX
        CGExpr con1 = new ConNode("con1", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr con2 = new ConNode("con2", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(con1, b, BinNode.Op.ADD, ValType.INT), con2, BinNode.Op.SUB, ValType.INT);
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr conX = new ConNode("conX", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(one, y, BinNode.Op.ADD, ValType.INT), conX, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }


    @Test
    public void testLitDoesNotSubsumeInternal() {
        // (a + b) - 1
        // (x + y) - (u + v)
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(a, b, BinNode.Op.ADD, ValType.INT), one, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new BinNode(u, v, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLitDoesNotSubsumeVar() {
        // (1 + b) - c
        // (x + y) - z
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(one, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(x, y, BinNode.Op.ADD, ValType.INT), z, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLitDoesNotSubsumeCon() {
        // (1 + b) - c
        // (con + y) - z
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(one, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr con = new ConNode("con", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(con, y, BinNode.Op.ADD, ValType.INT), z, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLitDoesNotSubsumeLitWithDifferentValue() {
        // (1 + b) - c
        // (2 + y) - z
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(one, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr two = new LitNode(2, ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(two, y, BinNode.Op.ADD, ValType.INT), z, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isNotOK());
    }

    @Test
    public void testLitSubsumesLitWithSameValue() {
        // (1 + b) - c
        // (1 + y) - z
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr ast1 = new BinNode(new BinNode(one, b, BinNode.Op.ADD, ValType.INT), c, BinNode.Op.SUB, ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(new BinNode(one, y, BinNode.Op.ADD, ValType.INT), z, BinNode.Op.SUB, ValType.INT);
        AstSubsume as = new AstSubsume(ast1, ast2);
        Assert.assertTrue(as.isOK());
    }
}
