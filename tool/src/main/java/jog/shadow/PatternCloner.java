package jog.shadow;

import jog.ast.Node;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.CallNode;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.visitor.CloneVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatternCloner {

    private final PatternS pattern;
    private final PatternS patternClone;
    private final Map<Node, Node> clonesOfNode;

    public PatternCloner(PatternS pattern) {
        this.pattern = pattern;
        this.clonesOfNode = new HashMap<>();
        this.patternClone = cloneASTs(pattern.before, pattern.after, pattern.preconditions);
    }

    public PatternS getPattern() {
        return pattern;
    }

    public PatternS getClone() {
        return patternClone;
    }

    public Map<Node, Node> getCloneMappings() {
        return clonesOfNode;
    }

    private PatternS cloneASTs(CGExpr beforeNode, CGExpr afterNode, List<CGExpr> preNodes) {
        Map<CGExpr, CGExpr> usedNodesInBefore = new HashMap<>();
        CGExpr beforeClone = cloneBeforeAST(beforeNode, usedNodesInBefore);
        CGExpr afterClone = cloneAfterOrPredAST(afterNode, usedNodesInBefore);
        List<CGExpr> preClones = preNodes.stream()
                .map(e -> cloneAfterOrPredAST(e, usedNodesInBefore))
                .collect(Collectors.toList());
        return new PatternS(beforeClone, afterClone, preClones);
    }

    /* Side effects: populate usedNodesInBefore and add mappings to
       clonesOfNode. */
    private CGExpr cloneBeforeAST(CGExpr root, Map<CGExpr, CGExpr> usedNodesInBefore) {
        CloneVisitor beforeCloneVisitor = new CloneVisitor() {
            @Override
            public Node visit(BinNode binNode) {
                if (usedNodesInBefore.containsKey(binNode)) {
                    return usedNodesInBefore.get(binNode);
                }
                BinNode n = (BinNode) super.visit(binNode);
                usedNodesInBefore.put(binNode, n);
                return n;
            }

            @Override
            public Node visit(ConNode conNode) {
                if (usedNodesInBefore.containsKey(conNode)) {
                    return usedNodesInBefore.get(conNode);
                }
                ConNode n = (ConNode) super.visit(conNode);
                usedNodesInBefore.put(conNode, n);
                return n;
            }

            @Override
            public Node visit(VarNode varNode) {
                if (usedNodesInBefore.containsKey(varNode)) {
                    return usedNodesInBefore.get(varNode);
                }
                VarNode n = (VarNode) super.visit(varNode);
                usedNodesInBefore.put(varNode, n);
                return n;
            }

            @Override
            public Node visit(LitNode litNode) {
                if (usedNodesInBefore.containsKey(litNode)) {
                    return usedNodesInBefore.get(litNode);
                }
                LitNode n = (LitNode) super.visit(litNode);
                usedNodesInBefore.put(litNode, n);
                return n;
            }

            @Override
            public Node visit(UnaryNode unaryNode) {
                if (usedNodesInBefore.containsKey(unaryNode)) {
                    return usedNodesInBefore.get(unaryNode);
                }
                UnaryNode n = (UnaryNode) super.visit(unaryNode);
                usedNodesInBefore.put(unaryNode, n);
                return n;
            }

            // There should be no other node in before node.
        };
        CGExpr clone = (CGExpr) root.accept(beforeCloneVisitor);
        clonesOfNode.putAll(beforeCloneVisitor.getClonesOfNode());
        return clone;
    }

    /* Side effects: add mappings to clonesOfNode. */
    private CGExpr cloneAfterOrPredAST(CGExpr root, Map<CGExpr, CGExpr> usedNodesInBefore) {
        CloneVisitor afterOrPredCloneVisitor = new CloneVisitor(){
            @Override
            public Node visit(BinNode binNode) {
                if (usedNodesInBefore.containsKey(binNode)) {
                    return usedNodesInBefore.get(binNode);
                }
                return super.visit(binNode);
            }

            @Override
            public Node visit(ConNode conNode) {
                if (usedNodesInBefore.containsKey(conNode)) {
                    return usedNodesInBefore.get(conNode);
                }
                return super.visit(conNode);
            }

            @Override
            public Node visit(VarNode varNode) {
                if (usedNodesInBefore.containsKey(varNode)) {
                    return usedNodesInBefore.get(varNode);
                }
                return super.visit(varNode);
            }

            @Override
            public Node visit(LitNode litNode) {
                if (usedNodesInBefore.containsKey(litNode)) {
                    return usedNodesInBefore.get(litNode);
                }
                return super.visit(litNode);
            }

            @Override
            public Node visit(UnaryNode unaryNode) {
                if (usedNodesInBefore.containsKey(unaryNode)) {
                    return usedNodesInBefore.get(unaryNode);
                }
                return super.visit(unaryNode);
            }

            @Override
            public Node visit(CallNode callNode) {
                if (usedNodesInBefore.containsKey(callNode)) {
                    return usedNodesInBefore.get(callNode);
                }
                return super.visit(callNode);
            }

            @Override
            public Node visit(TypeNode typeNode) {
                if (usedNodesInBefore.containsKey(typeNode)) {
                    return usedNodesInBefore.get(typeNode);
                }
                return super.visit(typeNode);
            }

            @Override
            public Node visit(OpNode opNode) {
                if (usedNodesInBefore.containsKey(opNode)) {
                    return usedNodesInBefore.get(opNode);
                }
                return super.visit(opNode);
            }

            @Override
            public Node visit(ConstantFoldingNode constantFoldingNode) {
                if (usedNodesInBefore.containsKey(constantFoldingNode)) {
                    return usedNodesInBefore.get(constantFoldingNode);
                }
                return super.visit(constantFoldingNode);
            }
        };
        CGExpr clone = (CGExpr) root.accept(afterOrPredCloneVisitor);
        clonesOfNode.putAll(afterOrPredCloneVisitor.getClonesOfNode());
        return clone;
    }
}
