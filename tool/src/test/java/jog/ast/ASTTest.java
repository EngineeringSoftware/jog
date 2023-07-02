package jog.ast;

import jog.ast.expr.BinNode;
import jog.ast.expr.BinNode.Op;
import jog.ast.expr.BinNode.NodeType;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.visitor.AfterVisitor;
import jog.ast.visitor.BeforeVisitor;
import jog.ast.visitor.NodeCodeGenBuilder;
import jog.ast.visitor.PredVisitor;
import jog.ast.visitor.PrepareVisitor;

import org.junit.Assert;
import org.junit.Test;

import static jog.codegen.AbstractPatternVTest.*;
import static jog.ast.visitor.CodeGenUtil.*;

public class ASTTest {

    @Test
    public void testBefore() {
        // (x + 1) + c
        CGExpr beforeNode = new BinNode(
                new BinNode(new VarNode("x", ValType.INT), new LitNode(1, ValType.INT), BinNode.Op.ADD, ValType.INT),
                new ConNode("c", ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        NodeCodeGenBuilder nodeCodeGenBuilder = new NodeCodeGenBuilder();
        beforeNode.accept(nodeCodeGenBuilder, null);
        BeforeVisitor v = new BeforeVisitor(nodeCodeGenBuilder.getNodeToCodeGen(),
                nodeCodeGenBuilder.getNodeToCodeGenList());
        beforeNode.accept(v, null);

        Assert.assertEquals(
                and(checkOpInt(Op.ADD, 1),
                        makeIsConLiteral(inVar(1, 2), 1),
                        makeIsConInt(inVar(2))),
                v.getResult());
    }

    @Test
    public void testAfterWithConstantFolding() {
        // (x + 1) + c
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr one = new LitNode(1, ValType.INT);
        CGExpr c = new ConNode("c", ValType.INT);
        CGExpr beforeNode = new BinNode(
                new BinNode(x, one, BinNode.Op.ADD, ValType.INT),
                c,
                BinNode.Op.ADD, ValType.INT);
        // x + (1 + c)
        CGExpr afterNode = new BinNode(
                x,
                new BinNode(one, c, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        CGAfterStmt afterStmt = new CGAfterStmt(afterNode);
        CGBeforeStmt cgAST = new CGBeforeStmt(beforeNode, afterStmt);

        PrepareVisitor pv = new PrepareVisitor();
        cgAST = (CGBeforeStmt) cgAST.accept(pv);
        pv.finish();
        AfterVisitor v = new AfterVisitor(pv.getNodeToCodeGen());
        ((CGAfterStmt) ((CGBlockStmt) cgAST.getThenStmt()).getStmts().get(1)).getExpression()
                .accept(v, null);

        Assert.assertEquals(
                newNodeInt(NodeType.ADD_NODE,
                        inVar(1, 1),
                        makeIntConFromLiteral(evaluateBinaryExpr(makeCastToJInt("1"), "c", Op.ADD))),
                v.getResult());
    }

    @Test
    public void testAfterWithPhaseTransform() {
        // a * b + a * c
        CGExpr a = new VarNode("a", ValType.INT);
        CGExpr b = new VarNode("b", ValType.INT);
        CGExpr c = new VarNode("c", ValType.INT);
        CGExpr beforeNode = new BinNode(
                new BinNode(a, b, BinNode.Op.MUL, ValType.INT),
                new BinNode(a, c, BinNode.Op.MUL, ValType.INT),
                BinNode.Op.ADD, ValType.INT);
        NodeCodeGenBuilder nodeCodeGenBuilder = new NodeCodeGenBuilder();
        beforeNode.accept(nodeCodeGenBuilder, null);
        // a * (b + c)
        CGExpr afterNode = new BinNode(
                a,
                new BinNode(b, c, BinNode.Op.ADD, ValType.INT),
                BinNode.Op.MUL, ValType.INT);

        AfterVisitor v = new AfterVisitor(nodeCodeGenBuilder.getNodeToCodeGen());
        afterNode.accept(v, null);

        Assert.assertEquals(
                newNodeInt(NodeType.MUL_NODE,
                        inVar(1, 1),
                        tNewNodeInt(NodeType.ADD_NODE, inVar(1, 2), inVar(2, 2))),
                v.getResult());
    }

    @Test
    public void testPred() {
        // before: (x >>> Z) + Y
        CGExpr x = new VarNode("x", ValType.INT);
        CGExpr Z = new ConNode("Z", ValType.INT);
        CGExpr Y = new ConNode("Y", ValType.INT);
        CGExpr beforeNode = new BinNode(
                new BinNode(x, Z, BinNode.Op.SHIFTUR, ValType.INT),
                Y,
                BinNode.Op.ADD, ValType.INT
        );
        NodeCodeGenBuilder nodeCodeGenBuilder = new NodeCodeGenBuilder();
        beforeNode.accept(nodeCodeGenBuilder, null);

        // pred: Z < 5 && (-5 < Y) && (Y < 0) && (X >= -(Y << Z))
        CGExpr predNode = new BinNode(
                new BinNode(
                        new BinNode(
                                new BinNode(Z, new LitNode(5, ValType.INT), BinNode.Op.LT, ValType.INT),
                                new BinNode(new LitNode(-5, ValType.INT), Y, BinNode.Op.LT, ValType.INT),
                                BinNode.Op.LOGIC_AND, ValType.INT),
                        new BinNode(Y, new LitNode(0, ValType.INT), BinNode.Op.LT, ValType.INT),
                        BinNode.Op.LOGIC_AND, ValType.INT),
                new BinNode(x, new UnaryNode(new BinNode(Y, Z, BinNode.Op.SHIFTL, ValType.INT), UnaryNode.Op.MINUS, ValType.INT), BinNode.Op.GE, ValType.INT),
                BinNode.Op.LOGIC_AND, ValType.INT
        );
        PredVisitor v = new PredVisitor(nodeCodeGenBuilder.getNodeToCodeGen());
        predNode.accept(v, null);
        Assert.assertEquals(
                evaluateBinaryExpr(
                        pEvaluateBinaryExpr(
                                pEvaluateBinaryExpr(
                                        pEvaluateBinaryExpr("Z", "5", Op.LT),
                                        pEvaluateBinaryExpr("-5", "Y", Op.LT),
                                        Op.LOGIC_AND),
                                pEvaluateBinaryExpr("Y", "0", Op.LT),
                                Op.LOGIC_AND),
                        pEvaluateBinaryExpr(
                                makeGetLowInt(inVar(1, 1)),
                                pEvaluateUnaryExpr(pEvaluateBinaryExpr("Y", "Z", Op.SHIFTL), UnaryNode.Op.MINUS),
                                Op.GE),
                        Op.LOGIC_AND),
                //"(((Z < 5) && (-5 < Y)) && (Y < 0)) && (phase->type(in(1)->in(1))->isa_int()->_lo >= (-(Y << Z)))",
                v.getResult());
    }
}
