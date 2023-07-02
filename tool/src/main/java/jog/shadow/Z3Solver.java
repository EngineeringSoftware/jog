package jog.shadow;

import com.microsoft.z3.BitVecSort;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Constructor;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import jog.api.Lib;
import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.UnaryNode;
import jog.log.Log;
import jog.util.Z3Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Z3Solver {

    private Context ctx;
    private FuncDecl nil_decl, is_nil_decl, val_decl, unary_cons_decl, is_unary_decl, unary_op_decl, operand_decl, binary_cons_decl, is_binary_decl, binary_op_decl, left_decl, right_decl;
    private Map<CallNode.Name, FuncDecl> libCallDecls;
    private Sort node;
    private EnumSort binaryOpSort;
    private EnumSort unaryOpSort;
    private EnumSort libOpSort;
    private EnumSort libTypeSort;
    private BitVecSort intSort;
    private BoolSort boolSort;

    private Map<Lib.Operator, Expr> libOpToEnumExpr;
    private Map<Lib.Type, Expr> libTypeToEnumExpr;
    private Map<String, Expr> xValueVars;
    private Map<String, Expr> yValueVars;
    private Map<String, Expr> xShapeVars;
    private Map<String, Expr> yShapeVars;

    private BoolExpr shapeMatchingEqvs;
    private BoolExpr valueMatchingEqvs;
    private BoolExpr shapeConstraintsX;
    private BoolExpr shapeConstraintsY;
    private BoolExpr valueConstraintsX;
    private BoolExpr valueConstraintsY;

    private static final int BIT_WIDTH = 32;
    private static final int TIMEOUT = 2000; // milliseconds

    public Z3Solver() {
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("proof", "true");
        // cfg.put("auto-config", "false");
        ctx = new Context(cfg);
        xShapeVars = new HashMap<>();
        yShapeVars = new HashMap<>();
        xValueVars = new HashMap<>();
        yValueVars = new HashMap<>();
        init();
    }

    public Context getContext() {
        return ctx;
    }

    public FuncDecl getValDecl() {
        return val_decl;
    }

    public int getBitWidth() {
        return BIT_WIDTH;
    }

    public Map<String, Expr> getShapeVars(boolean XOrY) {
        return !XOrY ? xShapeVars : yShapeVars;
    }

    public Map<String, Expr> getValueVars(boolean XOrY) {
        return !XOrY ? xValueVars : yValueVars;
    }

    public FuncDecl getBinaryConsDecl() {
        return binary_cons_decl;
    }

    public FuncDecl getUnaryConsDecl() {
        return unary_cons_decl;
    }

    public EnumSort getLibOpSort() {
        return libOpSort;
    }

    public Map<Lib.Operator, Expr> getLibOpToEnumExpr() {
        return libOpToEnumExpr;
    }

    public Map<Lib.Type, Expr> getLibTypeToEnumExpr() {
        return libTypeToEnumExpr;
    }

    public Map<CallNode.Name, FuncDecl> getLibCallDecls() {
        return libCallDecls;
    }

    protected void resetConstraints() {
        shapeMatchingEqvs = null;
        valueMatchingEqvs = null;
        shapeConstraintsX = null;
        shapeConstraintsY = null;
        valueConstraintsX = null;
        valueConstraintsY = null;
    }

    private void init() {
        createSorts();

        Constructor nil_con = ctx.mkConstructor("nil", "is_nil",
                new String[]{"val"}, new Sort[]{intSort}, new int[]{0});
        Constructor unary_con = ctx.mkConstructor("unary", "is_unary",
                new String[]{"op", "operand"}, new Sort[]{unaryOpSort, null}, new int[]{0, 0});
        Constructor binary_con = ctx.mkConstructor("binary", "is_binary",
                new String[]{"op", "left", "right"}, new Sort[]{binaryOpSort, null, null}, new int[]{0, 0, 0});
        Constructor<Z3Solver>[] constructors = new Constructor[]{nil_con, unary_con, binary_con};

        node = ctx.mkDatatypeSort("node", constructors);

        nil_decl = nil_con.ConstructorDecl();
        is_nil_decl = nil_con.getTesterDecl();
        val_decl = nil_con.getAccessorDecls()[0];

        unary_cons_decl = unary_con.ConstructorDecl();
        is_unary_decl = unary_con.getTesterDecl();
        unary_op_decl = unary_con.getAccessorDecls()[0];
        operand_decl = unary_con.getAccessorDecls()[1];

        binary_cons_decl = binary_con.ConstructorDecl();
        is_binary_decl = binary_con.getTesterDecl();
        binary_op_decl = binary_con.getAccessorDecls()[0];
        left_decl = binary_con.getAccessorDecls()[1];
        right_decl = binary_con.getAccessorDecls()[2];

        libCallDecls = new HashMap<>();
        libCallDecls.put(CallNode.Name.OK_TO_CONVERT, ctx.mkFuncDecl("OK_TO_CONVERT", new Sort[]{node, node}, boolSort));
        libCallDecls.put(CallNode.Name.OUT_CNT, ctx.mkFuncDecl("OUT_CNT", node, intSort));
        libCallDecls.put(CallNode.Name.MATCH_RULE_SUPPORTED, ctx.mkFuncDecl("MATCH_RULE_SUPPORTED", libOpSort, boolSort));
        libCallDecls.put(CallNode.Name.GET_TYPE, ctx.mkFuncDecl("GET_TYPE", node, libTypeSort));
    }

    private void createSorts() {
        intSort = ctx.mkBitVecSort(BIT_WIDTH);
        boolSort = ctx.getBoolSort();

        BinNode.Op[] astBinOps = BinNode.Op.values();
        List<String> binOps = new ArrayList<>();
        for (BinNode.Op op : astBinOps) {
            binOps.add(op.name());
        }
        binaryOpSort = ctx.mkEnumSort("BinaryOperator", binOps.toArray(new String[0]));

        UnaryNode.Op[] astUnaryOps = UnaryNode.Op.values();
        List<String> unaryOps = new ArrayList<>();
        for (UnaryNode.Op op : astUnaryOps) {
            unaryOps.add(op.name());
        }
        unaryOpSort = ctx.mkEnumSort("UnaryOperator", unaryOps.toArray(new String[0]));

        libOpSort = ctx.mkEnumSort("LibOperator", Arrays.stream(Lib.Operator.values()).map(Lib.Operator::name).toArray(String[]::new));
        libOpToEnumExpr = new HashMap<>();
        for (Lib.Operator op : Lib.Operator.values()) {
            int i = op.ordinal();
            libOpToEnumExpr.put(op, libOpSort.getConst(i));
        }

        libTypeSort = ctx.mkEnumSort("LibType", Arrays.stream(Lib.Type.values()).map(Lib.Type::name).toArray(String[]::new));
        libTypeToEnumExpr = new HashMap<>();
        for (Lib.Type op : Lib.Type.values()) {
            int i = op.ordinal();
            libTypeToEnumExpr.put(op, libTypeSort.getConst(i));
        }
    }

    /**
     * Add a new variable to the solver.
     * @param name variable name.
     * @param XOrY the X or Y tree the variable belongs to.
     */
    public void addVar(String name, boolean XOrY) {
        addVar(name, XOrY, false);
        addVar(name, XOrY, true);
    }

    public void addVar(String name, boolean XOrY, boolean shapeOrValue) {
        Map<String, Expr> xVars = !shapeOrValue ? xShapeVars : xValueVars;
        Map<String, Expr> yVars = !shapeOrValue ? yShapeVars : yValueVars;
        Map<String, Expr> vars = !XOrY ? xVars : yVars;
        if (vars.containsKey(name)) {
            return;
        }
        Expr v = ctx.mkConst(name + (!shapeOrValue ? "_s" : "_v"), (!shapeOrValue ? node : intSort));
        vars.put(name, v);
    }

    /**
     * Add constraints for leaf.
     * @param name variable name.
     * @param value the value of the variable if the variable is an
     *              int literal; the argument is ignored if it is
     *              null.
     * @param XOrY the X or Y tree the variable belongs to.
     */
    public void addLeafConstraint(String name, Expr value, boolean XOrY) {
        // Shape
        Map<String, Expr> vars = !XOrY ? xShapeVars : yShapeVars;
        if (!vars.containsKey(name)) {
            throw new RuntimeException(name + " does not exist in shape variables!");
        }
        Expr v = vars.get(name);
        // if the variable represents a constant in the pattern
        addShapeConstraint(XOrY, ctx.mkApp(is_nil_decl, v));
        // if the variable is further an int literal
        if (value != null) {
            addShapeConstraint(XOrY, ctx.mkEq(ctx.mkApp(val_decl, v), value));
        }

        // Value
        vars = !XOrY ? xValueVars : yValueVars;
        if (!vars.containsKey(name)) {
            throw new RuntimeException(name + " does not exist in value variables!");
        }
        v = vars.get(name);
        if (value != null) {
            // if the variable is further an int literal
            addValueConstraint(XOrY, ctx.mkEq(v, value));
        }
    }

    /**
     * Add clause x = y.
     */
    public void addMatchingEqv(String xName, String yName) {
        // shape matching
        if (!xShapeVars.containsKey(xName) || !yShapeVars.containsKey(yName)) {
            throw new RuntimeException(xName + " or " + yName + " does not exist in shape variables!");
        }
        addMatchingEqvs(false, ctx.mkEq(xShapeVars.get(xName), yShapeVars.get(yName)));
        // value matching
        if (!xValueVars.containsKey(xName) || !yValueVars.containsKey(yName)) {
            throw new RuntimeException(xName + " or " + yName + " does not exist in value variables!");
        }
        addMatchingEqvs(true, ctx.mkEq(xValueVars.get(xName), yValueVars.get(yName)));
    }

    /**
     * Add parent = binary binOp left right
     */
    public void addBinRel(String parent, String left, String right, BinNode.Op binOp, boolean XOrY) {
        addBinRel(parent, left, right, binOp, XOrY, false);
        addBinRel(parent, left, right, binOp, XOrY, true);
    }

    private void addBinRel(String parent, String left, String right, BinNode.Op binOp, boolean XOrY, boolean shapeOrValue) {
        Map<String, Expr> xVars = !shapeOrValue ? xShapeVars : xValueVars;
        Map<String, Expr> yVars = !shapeOrValue ? yShapeVars : yValueVars;
        Map<String, Expr> vars = !XOrY ? xVars : yVars;
        if (!vars.containsKey(parent) || !vars.containsKey(left) || !vars.containsKey(right)) {
            throw new RuntimeException(parent + " or " + left + " or " + right + " does not exist in " + (!shapeOrValue ? "shape variables" : "value variables"));
        }
        Expr p = vars.get(parent);
        Expr l = vars.get(left);
        Expr r = vars.get(right);

        if (!shapeOrValue) {
            Expr f = ctx.mkApp(binary_cons_decl, binaryOpSort.getConst(binOp.ordinal()), l, r);
            addShapeConstraint(XOrY, ctx.mkEq(p, f));
        } else {
            Expr f = Z3Util.mkBVBinaryExpr(ctx, l, r, binOp);
            addValueConstraint(XOrY, ctx.mkEq(p, f));
        }
    }

    /**
     * Add parent = unary binOp operand
     */
    public void addUnaryRel(String parent, String operand, UnaryNode.Op unaryOp, boolean XOrY) {
        addUnaryRel(parent, operand, unaryOp, XOrY, false);
        addUnaryRel(parent, operand, unaryOp, XOrY, true);
    }

    public void addUnaryRel(String parent, String operand, UnaryNode.Op unaryOp, boolean XOrY, boolean shapeOrValue) {
        Map<String, Expr> xVars = !shapeOrValue ? xShapeVars : xValueVars;
        Map<String, Expr> yVars = !shapeOrValue ? yShapeVars : yValueVars;
        Map<String, Expr> vars = !XOrY ? xVars : yVars;
        if (!vars.containsKey(parent) || !vars.containsKey(operand)) {
            throw new RuntimeException(parent + " or " + operand + " does not exist!");
        }
        Expr p = vars.get(parent);
        Expr o = vars.get(operand);

        if (!shapeOrValue) {
            Expr f = ctx.mkApp(unary_cons_decl, unaryOpSort.getConst(unaryOp.ordinal()), o);
            addShapeConstraint(XOrY, ctx.mkEq(p, f));
        } else {
            Expr f = Z3Util.mkBVUnaryExpr(ctx, o, unaryOp);
            addValueConstraint(XOrY, ctx.mkEq(p, f));
        }
    }

    public void addShapeConstraint(boolean XOrY, Expr... exprs) {
        if (!XOrY) {
            shapeConstraintsX = andFormulas(shapeConstraintsX, exprs);
        } else {
            shapeConstraintsY = andFormulas(shapeConstraintsY, exprs);
        }
    }

    public void addTrueAsValueConstraint(boolean XOrY) {
        addValueConstraint(XOrY, ctx.mkTrue());
    }

    public void addValueConstraint(boolean XOrY, Expr... exprs) {
        if (!XOrY) {
            valueConstraintsX = andFormulas(valueConstraintsX, exprs);
        } else {
            valueConstraintsY = andFormulas(valueConstraintsY, exprs);
        }
    }

    private void addMatchingEqvs(boolean shapeOrValue, Expr... exprs) {
        if (!shapeOrValue) {
            shapeMatchingEqvs = andFormulas(shapeMatchingEqvs, exprs);
        } else {
            valueMatchingEqvs = andFormulas(valueMatchingEqvs, exprs);
        }
    }

    public Status proveSubsume(boolean shapeOrValue) {
        return prove(makeSubsumeFormula(shapeOrValue));
    }

    public Status checkComposite(boolean shapeOrValue) {
        Expr f = makeCompositeFormula(shapeOrValue);
        // Log.info(f);
        return solve(f);
    }

    private Status prove(Expr fml, BoolExpr... assumptions) {
        return solve(ctx.mkNot(fml), assumptions);
    }

    private Status solve(Expr fml, BoolExpr... assumptions) {
        Solver s = ctx.mkSolver();
        Params p = ctx.mkParams();
        p.add("mbqi", false);
        p.add("timeout", TIMEOUT);
        s.setParameters(p);
        for (BoolExpr a : assumptions) {
            s.add(a);
        }
        s.add(fml);
        Status status = s.check();

        switch (status) {
        case SATISFIABLE:
            // Log.info(s.getModel());
            break;
        case UNSATISFIABLE:
            // Log.info("OK, proof: " + s.getProof());
            break;
        case UNKNOWN:
            Log.info("Unknown because: " + s.getReasonUnknown());
            break;
        default:
            throw new RuntimeException("Unexpected status: " + status);
        }
        return status;
    }

    /**
     * Make final formula that will be solved for composition.
     * @param shapeOrValue false: shape check; true: value check
     */
    private Expr makeCompositeFormula(boolean shapeOrValue) {
        if (!shapeOrValue) {
            // \exists x0.x1.x2.y0.y1.y2. <?>ConstraintsX && <?>ConstraintsY && <?>MatchingEqvs
            return andFormulas(null,
                    shapeConstraintsX, shapeConstraintsY, shapeMatchingEqvs);
        } else {
            return andFormulas(null,
                    shapeConstraintsX, valueConstraintsX,
                    shapeConstraintsY, valueConstraintsY,
                    shapeMatchingEqvs, valueMatchingEqvs);
        }
    }

    /**
     * Make final formula that will be solved for subsumption.
     * @param shapeOrValue false: shape check; true: value check
     */
    private Expr makeSubsumeFormula(boolean shapeOrValue) {
        if (!shapeOrValue) {
            // \forall y0.y1.y2. <?>ConstraintsY => \exists x0.x1.x2. <?>ConstraintsX && <?>MatchingEqvs
            Expr[] xVarsArr = xShapeVars.values().toArray(new Expr[0]);
            Expr[] yVarsArr = yShapeVars.values().toArray(new Expr[0]);
            Expr eFml = ctx.mkExists(xVarsArr, ctx.mkAnd(shapeConstraintsX, shapeMatchingEqvs),
                    1, null, null,
                    ctx.mkSymbol("Q1"), ctx.mkSymbol("skidQ1"));
            Expr uFml = ctx.mkImplies(shapeConstraintsY, eFml);
            return ctx.mkForall(yVarsArr, uFml,
                    1, null, null,
                    ctx.mkSymbol("Q2"), ctx.mkSymbol("skidQ2"));
        } else {
            // use both shape and value variables and all shape and
            // value constraints because Lib apis that uses shape
            // variables may appear in preconditions
            Expr[] xAllVars = new Expr[xShapeVars.size() + xValueVars.size()];
            System.arraycopy(xShapeVars.values().toArray(), 0, xAllVars, 0, xShapeVars.size());
            System.arraycopy(xValueVars.values().toArray(), 0, xAllVars, xShapeVars.size(), xValueVars.size());
            Expr[] yAllVars = new Expr[yShapeVars.size() + yValueVars.size()];
            System.arraycopy(yShapeVars.values().toArray(), 0, yAllVars, 0, yShapeVars.size());
            System.arraycopy(yValueVars.values().toArray(), 0, yAllVars, yShapeVars.size(), yValueVars.size());
            Expr eFml = ctx.mkExists(xAllVars, ctx.mkAnd(shapeConstraintsX, valueConstraintsX, shapeMatchingEqvs, valueMatchingEqvs),
                    1, null, null,
                    ctx.mkSymbol("Q3"), ctx.mkSymbol("skidQ3"));
            Expr uFml = ctx.mkImplies(ctx.mkAnd(shapeConstraintsY, valueConstraintsY), eFml);
            return ctx.mkForall(yAllVars, uFml,
                    1, null, null,
                    ctx.mkSymbol("Q4"), ctx.mkSymbol("skidQ4"));
        }
    }

    private BoolExpr andFormulas(BoolExpr target, Expr... exprs) {
        for (Expr e : exprs) {
            target = target == null ? (BoolExpr) e : ctx.mkAnd(target, e);
        }
        return target;
    }
}
