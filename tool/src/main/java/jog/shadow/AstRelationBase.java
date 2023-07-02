package jog.shadow;

import com.github.javaparser.utils.Pair;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.ConNode;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.nodetype.LeafNode;
import jog.ast.nodetype.NodeThatIsConstant;
import jog.util.Z3Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AstRelationBase {

    protected final CGExpr astX;
    protected final CGExpr astY;

    protected final List<CGExpr> preXs;
    protected final List<CGExpr> preYs;

    private final Map<CGExpr, String> astXNodeToZ3VarName;
    private final Map<CGExpr, String> astYNodeToZ3VarName;

    protected final Z3Solver z3Solver;

    // Store all equivalences to help composition
    protected final Set<Pair<CGExpr, CGExpr>> eqvs = new HashSet<>();

    protected static final Map<com.microsoft.z3.Status, Status> proofStatusToStatus = Map.of(
            com.microsoft.z3.Status.SATISFIABLE, Status.NOT_OK,
            com.microsoft.z3.Status.UNSATISFIABLE, Status.OK,
            com.microsoft.z3.Status.UNKNOWN, Status.UNKNOWN
    );

    protected static final Map<com.microsoft.z3.Status, Status> solvingStatusToStatus = Map.of(
            com.microsoft.z3.Status.SATISFIABLE, Status.OK,
            com.microsoft.z3.Status.UNSATISFIABLE, Status.NOT_OK,
            com.microsoft.z3.Status.UNKNOWN, Status.UNKNOWN
    );

    public AstRelationBase(CGExpr astX, CGExpr astY) {
        this(astX, astY, (List<CGExpr>) null, null);
    }

    public AstRelationBase(CGExpr astX, CGExpr astY, CGExpr preX, CGExpr preY) {
        this(astX, astY, List.of(preX), List.of(preY));
    }

    public AstRelationBase(CGExpr astX, CGExpr astY, List<CGExpr> preXs, List<CGExpr> preYs) {
        this.astX = astX;
        this.astY = astY;
        this.preXs = preXs;
        this.preYs = preYs;
        this.astXNodeToZ3VarName = new HashMap<>();
        this.astYNodeToZ3VarName = new HashMap<>();
        this.z3Solver = new Z3Solver();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "astX=" + astX +
                ", astY=" + astY +
                '}';
    }

    public boolean isOK() {
        return check() == Status.OK;
    }

    public boolean isNotOK() {
        return check() == Status.NOT_OK;
    }

    public abstract Status check();

    protected boolean noPreconditions() {
        return noPreconditions(preXs) && noPreconditions(preYs);
    }

    protected boolean noPreconditions(List<CGExpr> l) {
        return l == null || l.isEmpty();
    }

    protected boolean shapeMatch() {
        return shapeMatch(astX, astY);
    }

    /**
     * Returns true if the two given ASTs match shape.
     */
    protected static boolean shapeMatch(CGExpr rootX, CGExpr rootY) {
        // At least one leaf
        if (isLeaf(rootX) || isLeaf(rootY)) {
            return true;
        }

        // Both nodes are internal
        if (!rootX.getClass().equals(rootY.getClass())) {
            // return false if they do not share the same type of
            // node
            return false;
        }
        // Both nodes share the same type of node
        if (rootX instanceof BinNode) {
            BinNode rX = (BinNode) rootX;
            BinNode rY = (BinNode) rootY;
            return rX.getOperator() == rY.getOperator()
                    && shapeMatch(rX.getLeft(), rY.getLeft())
                    && shapeMatch(rX.getRight(), rY.getRight());
        }
        if (rootX instanceof UnaryNode) {
            UnaryNode rX = (UnaryNode) rootX;
            UnaryNode rY = (UnaryNode) rootY;
            return rX.getOperator() == rY.getOperator()
                    && shapeMatch(rX.getOperand(), rY.getOperand());
        }
        throw new RuntimeException("Not expected node type: " + rootX.getClass());
    }

    protected void createZ3Vars() {
        createZ3VarsForAST(astX, false);
        createZ3VarsForAST(astY, true);
    }

    /**
     * Create a variable for every node in the given AST.
     */
    protected void createZ3VarsForAST(CGExpr root, boolean XOrY) {
        createVarInZ3Solver(root, XOrY);
        if (isLeaf(root)) {
            if (root instanceof ConstantFoldingNode) {
                // Only literals can occur only in ConstantFoldingNode
                // but no elsewhere; so we want to create a new
                // z3 variable for it just in case we use the
                // literal in the preconditions.
                createZ3VarsForAST(((ConstantFoldingNode) root).getExpr(), XOrY);
            }
            return;
        }
        if (root instanceof BinNode) {
            createZ3VarsForAST(((BinNode) root).getLeft(), XOrY);
            createZ3VarsForAST(((BinNode) root).getRight(), XOrY);
            return;
        }
        if (root instanceof UnaryNode) {
            createZ3VarsForAST(((UnaryNode) root).getOperand(), XOrY);
            return;
        }
        throw new RuntimeException("Not expected node type: " + root.getClass());
    }

    protected void constrainShapeAndValue() {
        constrainShapeAndValue(astX, false);
        constrainShapeAndValue(astY, true);
    }

    /**
     * Traverse the sub-ast rooted at the given node and encode
     * parent-children relationships in z3 solver.
     * @param XOrY false: X; true: Y.
     */
    protected void constrainShapeAndValue(CGExpr root, boolean XOrY) {
        if (isLeaf(root)) {
            // Encode leaf
            constrainLeaf(root, XOrY);
            return;
        }
        if (root instanceof BinNode) {
            // Encode binary relation
            BinNode r = (BinNode) root;
            addBinRel(r, XOrY);
            constrainShapeAndValue(r.getLeft(), XOrY);
            constrainShapeAndValue(r.getRight(), XOrY);
            return;
        }
        if (root instanceof UnaryNode) {
            // Encode unary relation
            UnaryNode r = (UnaryNode) root;
            addUnaryRel(r, XOrY);
            constrainShapeAndValue(r.getOperand(), XOrY);
            return;
        }
        throw new RuntimeException("Not expected node type: " + root.getClass());
    }

    private void addBinRel(BinNode r, boolean XOrY) {
        z3Solver.addBinRel(
                getZ3Var(r, XOrY),
                getZ3Var(r.getLeft(), XOrY),
                getZ3Var(r.getRight(), XOrY),
                r.getOperator(),
                XOrY);
    }

    private void addUnaryRel(UnaryNode r, boolean XOrY) {
        z3Solver.addUnaryRel(
                getZ3Var(r, XOrY),
                getZ3Var(r.getOperand(), XOrY),
                r.getOperator(),
                XOrY);
    }

    protected void constrainEqv() {
        constrainEqv(astX, astY);
    }

    protected void constrainEqv(CGExpr rootX, CGExpr rootY) {
        addMatchingEqv(rootX, rootY);
        if (isLeaf(rootX) || isLeaf(rootY)) {
            return;
        }
        if (rootX instanceof BinNode) {
            constrainEqv(((BinNode) rootX).getLeft(), ((BinNode) rootY).getLeft());
            constrainEqv(((BinNode) rootX).getRight(), ((BinNode) rootY).getRight());
            return;
        }
        if (rootX instanceof UnaryNode) {
            constrainEqv(((UnaryNode) rootX).getOperand(), ((UnaryNode) rootY).getOperand());
            return;
        }
        throw new RuntimeException("Not expected node type: " + rootX.getClass());
    }

    private void addMatchingEqv(CGExpr nodeX, CGExpr nodeY) {
        eqvs.add(new Pair<>(nodeX, nodeY));
        z3Solver.addMatchingEqv(getZ3Var(nodeX, false), getZ3Var(nodeY, true));
    }

    protected void constrainPreds() {
        constrainPreds(preXs, false);
        constrainPreds(preYs, true);
    }

    protected void constrainPreds(List<CGExpr> pres, boolean XOrY) {
        if (pres == null || pres.isEmpty()) {
            // add true
            z3Solver.addTrueAsValueConstraint(XOrY);
            return;
        }

        Map<CGExpr, Expr> astNodeToZ3ShapeVar = new HashMap<>();
        for (Map.Entry<CGExpr, String> e : (!XOrY ? astXNodeToZ3VarName : astYNodeToZ3VarName).entrySet()) {
            CGExpr node = e.getKey();
            String name = e.getValue();
            Expr var = z3Solver.getShapeVars(XOrY).get(name);
            astNodeToZ3ShapeVar.put(node, var);
        }
        Map<CGExpr, Expr> astNodeToZ3ValueVar = getAstNodeToZ3ValueVar(XOrY);
        for (CGExpr precondition : pres) {
            PreconditionZ3Builder preZ3Builder = new PreconditionZ3Builder(z3Solver, astNodeToZ3ShapeVar, astNodeToZ3ValueVar);
            Expr z3Pre = precondition.accept(preZ3Builder);
            // z3Pre would be null because we did not encode most of
            // lib apis. see comments in {@link PreconditionZ3Builder}
            if (z3Pre != null) {
                z3Solver.addValueConstraint(XOrY, z3Pre);
            }
        }
    }

    private void constrainLeaf(CGExpr node, boolean XOrY) {
        if (!isConstant(node)) {
            return;
        }
        Expr value;
        Context ctx = z3Solver.getContext();
        int bitWidth = z3Solver.getBitWidth();
        if (node instanceof LitNode) {
            value = Z3Util.mkBV(ctx, ((LitNode) node).getValue(), bitWidth);
        } else if (node instanceof ConNode) {
            value = null;
        } else if (node instanceof ConstantFoldingNode) {
            ConstantFoldingNodeZ3Builder builder =
                    new ConstantFoldingNodeZ3Builder(z3Solver, getAstNodeToZ3ValueVar(XOrY));
            value = node.accept(builder);
            // Constrain shape inside ConstantFoldingNode
            constrainShapeAndValue(((ConstantFoldingNode) node).getExpr(), XOrY);
        } else {
            throw new RuntimeException("Unexpected constant type: " + node);
        }
        z3Solver.addLeafConstraint(getZ3Var(node, XOrY), value, XOrY);
    }

    private void createVarInZ3Solver(CGExpr node, boolean XOrY) {
        z3Solver.addVar(getZ3Var(node, XOrY), XOrY);
    }

    private Map<CGExpr, Expr> getAstNodeToZ3ValueVar(boolean XOrY) {
        Map<CGExpr, Expr> astNodeToZ3ValueVar = new HashMap<>();
        for (Map.Entry<CGExpr, String> e : (!XOrY ? astXNodeToZ3VarName : astYNodeToZ3VarName).entrySet()) {
            CGExpr node = e.getKey();
            String name = e.getValue();
            Expr var = z3Solver.getValueVars(XOrY).get(name);
            astNodeToZ3ValueVar.put(node, var);
        }
        return astNodeToZ3ValueVar;
    }

    private static boolean isInternal(CGExpr node) {
        return !isLeaf(node);
    }

    protected static boolean isLeaf(CGExpr node) {
        return node instanceof LeafNode;
    }

    private static boolean isConstant(CGExpr node) {
        return node instanceof NodeThatIsConstant;
    }

    private String getZ3Var(CGExpr node, boolean XOrY) {
        if (!XOrY) {
            if (astXNodeToZ3VarName.containsKey(node)) {
                return astXNodeToZ3VarName.get(node);
            }
            String x = "x" + astXNodeToZ3VarName.size();
            astXNodeToZ3VarName.put(node, x);
            return x;
        } else {
            if (astYNodeToZ3VarName.containsKey(node)) {
                return astYNodeToZ3VarName.get(node);
            }
            String y = "y" + astYNodeToZ3VarName.size();
            astYNodeToZ3VarName.put(node, y);
            return y;
        }
    }
}
