package jog.ast.visitor;

import jog.ast.Node;
import jog.ast.expr.BinNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGStmt;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.ast.expr.ConNode;
import jog.ast.nodetype.NodeThatIsConstant;
import jog.ast.expr.CGExpr;
import jog.ast.expr.UnaryNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Do constant folding for AfterStatement;
 * Add necessary new constant declaring statements to the then block
 * of before statement;
 * Initialize nodeToCodeGen mappings.
 */
public class PrepareVisitor extends ModifierVisitor {

    private Map<CGExpr, String> nodeToCodeGen = new HashMap<>();
    private Map<CGExpr, List<String>> nodeToCodeGenList = new HashMap<>();
    private Map<String, String> varToInCallChain = new HashMap<>();

    private CGBeforeStmt beforeStmt;
    private CGAfterStmt afterStmt;
    private List<CGExpr> preconditions = new LinkedList<>();
    private final Set<String> newConIdsDeclared = new HashSet<>();
    private final List<CGGeneralStmt> newConDeclstmts = new LinkedList<>();

    public void finish() {
        // Add new constant declaring statements to the block of
        // before statement.
        CGStmt thenStmt = beforeStmt.getThenStmt();
        List<CGStmt> stmts = thenStmt instanceof CGBlockStmt ?
                ((CGBlockStmt) thenStmt).getStmts() :
                List.of(thenStmt);
        List<CGStmt> allStmts = new LinkedList<>(newConDeclstmts);
        allStmts.addAll(stmts);
        beforeStmt.setThenStmt(new CGBlockStmt(allStmts));
    }

    public Map<CGExpr, String> getNodeToCodeGen() {
        return nodeToCodeGen;
    }

    public Map<CGExpr, List<String>> getNodeToCodeGenList() {
        return nodeToCodeGenList;
    }

    public Map<String, String> getVarToInCallChain() {
        return varToInCallChain;
    }

    public CGExpr getBeforeNode() {
        return beforeStmt.getCondition();
    }

    public CGExpr getAfterNode() {
        return afterStmt.getExpression();
    }

    public List<CGExpr> getPreconditions() {
        return preconditions;
    }

    @Override
    public Node visit(CGBeforeStmt cgBeforeStmt) {
        beforeStmt = cgBeforeStmt;
        CGExpr before = cgBeforeStmt.getCondition();
        NodeCodeGenBuilder nodeCodeGenBuilder = new NodeCodeGenBuilder();
        before.accept(nodeCodeGenBuilder, null);
        nodeToCodeGen = nodeCodeGenBuilder.getNodeToCodeGen();
        nodeToCodeGenList = nodeCodeGenBuilder.getNodeToCodeGenList();
        varToInCallChain = nodeCodeGenBuilder.getVarToInCallChain();

        return super.visit(cgBeforeStmt);
    }

    @Override
    public Node visit(CGAfterStmt cgAfterStmt) {
        afterStmt = cgAfterStmt;
        // Fold constants for after statement
        CGExpr after = cgAfterStmt.getExpression();
        after.accept(new ModifierVisitor() {
            @Override
            public Node visit(BinNode binNode) {
                CGExpr left = (CGExpr) binNode.getLeft().accept(this);
                CGExpr right = (CGExpr) binNode.getRight().accept(this);
                Node ret = null;
                if (left instanceof NodeThatIsConstant
                        && right instanceof NodeThatIsConstant) {
                    // If we want to do constant folding we need to
                    // declare a var that is assigned by the value of the
                    // constant.
                    left = handleNodeThatIsConstant(left);
                    right = handleNodeThatIsConstant(right);
                    binNode.setLeft(left);
                    binNode.setRight(right);
                    ret = new ConstantFoldingNode(binNode, binNode.getValType());
                } else {
                    binNode.setLeft(left);
                    binNode.setRight(right);
                    ret = binNode;
                }
                return ret;
            }

            @Override
            public Node visit(UnaryNode unaryNode) {
                CGExpr operand = (CGExpr) unaryNode.getOperand().accept(this);
                if (operand instanceof NodeThatIsConstant) {
                    operand = handleNodeThatIsConstant(operand);
                    unaryNode.setOperand(operand);
                    return new ConstantFoldingNode(unaryNode, unaryNode.getValType());
                } else {
                    unaryNode.setOperand(operand);
                    return unaryNode;
                }
            }

            private CGExpr handleNodeThatIsConstant(CGExpr node) {
                if (node instanceof ConNode) {
                    addNewConDeclStmt((ConNode) node);
                } else if (node instanceof ConstantFoldingNode) {
                    // remove nested constant folding node
                    node = ((ConstantFoldingNode) node).getExpr();
                }
                return node;
            }
        });
        cgAfterStmt.setExpression(after);
        return cgAfterStmt;
    }

    @Override
    public Node visit(CGIfStmt cgIfStmt) {
        CGExpr condition = cgIfStmt.getCondition();
        preconditions.add(condition);
        condition.accept(new ExprVisitorAdapter() {
            @Override
            public void visit(ConNode conNode, Void arg) {
                addNewConDeclStmt(conNode);
                super.visit(conNode, arg);
            }
        }, null);

        return super.visit(cgIfStmt);
    }


    private void addNewConDeclStmt(ConNode conNode) {
        String identifier = conNode.getIdentifier();
        if (newConIdsDeclared.contains(identifier)) {
            // skip if we have added this identifier
            return;
        }
        newConIdsDeclared.add(identifier);
        ValType valType = conNode.getValType();
        String code = CodeGenUtil.makeNewConDeclStmt(identifier, nodeToCodeGen.get(conNode), valType);
        newConDeclstmts.add(new CGGeneralStmt(code));
    }
}
