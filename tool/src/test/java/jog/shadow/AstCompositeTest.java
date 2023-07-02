package jog.shadow;

import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.codegen.PatternV;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AstCompositeTest extends AstRelationAbstractTest {

    /**
     * Make an AstComposite instance with p1's after as ast1 and p2's
     * before as ast2.
     */
    private AstComposite makeComposite(String p1Name, String p2Name) {
        PatternV p1 = patternsByName.get(p1Name);
        CGExpr ast1 = p1.getAfterNode();
        PatternV p2 = patternsByName.get(p2Name);
        CGExpr ast2 = p2.getBeforeNode();
        return new AstComposite(ast1, ast2,
                p1.getPreconditions(), p2.getPreconditions(),
                p1.getBeforeNode(), p2.getAfterNode());
    }

    private static void assertPatternSEquals(PatternS expected, PatternS actual) {
        PatternRenamer.rename(expected, "x");
        PatternRenamer.rename(actual, "x");
        Assert.assertEquals(expected.before.toString(), actual.before.toString());
        Assert.assertEquals(expected.after.toString(), actual.after.toString());
        Assert.assertEquals(expected.preconditions.stream().map(Object::toString)
                        .collect(Collectors.toCollection(TreeSet::new)),
                actual.preconditions.stream().map(Object::toString)
                        .collect(Collectors.toCollection(TreeSet::new)));
    }

    /**
     * pSub13's composites pAddMoveConstantRight
     * A-(B-C) => (A+C)-B // pre: Lib.outcnt(B - C) == 1
     * ->
     * con + x => x + con
     *
     * con-(B-x) => (x+con)-B // pre: Lib.outcnt(B-x) == 1
     */
    @Test
    public void testPSub13CompositePAddMoveConstantRight() {
        AstComposite ac = makeComposite("pSub13", "pAddMoveConstantRight");
        Assert.assertTrue(ac.isOK());
        CGExpr con = new ConNode("con", ValType.INT);
        CGExpr B = new VarNode("B", ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr before = new BinNode(
                con,
                new BinNode(B, x, BinNode.Op.SUB, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        CGExpr after = new BinNode(
                new BinNode(x, con, BinNode.Op.ADD, ValType.INT),
                B,
                BinNode.Op.SUB, ValType.INT);
        CGExpr precondition = new BinNode(
                new CallNode(CallNode.Name.OUT_CNT, ValType.INT, new BinNode(B, x, BinNode.Op.SUB, ValType.INT)),
                new LitNode(1, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        assertPatternSEquals(new PatternS(before, after, precondition), ac.composite());
    }

    /**
     * pSub13's composites pSub1
     * A-(B-C) => (A+C)-B // pre: Lib.outcnt(B - C) == 1
     * ->
     * x - c0 => x + (-c0)
     *
     * A - (c0 - C) => (A + C) + -c0 // pre: Lib.outcnt(c0 - C) == 1
     */
    @Test
    public void testPSub13CompositePSub1() {
        AstComposite ac = makeComposite("pSub13", "pSub1");
        Assert.assertTrue(ac.isOK());
        CGExpr A = new VarNode("A", ValType.INT);
        CGExpr c0 = new ConNode("c0", ValType.INT);
        CGExpr C = new VarNode("C", ValType.INT);
        CGExpr before = new BinNode(
                A,
                new BinNode(c0, C, BinNode.Op.SUB, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        CGExpr after = new BinNode(
                new BinNode(A, C, BinNode.Op.ADD, ValType.INT),
                new ConstantFoldingNode(new UnaryNode(c0, UnaryNode.Op.MINUS, ValType.INT),
                        ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        CGExpr precondition = new BinNode(
                new CallNode(CallNode.Name.OUT_CNT, ValType.INT, new BinNode(c0, C, BinNode.Op.SUB, ValType.INT)),
                new LitNode(1, ValType.INT),
                BinNode.Op.EQ, ValType.INT);
        assertPatternSEquals(new PatternS(before, after, precondition), ac.composite());
    }

    /**
     * pSub1 composites pAddConstantAssociative
     * x - c0 => x + (-c0)
     * ->
     * (x + con1) + con2 => x + (con1 + con2)
     *
     * (x + con1) - c0 => x + (con1 + (-c0))
     */
    @Test
    public void testPSub1CompositePAddConstantAssociative() {
        AstComposite ac = makeComposite("pSub1", "pAddConstantAssociative");
        Assert.assertTrue(ac.isOK());
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr con1 = new ConNode("con1", ValType.INT);
        CGExpr c0 = new ConNode("c0", ValType.INT);
        CGExpr before = new BinNode(
                new BinNode(x, con1, BinNode.Op.ADD, ValType.INT),
                c0,
                BinNode.Op.SUB, ValType.INT);
        CGExpr after = new BinNode(
                x,
                new ConstantFoldingNode(new BinNode(con1, new UnaryNode(c0, UnaryNode.Op.MINUS, ValType.INT), BinNode.Op.ADD, ValType.INT),
                        ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        assertPatternSEquals(new PatternS(before, after), ac.composite());
    }

    /*------------------------- Make-up. ---------------------------*/

    @Test
    public void testSimpleFullMatch() {
        // x & x
        // u & v
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr ast1 = new BinNode(x, x, BinNode.Op.BIN_AND, ValType.INT);
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast2 = new BinNode(u, v, BinNode.Op.BIN_AND, ValType.INT);
        AstComposite ac = new AstComposite(ast1, ast2);
        Assert.assertTrue(ac.isOK());
    }

    @Test
    public void testSimplePartialMatch() {
        // (x & x) + 1
        // u & v
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr ast1 = new BinNode(
                new BinNode(x, x, BinNode.Op.BIN_AND, ValType.INT),
                new LitNode(1, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast2 = new BinNode(u, v, BinNode.Op.BIN_AND, ValType.INT);
        AstComposite ac = new AstComposite(ast1, ast2);
        Assert.assertTrue(ac.isOK());
    }

    @Test
    public void testTwoSameInternalNodesDoesComposite() {
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
        AstComposite ac = new AstComposite(ast1, ast2);
        Assert.assertTrue(ac.isOK());
    }

    @Test
    public void testShapeDoesNotMatch() {
        // u + v
        // x - y
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast1 = new BinNode(u, v, BinNode.Op.ADD, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(x, y, BinNode.Op.SUB, ValType.INT);
        AstComposite ac = new AstComposite(ast1, ast2);
        Assert.assertTrue(ac.isNotOK());
    }

    @Test
    public void testLitMatchVarComposite() {
        // (u + v) - 1
        // (x + y) - z
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast1 = new BinNode(
                new BinNode(u, v, BinNode.Op.ADD, ValType.INT),
                new LitNode(1, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                new VarNode("z", ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        AstComposite ac = new AstComposite(ast1, ast2);
        Assert.assertTrue(ac.isOK());
    }

    @Test
    public void testLitMatchVarWithPreconditionNotComposite() {
        // (u + v) - 1
        // (x + y) - z // pred: z != 1
        CGExpr u = new VarNode("u", ValType.INT);
        CGExpr v = new VarNode("v", ValType.INT);
        CGExpr ast1 = new BinNode(
                new BinNode(u, v, BinNode.Op.ADD, ValType.INT),
                new LitNode(1, ValType.INT),
                BinNode.Op.SUB, ValType.INT);
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr y = new VarNode("y", ValType.INT);
        CGExpr z = new VarNode("z", ValType.INT);
        CGExpr ast2 = new BinNode(
                new BinNode(x, y, BinNode.Op.ADD, ValType.INT),
                z,
                BinNode.Op.SUB, ValType.INT);
        CGExpr pred = new BinNode(
                z,
                new LitNode(1, ValType.INT),
                BinNode.Op.NE, ValType.INT);
        AstComposite ac = new AstComposite(ast1, ast2, null, List.of(pred), null, null);
        Assert.assertTrue(ac.isNotOK());
    }
}
