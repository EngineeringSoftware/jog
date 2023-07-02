package jog.shadow;

import com.github.javaparser.utils.Pair;
import jog.ast.Node;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.visitor.ModifierVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Check if astX and astY can be composited in the order astX astY.
 */
public class AstComposite extends AstRelationBase {

    /* We need before of x because precondition of x could use nodes
     * from before which do not exist in x.
     */
    private final CGExpr astXBefore;

    private final CGExpr astYAfter;

    // representative of every equivalent closure.
    private Map<CGExpr, CGExpr> repGraph;

    private CGExpr nodeInAstXMatchedByAstY;

    public static AstComposite makeAstComposite(PatternS x, PatternS y) {
        if (x == y) {
            y = new PatternCloner(x).getClone();
        }
        return new AstComposite(x, y);
    }

    private AstComposite(PatternS x, PatternS y) {
        this(x.after, y.before, x.preconditions, y.preconditions, x.before, y.after);
    }

    public AstComposite(CGExpr astX, CGExpr astY) {
        this(astX, astY, null, null, null, null);
    }

    public AstComposite(CGExpr astX, CGExpr astY, List<CGExpr> preXs, List<CGExpr> preYs, CGExpr astXBefore, CGExpr astYAfter) {
        super(astX, astY, preXs, preYs);
        this.astXBefore = astXBefore;
        this.astYAfter = astYAfter;
    }

    /**
     * Returns the composited pattern if the two given asts can be
     * composited.
     * <p>
     * This method can be invoked only when check() returns true.
     */
    public PatternS composite() {
        // TODO: rename for duplicated variable.
        // 1. Get clone of full pattern x and y, including before,
        //       after and preconditions.
        PatternS x = new PatternS(astXBefore, astX, preXs);
        PatternS y = new PatternS(astY, astYAfter, preYs);
        PatternCloner xCloner = new PatternCloner(x);
        PatternCloner yCloner = new PatternCloner(y);
        PatternS xClone = xCloner.getClone();
        PatternS yClone = yCloner.getClone();
        // Rename variables for pattern to make sure there is no
        // duplicate.
        PatternRenamer.rename(xClone, "x");
        PatternRenamer.rename(yClone, "y");
        CGExpr compositedAstXBefore = xClone.before;
        CGExpr compositedAstXAfter = xClone.after;
        CGExpr compositedAstYAfter = yClone.after;
        List<CGExpr> compositedPres = new LinkedList<>();
        compositedPres.addAll(xClone.preconditions);
        compositedPres.addAll(yClone.preconditions);
        // 2. Get eqvs accordingly in the clones
        Set<Pair<CGExpr, CGExpr>> compositedEqvs = eqvs.stream().map(e -> {
            CGExpr nx = e.a;
            CGExpr ny = e.b;
            CGExpr nxClone = (CGExpr) xCloner.getCloneMappings().get(nx);
            CGExpr nyClone = (CGExpr) yCloner.getCloneMappings().get(ny);
            return new Pair<>(nxClone, nyClone);
        }).collect(Collectors.toSet());
        // 3. populateRepGraph
        populateRepGraph(compositedEqvs);
        // Log.info(repGraph);
        // 4. replace in astXBefore, astYAfter
        compositedAstXBefore = replaceNodesInAST(compositedAstXBefore, repGraph);
        compositedAstYAfter = replaceNodesInAST(compositedAstYAfter, repGraph);
        // 5. replace the target node in astXAfter with astYAfter
        compositedAstXAfter = findAndReplaceSingleNodeInAST(
                compositedAstXAfter,
                (CGExpr) xCloner.getCloneMappings().get(nodeInAstXMatchedByAstY),
                compositedAstYAfter);
        // 6. replace in the new astXAfter
        compositedAstXAfter = replaceNodesInAST(compositedAstXAfter, repGraph);
        // 7. replace in preXs preYs
        compositedPres = compositedPres.stream()
                .map(e -> replaceNodesInAST(e, repGraph))
                .collect(Collectors.toList());
        // 8. handle constant folding nodes
        compositedAstXBefore = mergeConstantFoldingNode(compositedAstXBefore);
        compositedAstXAfter = mergeConstantFoldingNode(compositedAstXAfter);
        compositedPres = compositedPres.stream()
                .map(AstComposite::removeConstantFoldingNode)
                .collect(Collectors.toList());
        // 9. make a new Pattern object
        return new PatternS(compositedAstXBefore, compositedAstXAfter, compositedPres);
    }

    // Keep only one constant folding node, removing others.
    private static CGExpr mergeConstantFoldingNode(CGExpr root) {
        return (CGExpr) root.accept(new ModifierVisitor() {
            private boolean seenConstantFoldingNode = false;

            @Override
            public Node visit(ConstantFoldingNode constantFoldingNode) {
                if (!seenConstantFoldingNode) {
                    seenConstantFoldingNode = true;
                    return super.visit(constantFoldingNode);
                } else {
                    return constantFoldingNode.getExpr().accept(this);
                }
            }
        });
    }

    private static CGExpr removeConstantFoldingNode(CGExpr root) {
        return (CGExpr) root.accept(new ModifierVisitor() {
            @Override
            public Node visit(ConstantFoldingNode constantFoldingNode) {
                return constantFoldingNode.getExpr().accept(this);
            }
        });
    }

    private CGExpr findAndReplaceSingleNodeInAST(CGExpr root, CGExpr target, CGExpr substitute) {
        return (CGExpr) root.accept(new ModifierVisitor() {
            @Override
            public Node visit(BinNode binNode) {
                if (binNode == target) {
                    return substitute;
                } else {
                    return super.visit(binNode);
                }
            }

            @Override
            public Node visit(ConNode conNode) {
                if (conNode == target) {
                    return substitute;
                } else {
                    return super.visit(conNode);
                }
            }

            @Override
            public Node visit(VarNode varNode) {
                if (varNode == target) {
                    return substitute;
                } else {
                    return super.visit(varNode);
                }
            }

            @Override
            public Node visit(LitNode litNode) {
                if (litNode == target) {
                    return substitute;
                } else {
                    return super.visit(litNode);
                }
            }

            @Override
            public Node visit(UnaryNode unaryNode) {
                if (unaryNode == target) {
                    return substitute;
                } else {
                    return super.visit(unaryNode);
                }
            }
        });
    }

    private void populateRepGraph(Set<Pair<CGExpr, CGExpr>> eqvs) {
        repGraph = new HashMap<>();
        for (Pair<CGExpr, CGExpr> eqv : eqvs) {
            CGExpr nodeX = eqv.a;
            CGExpr nodeY = eqv.b;
            // Skip eqv between two internal nodes.
            if (isLeaf(nodeX) || isLeaf(nodeY)) {
                updateReps(nodeX, nodeY);
            }
        }
    }

    private void updateReps(CGExpr nodeX, CGExpr nodeY) {
        CGExpr rep = compare(nodeX, nodeY) < 0 ? nodeX : nodeY;
        updateRepForNode(nodeX, rep);
        updateRepForNode(nodeY, rep);
    }

    /* Less means more specific. */
    private static int compare(CGExpr nodeX, CGExpr nodeY) {
        if (nodeX instanceof LitNode) {
            if (nodeY instanceof LitNode) {
                return 0;
            } else if (nodeY instanceof ConstantFoldingNode
                    || nodeY instanceof ConNode
                    || nodeY instanceof VarNode
                    ) {
                return -1;
            }
        }
        if (nodeX instanceof ConstantFoldingNode) {
            if (nodeY instanceof LitNode) {
                return 1;
            } else if (nodeY instanceof ConstantFoldingNode) {
                return 0;
            } else if (nodeY instanceof ConNode
                    || nodeY instanceof VarNode) {
                return -1;
            }
        }
        if (nodeX instanceof ConNode) {
            if (nodeY instanceof LitNode
                    || nodeY instanceof ConstantFoldingNode) {
                return 1;
            } else if (nodeY instanceof ConNode) {
                return 0;
            } else if (nodeY instanceof VarNode) {
                return -1;
            }
        }
        if (nodeX instanceof VarNode) {
            if (nodeY instanceof LitNode
                    || nodeY instanceof ConstantFoldingNode
                    || nodeY instanceof ConNode) {
                return 1;
            } else if (nodeY instanceof VarNode) {
                return 0;
            } else if (nodeY instanceof BinNode
                    || nodeY instanceof UnaryNode) {
                // var node is the most general node
                return 1;
            }
        }
        if (nodeX instanceof BinNode
                || nodeX instanceof UnaryNode) {
            if (nodeY instanceof VarNode) {
                return -1;
            }
        }
        throw new RuntimeException("Unexpected comparison between" +
                " nodeX: " + nodeX + " and nodeY: " + nodeY);
    }

    private void updateRepForNode(CGExpr node, CGExpr rep) {
        repGraph.put(getRepOfNode(node), rep);
    }

    private CGExpr getRepOfNode(CGExpr node) {
        if (!repGraph.containsKey(node)) {
            repGraph.put(node, node);
        }
        while (repGraph.get(node) != node) {
            node = repGraph.get(node);
        }
        return node;
    }

    private CGExpr replaceNodesInAST(CGExpr root, Map<CGExpr, CGExpr> replacements) {
        return (CGExpr) root.accept(new ModifierVisitor() {
            @Override
            public Node visit(BinNode binNode) {
                return Objects.requireNonNullElse(
                        getReplacement(binNode),
                        super.visit(binNode));
            }

            @Override
            public Node visit(ConNode conNode) {
                return Objects.requireNonNullElse(
                        getReplacement(conNode),
                        super.visit(conNode));
            }

            @Override
            public Node visit(VarNode varNode) {
                return Objects.requireNonNullElse(
                        getReplacement(varNode),
                        super.visit(varNode));
            }

            @Override
            public Node visit(LitNode litNode) {
                return Objects.requireNonNullElse(
                        getReplacement(litNode),
                        super.visit(litNode));
            }

            @Override
            public Node visit(UnaryNode unaryNode) {
                return Objects.requireNonNullElse(
                        getReplacement(unaryNode),
                        super.visit(unaryNode));
            }

            @Override
            public Node visit(ConstantFoldingNode constantFoldingNode) {
                return Objects.requireNonNullElse(
                        getReplacement(constantFoldingNode),
                        super.visit(constantFoldingNode));
            }

            private Node getReplacement(CGExpr n) {
                return replacements.get(n);
            }
        });
    }

    @Override
    public Status check() {
        return check(astX);
    }

    /**
     * Match every subtree of astX with astY and solve
     * shape and value formulas. If ok we are done; otherwise
     * we move on to the next subtree and check.
     */
    private Status check(CGExpr rootX) {
        nodeInAstXMatchedByAstY = rootX;
        if (isLeaf(rootX)) {
            // TODO: when rootX is a single node, theoretically it can
            //  composite anything but we do not want it to.
            return Status.NOT_OK;
        }
        Status res = checkEach(rootX);
        if (res == Status.OK) {
            return Status.OK;
        }
        if (rootX instanceof BinNode) {
            Status leftRes = check(((BinNode) rootX).getLeft());
            if (leftRes == Status.OK) {
                return Status.OK;
            } else {
                return check(((BinNode) rootX).getRight());
            }
        }
        if (rootX instanceof UnaryNode) {
            return check(((UnaryNode) rootX).getOperand());
        }
        throw new RuntimeException("Not expected node type: " + rootX.getClass());
    }

    private Status checkEach(CGExpr node) {
        z3Solver.resetConstraints();
        eqvs.clear();
        if (!shapeMatch(node, astY)) {
            return Status.NOT_OK;
        }
        createZ3Vars();
        constrainShapeAndValue();
        constrainEqv(node, astY);
        Status shapeCheckRes = checkShapes();
        if (shapeCheckRes != Status.OK) {
            return shapeCheckRes;
        }
        if (noPreconditions()) {
            return Status.OK;
        }
        // Do value check only if shape check passes and there are
        // preconditions to check
        constrainPreds();
        return checkValues();
    }

    // We need to do more here than in AstSubsume because precondition
    // of X could use node that comes from before node and thus might
    // not be in astX which is usually after node in AstComposite.
    // Similarly, precondition of Y could use nodes that occur only in
    // after node, where the nodes can only be literals, actually.
    @Override
    protected void createZ3Vars() {
        super.createZ3Vars();
        if (astXBefore != null) { // astXBefore exists
            createZ3VarsForAST(astXBefore, false);
        }
        if (astYAfter != null) {
            createZ3VarsForAST(astYAfter, true);
        }
    }

    @Override
    protected void constrainPreds() {
        super.constrainPreds();
        if (astXBefore != null) { // astXBefore exists
            constrainShapeAndValue(astXBefore, false);
        }
        if (astYAfter != null) {
            constrainShapeAndValue(astYAfter, true);
        }
    }

    private Status checkShapes() {
        return solvingStatusToStatus.get(z3Solver.checkComposite(false));
    }

    private Status checkValues() {
        return solvingStatusToStatus.get(z3Solver.checkComposite(true));
    }
}
