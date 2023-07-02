package jog.codegen;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.types.ResolvedType;

import jog.Constants;
import jog.api.Composites;
import jog.api.Shadows;
import jog.api.Lib;
import jog.api.Origin;
import jog.api.PR;
import jog.ast.expr.BinNode;
import jog.ast.expr.CallNode;
import jog.ast.expr.lib.OpNode;
import jog.ast.expr.lib.TypeNode;
import jog.ast.stmt.CGAfterStmt;
import jog.ast.expr.ConNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.LitNode;
import jog.ast.expr.UnaryNode;
import jog.ast.expr.VarNode;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.visitor.CodeGenVisitor;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGStmt;
import jog.ast.visitor.PrepareVisitor;
import jog.ast.visitor.StmtVisitorAdapter;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.stats.Stats;
import jog.util.ApiUtil;
import jog.util.JPUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PatternV extends Translatable implements Comparable<PatternV> {

    private Stats stats;

    private CGBlockStmt cgAST;

    private PrepareVisitor pv;
    private CodeGenVisitor cgv;
    private TestMethodGen tmg;
    private ValType valType;

    // Whether every original identifier is a constant or not
    private final Map<String, Boolean> isIdConstant;
    // Maps original identifier name to formalized name
    private final Map<String, String> formalizedNamesOfId;

    private final Map<String, CGExpr> exprToNode;
    private final Map<String, CGExpr> tmpVarsToNodes;

    private static final Map<BinaryExpr.Operator, BinNode.Op> BINARY_JAVA_OP_TO_OP = Map.ofEntries(
            Map.entry(BinaryExpr.Operator.MINUS, BinNode.Op.SUB),
            Map.entry(BinaryExpr.Operator.PLUS, BinNode.Op.ADD),
            Map.entry(BinaryExpr.Operator.MULTIPLY, BinNode.Op.MUL),
            Map.entry(BinaryExpr.Operator.DIVIDE, BinNode.Op.DIV),
            Map.entry(BinaryExpr.Operator.REMAINDER, BinNode.Op.MOD),
            Map.entry(BinaryExpr.Operator.LEFT_SHIFT, BinNode.Op.SHIFTL),
            Map.entry(BinaryExpr.Operator.SIGNED_RIGHT_SHIFT, BinNode.Op.SHIFTR),
            Map.entry(BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT, BinNode.Op.SHIFTUR),
            Map.entry(BinaryExpr.Operator.BINARY_AND, BinNode.Op.BIN_AND),
            Map.entry(BinaryExpr.Operator.BINARY_OR, BinNode.Op.BIN_OR),
            Map.entry(BinaryExpr.Operator.XOR, BinNode.Op.XOR),
            /*------------------------------------------------------*/
            Map.entry(BinaryExpr.Operator.LESS, BinNode.Op.LT),
            Map.entry(BinaryExpr.Operator.LESS_EQUALS, BinNode.Op.LE),
            Map.entry(BinaryExpr.Operator.GREATER, BinNode.Op.GT),
            Map.entry(BinaryExpr.Operator.GREATER_EQUALS, BinNode.Op.GE),
            Map.entry(BinaryExpr.Operator.EQUALS, BinNode.Op.EQ),
            Map.entry(BinaryExpr.Operator.NOT_EQUALS, BinNode.Op.NE),
            Map.entry(BinaryExpr.Operator.AND, BinNode.Op.LOGIC_AND),
            Map.entry(BinaryExpr.Operator.OR, BinNode.Op.LOGIC_OR)
    );

    private static final Map<UnaryExpr.Operator, UnaryNode.Op> UNARY_JP_OP_TO_OP = Map.ofEntries(
            /*------------------------------------------------------*/
            Map.entry(UnaryExpr.Operator.MINUS, UnaryNode.Op.MINUS),
            Map.entry(UnaryExpr.Operator.LOGICAL_COMPLEMENT, UnaryNode.Op.LOGIC_NOT),
            Map.entry(UnaryExpr.Operator.BITWISE_COMPLEMENT, UnaryNode.Op.BITWISE_NOT)
    );

    private static final Map<String, CallNode.Name> LIB_CALL_TO_CALL_NAME = Map.ofEntries(
            Map.entry(Constants.GET_HI, CallNode.Name.GET_HI),
            Map.entry(Constants.GET_LO, CallNode.Name.GET_LO),
            Map.entry(Constants.GET_TYPE, CallNode.Name.GET_TYPE),
            Map.entry(Constants.MATCH_RULE_SUPPORTED, CallNode.Name.MATCH_RULE_SUPPORTED),
            Map.entry(Constants.OK_TO_CONVERT_METH, CallNode.Name.OK_TO_CONVERT),
            Map.entry(Constants.OUTCNT, CallNode.Name.OUT_CNT)
    );

    private static final Map<String, BinNode.Op> SPECIAL_CONSTRUCT_TO_BIN_OP = Map.ofEntries(
            Map.entry(Constants.ROTATE_RIGHT, BinNode.Op.ROTATE_RIGHT),
            Map.entry(Constants.ROTATE_LEFT, BinNode.Op.ROTATE_LEFT),
            Map.entry(Constants.MAX, BinNode.Op.MAX),
            Map.entry(Constants.MIN, BinNode.Op.MIN)
    );

    /**
     * Instantiates this pattern from the given method declaration.
     */
    public PatternV(MethodDeclaration methodDecl) {
        super(methodDecl, Constants.PATTERN_ANNOT);
        isIdConstant = new HashMap<>();
        formalizedNamesOfId = new HashMap<>();
        exprToNode = new HashMap<>();
        tmpVarsToNodes = new HashMap<>();
        makeAST();
    }

    public ValType getValType() {
        return valType;
    }

    public String getName() {
        return methodDecl.getNameAsString();
    }

    public Stats getStats() {
        return stats;
    }

    public CGExpr getBeforeNode() {
        return pv.getBeforeNode();
    }

    public CGExpr getAfterNode() {
        return pv.getAfterNode();
    }

    public List<CGExpr> getPreconditions() {
        return pv.getPreconditions();
    }

    public TestMethodGen getTestMethodGen() {
        return tmg;
    }

    @Override
    public void translate() {
        translationBuilder = new StringBuilder();
        String codeGen = cgv.getResult();
        translationBuilder.append(codeGen).append("\n");
    }

    @Override
    public String toString() {
        return "Pattern " + getName();
    }

    @Override
    public int compareTo(PatternV o) {
        return getName().compareTo(o.getName());
    }

    /**
     * Generate Java test for this optimization. makeStats() has to be
     * run before this method can be called.
     */
    public void createTestMethodGen() {
        Map<String, Boolean> isFormalizedIdConstant = new HashMap<>();
        for (Map.Entry<String, String> e : formalizedNamesOfId.entrySet()) {
            isFormalizedIdConstant.put(e.getValue(), isIdConstant(e.getKey()));
        }
        tmg = new TestMethodGen(this, isFormalizedIdConstant);
    }

    private void makeAST() {
        if (methodDecl.getBody().isEmpty()) {
            return;
        }

        // Know which variables are constants from annotations.
        defineVariables();

        // Make initial AST
        cgAST = makeCodeGenAST();

        // Modify cgAST a bit, inserting constant declaring statements
        // and doing constant folding
        pv = new PrepareVisitor();
        cgAST = (CGBlockStmt) cgAST.accept(pv);
        pv.finish();

        // Insert declaration of variables to save values of
        // in() call chains.
        List<CGStmt> stmts = new ArrayList<>();
        for (Map.Entry<String, String> e : new TreeMap<>(pv.getVarToInCallChain()).entrySet()) {
            String v = e.getKey();
            String inCallChain = e.getValue();
            stmts.add(new CGGeneralStmt("Node* " + v + " = " + inCallChain + ";"));
        }
        stmts.addAll(cgAST.getStmts());
        cgAST.setStmts(stmts);

        // Generate C++ code for this optimization
        cgv = new CodeGenVisitor(pv.getNodeToCodeGen(), pv.getNodeToCodeGenList());
        cgAST.accept(cgv, null);
    }

    private void defineVariables() {
        for (Parameter param : methodDecl.getParameters()) {
            boolean isConstant = param.getAnnotationByName(Constants.CONSTANT_ANNOT).isPresent();
            String name = param.getNameAsString();
            isIdConstant.put(name, isConstant);

            if (valType != null) {
                continue;
            }
            // Set the value type
            ResolvedType rt = param.resolve().getType();
            if (!rt.isPrimitive()) {
                throw new IllegalArgumentException("Not primitive");
            }
            valType = ValType.of(rt.asPrimitive().describe());
        }
    }

    private CGBlockStmt makeCodeGenAST() {
        // Assume there is only one before ACTION
        CGBlockStmt cgAST = makeCGBlockStmt(methodDecl.getBody().get());

        Map<CGStmt, CGStmt> parents = new HashMap<>();
        CGBeforeStmt cgBeforeStmt = findBeforeStatementAndParents(cgAST, parents);
        // Merge all CSStmts after CGBeforeStmt into the then branch
        // of the CGBeforeStmt
        boolean seenBefore = false;
        List<CGStmt> stmtsInThenBlockOfBefore = new LinkedList<>();
        CGBlockStmt parentOfBefore = ((CGBlockStmt) parents.get(cgBeforeStmt));
        for (CGStmt s : parentOfBefore.getStmts()) {
            if (s == cgBeforeStmt) {
                seenBefore = true;
                continue;
            }
            if (seenBefore) {
                stmtsInThenBlockOfBefore.add(s);
            }
        }
        for (CGStmt s : stmtsInThenBlockOfBefore) {
            parentOfBefore.getStmts().remove(s);
        }
        cgBeforeStmt.setThenStmt(new CGBlockStmt(stmtsInThenBlockOfBefore));
        return cgAST;
    }

    private CGBeforeStmt findBeforeStatementAndParents(CGStmt stmt, Map<CGStmt, CGStmt> parents) {
        final CGBeforeStmt[] beforeStmt = new CGBeforeStmt[1];
        stmt.accept(new StmtVisitorAdapter() {
            @Override
            public void visit(CGBeforeStmt cgBeforeStmt, Void arg) {
                beforeStmt[0] = cgBeforeStmt;
            }

            @Override
            public void visit(CGBlockStmt cgBlockStmt, Void arg) {
                for (CGStmt s : cgBlockStmt.getStmts()) {
                    parents.put(s, cgBlockStmt);
                }
                super.visit(cgBlockStmt, arg);
            }

            @Override
            public void visit(CGIfStmt cgIfStmt, Void arg) {
                parents.put(cgIfStmt.getThenStmt(), cgIfStmt);
                if (cgIfStmt.hasElseBranch()) {
                    parents.put(cgIfStmt.getElseStmt(), cgIfStmt);
                }
                super.visit(cgIfStmt, arg);
            }
        }, null);
        return beforeStmt[0];
    }

    private CGStmt makeCGStmt(Statement stmt) {
        if (ApiUtil.isAction(stmt)) {
            return makeCGStmtForAction(stmt);
        } else if (JPUtil.isDeclStmt(stmt)) {
            // do not do for assignment because for now we just keep
            // assignment statement as is, and it will be processed as
            // a CGGeneralStmt.
            return makeCGStmtForDeclStmt(JPUtil.getDeclExpr(stmt));
        } else if (stmt.isIfStmt()) {
            return makeCGIfStmt(stmt.asIfStmt());
        } else if (stmt.isBlockStmt()) {
            return makeCGBlockStmt(stmt.asBlockStmt());
        }
        // keep general statement intact
        return new CGGeneralStmt(stmt.toString());
    }

    private CGTmpDeclStmt makeCGStmtForDeclStmt(VariableDeclarationExpr expr) {
        // Assume <type> <var> = <expr>
        VariableDeclarator vd = expr.getVariable(0);
        String var = vd.getNameAsString();
        tmpVarsToNodes.put(var, makeAfterOrPredNode(vd.getInitializer().get()));
        return new CGTmpDeclStmt(expr.toString());
    }

    private CGIfStmt makeCGIfStmt(IfStmt ifStmt) {
        CGExpr predNode = makeAfterOrPredNode(ifStmt.getCondition());
        CGStmt thenCGStmt = makeCGStmt(ifStmt.getThenStmt());
        CGStmt elseCGStmt = ifStmt.getElseStmt().map(this::makeCGStmt).orElse(null);
        return new CGIfStmt(predNode, thenCGStmt, elseCGStmt);
    }

    private CGBlockStmt makeCGBlockStmt(BlockStmt blockStmt) {
        List<CGStmt> cgStmts = new LinkedList<>();
        for (Statement s : blockStmt.getStatements()) {
            cgStmts.add(makeCGStmt(s));
        }
        return new CGBlockStmt(cgStmts);
    }

    private CGStmt makeCGStmtForAction(Statement stmt) {
        Expression expr = stmt.asExpressionStmt().getExpression();
        // Translate Action
        String apiName = ApiUtil.getCallName(expr);
        MethodCallExpr actionCallExpr = expr.asMethodCallExpr();
        Expression actionExpr = getArgOfActionCall(actionCallExpr);
        switch (apiName) {
        case Constants.BEFORE_METH:
            return makeCGStmtForBefore(actionExpr);
        case Constants.AFTER_METH:
            return makeCGStmtForAfter(actionExpr);
        default:
            throw new RuntimeException("Unexpected action api: " + apiName);
        }
    }

    // Has to be invoked before making any after or pred node because
    // we populate exprToNode hand nodeToCodeGen here.
    private CGStmt makeCGStmtForBefore(Expression before) {
        CGExpr beforeNode = makeBeforeNodeAndBuildExprToNode(before);
        // thenStmt will be filled later
        return new CGBeforeStmt(beforeNode, null);
    }

    private CGStmt makeCGStmtForAfter(Expression after) {
        CGExpr afterNode = makeAfterOrPredNode(after);
        return new CGAfterStmt(afterNode);
    }

    private CGExpr makeBeforeNodeAndBuildExprToNode(Expression expr) {
        return expr.accept(new GenericVisitorAdapter<CGExpr, Void>() {
            // We reuse node only for leaves.

            @Override
            public CGExpr visit(MethodCallExpr mce, Void arg) {
                if (!ApiUtil.isSpecialConstruct(mce)) {
                    return super.visit(mce, arg);
                }
                List<Expression> args = mce.asMethodCallExpr().getArguments();
                CGExpr left = args.get(0).accept(this, arg);
                CGExpr right = args.get(1).accept(this, arg);
                String callName = ApiUtil.getCallName(mce);
                BinNode.Op operator = SPECIAL_CONSTRUCT_TO_BIN_OP.get(callName);
                CGExpr node = new BinNode(left, right, operator, valType);
                return addExprToNodeMapping(mce, node);
            }

            @Override
            public CGExpr visit(BinaryExpr be, Void arg) {
                BinaryExpr.Operator operator = be.getOperator();
                if (!BINARY_JAVA_OP_TO_OP.containsKey(operator)) {
                    throw new RuntimeException("Unsupported operator: " + operator);
                }
                CGExpr left = be.getLeft().accept(this, arg);
                CGExpr right = be.getRight().accept(this, arg);
                return addExprToNodeMapping(be, new BinNode(left, right, BINARY_JAVA_OP_TO_OP.get(operator), valType));
            }

            @Override
            public CGExpr visit(UnaryExpr ue, Void arg) {
                Expression e = ue.getExpression();
                // handle negative sign for a number literal
                if (isNegNumberLit(ue)) {
                    CGExpr node = reuseNodeForExprAlreadySeen(ue);
                    if (node != null) {
                        return node;
                    }
                    return addExprToNodeMapping(ue, new LitNode(numberValue(ue), valType));
                }
                // other unary expressions.
                CGExpr operand = e.accept(this, arg);
                UnaryExpr.Operator operator = ue.getOperator();
                if (!UNARY_JP_OP_TO_OP.containsKey(operator)) {
                    throw new RuntimeException("Un-supported operator: " + operator);
                }
                return addExprToNodeMapping(ue, new UnaryNode(operand, UNARY_JP_OP_TO_OP.get(operator), valType));
            }

            @Override
            public CGExpr visit(IntegerLiteralExpr ile, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(ile);
                if (node != null) {
                    return node;
                }
                return addExprToNodeMapping(ile, new LitNode(JPUtil.intValue(ile), valType));
            }

            @Override
            public CGExpr visit(LongLiteralExpr lle, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(lle);
                if (node != null) {
                    return node;
                }
                return addExprToNodeMapping(lle, new LitNode(JPUtil.longValue(lle), valType));
            }

            @Override
            public CGExpr visit(NameExpr ne, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(ne);
                if (node != null) {
                    return node;
                }
                String id = ne.getNameAsString();
                return addExprToNodeMapping(ne, isIdConstant(id) ? new ConNode(id, valType) : new VarNode(id, valType));
            }

            private CGExpr addExprToNodeMapping(Expression expr, CGExpr node) {
                expr = JPUtil.getEnclosed(expr);
                String exprStr = expr.toString();
                exprToNode.put(exprStr, node);
                return node;
            }
        }, null);
    }

    private CGExpr makeAfterOrPredNode(Expression expr) {
        return expr.accept(new GenericVisitorAdapter<CGExpr, Void>() {
            @Override
            public CGExpr visit(FieldAccessExpr fae, Void arg) {
                if (ApiUtil.isLibTypeField(fae)) {
                    Lib.Type type = Lib.Type.valueOf(fae.getNameAsString());
                    return new TypeNode(type, valType);
                }
                if (ApiUtil.isLibOperatorField(fae)) {
                    Lib.Operator operator = Lib.Operator.valueOf(fae.getNameAsString());
                    return new OpNode(operator, valType);
                }
                return super.visit(fae, arg);
            }

            @Override
            public CGExpr visit(MethodCallExpr mce, Void arg) {
                if (!ApiUtil.isSpecialConstruct(mce)
                        && !ApiUtil.isLibMethod(mce)) {
                    return super.visit(mce, arg);
                }
                CGExpr node = reuseNodeForExprAlreadySeen(mce);
                if (node != null) {
                    return node;
                }
                String callName = ApiUtil.getCallName(mce);
                List<Expression> args = mce.asMethodCallExpr().getArguments();
                if (LIB_CALL_TO_CALL_NAME.containsKey(callName)) {
                    List<CGExpr> argExprs = args.stream()
                            .map(a -> a.accept(this, arg))
                            .collect(Collectors.toList());
                    return new CallNode(LIB_CALL_TO_CALL_NAME.get(callName), argExprs, valType);
                }
                if (SPECIAL_CONSTRUCT_TO_BIN_OP.containsKey(callName)) {
                    CGExpr left = args.get(0).accept(this, arg);
                    CGExpr right = args.get(1).accept(this, arg);
                    BinNode.Op operator = SPECIAL_CONSTRUCT_TO_BIN_OP.get(callName);
                    return new BinNode(left, right, operator, valType);
                }
                throw new RuntimeException("Unsupported Lib api: " + callName);
            }

            @Override
            public CGExpr visit(BinaryExpr be, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(be);
                if (node != null) {
                    return node;
                }
                CGExpr left = be.getLeft().accept(this, arg);
                CGExpr right = be.getRight().accept(this, arg);
                BinaryExpr.Operator operator = be.getOperator();
                if (!BINARY_JAVA_OP_TO_OP.containsKey(operator)) {
                    throw new RuntimeException("Un-supported operator: " + operator);
                }
                return new BinNode(left, right, BINARY_JAVA_OP_TO_OP.get(operator), valType);
            }

            @Override
            public CGExpr visit(NameExpr ne, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(ne);
                if (node != null) {
                    return node;
                } else {
                    // Should not be reached because we do not expect to
                    // see any new identifier in after.
                    throw new RuntimeException("An identifier not used in before appeared in after: " + ne.toString());
                }
            }

            @Override
            public CGExpr visit(UnaryExpr ue, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(ue);
                if (node != null) {
                    return node;
                }
                Expression e = ue.getExpression();
                // handle negative sign for a number literal
                if (isNegNumberLit(ue)) {
                    return new LitNode(numberValue(ue), valType);
                }
                // other unary expressions.
                CGExpr operand = e.accept(this, arg);
                UnaryExpr.Operator operator = ue.getOperator();
                if (!UNARY_JP_OP_TO_OP.containsKey(operator)) {
                    throw new RuntimeException("Un-supported operator: " + operator);
                }
                return new UnaryNode(operand, UNARY_JP_OP_TO_OP.get(operator), valType);
            }

            @Override
            public CGExpr visit(IntegerLiteralExpr ile, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(ile);
                if (node != null) {
                    return node;
                }
                // Could be reached
                return new LitNode(JPUtil.intValue(ile), valType);
            }

            @Override
            public CGExpr visit(LongLiteralExpr lle, Void arg) {
                CGExpr node = reuseNodeForExprAlreadySeen(lle);
                if (node != null) {
                    return node;
                }
                // Could be reached
                return new LitNode(JPUtil.longValue(lle), valType);
            }
        }, null);
    }

    /**
     * Reuse the node for the given expression already seen,
     * otherwise return null.
     */
    private CGExpr reuseNodeForExprAlreadySeen(Expression e) {
        e = JPUtil.getEnclosed(e);
        String exprStr = e.toString();
        return exprToNode.getOrDefault(exprStr, tmpVarsToNodes.getOrDefault(exprStr, null));
    }

    /**
     * Make stats field. Needs the entire context including other
     * patterns.
     */
    public void makeStats(Map<String, PatternV> patternsByName) {
        Optional<Expression> originValue = getSingleMemeberAnnotValue(Origin.class);
        String origin = originValue.map(o -> o.asStringLiteralExpr().asString())
                // by default assume origin is OpenJDK
                .orElse(Constants.OPENJDK);
        Optional<Expression> prValue = getSingleMemeberAnnotValue(PR.class);
        int pr = prValue.map(o -> JPUtil.intValue(o.asIntegerLiteralExpr()))
                // Set pr to be -1, which means not associated with any PR
                .orElse(-1);
        Set<PatternV> shadows = getSetOfPatternsFromAnnotation(Shadows.class, patternsByName);
        Set<PatternV> composites = getSetOfPatternsFromAnnotation(Composites.class, patternsByName);
        Expression actionBefore = getAction(Constants.BEFORE_METH);
        Expression actionAfter = getAction(Constants.AFTER_METH);
        BinNode.NodeType nodeType;
        if (actionBefore.isBinaryExpr()) {
            nodeType = BINARY_JAVA_OP_TO_OP.get(actionBefore.asBinaryExpr().getOperator()).toNodeType();
        } else if (ApiUtil.isSpecialConstruct(actionBefore)) {
            nodeType = SPECIAL_CONSTRUCT_TO_BIN_OP.get(ApiUtil.getCallName(actionBefore)).toNodeType();
        } else {
            throw new RuntimeException("Unknown before action: " + actionBefore);
        }
        String patternCode = extractPatternCode(methodDecl);
        translate();
        String generatedCode = getTranslation();
        stats = new Stats(
                methodDecl.getNameAsString(),
                nodeType,
                valType,
                formalizeExpression(actionBefore),
                formalizeExpression(getIfConditions()),
                formalizeExpression(actionAfter),
                origin,
                pr,
                shadows,
                composites,
                patternCode,
                generatedCode);
    }

    private String extractPatternCode(MethodDeclaration m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.getParameters().stream().map(Parameter::toString).collect(Collectors.joining(", ")));
        sb.append(" ");
        sb.append(m.getBody().get());
        return sb.toString();
    }

    private List<Expression> formalizeExpression(List<Expression> expressions) {
        List<Expression> es = new ArrayList<>();
        for (Expression e : expressions) {
            es.add(formalizeExpression(e.clone()));
        }
        return es;
    }

    /**
     * Make all constants in upper cases and all non-constant
     * variables in lower cases.
     */
    private Expression formalizeExpression(Expression expression) {
        return formalizeNamesOfIdentifiers(expression.clone(),
                isIdConstant.keySet().stream().filter(this::isIdConstant).collect(Collectors.toSet()));
    }

    private Expression formalizeNamesOfIdentifiers(Expression expression, Set<String> constants) {
        return (Expression) expression.accept(new ModifierVisitor<Void>() {
            @Override
            @SuppressWarnings("unchecked")
            public Visitable visit(MethodCallExpr n, Void arg) {
                // Skip method scope and name, only formalize arguments.
                NodeList<Expression> arguments = (NodeList<Expression>) n.getArguments().accept(this, arg);
                return n.setArguments(arguments);
            }

            @Override
            public Visitable visit(SimpleName n, Void arg) {
                String name = n.getIdentifier();
                String newName = constants.contains(name) ?
                        name.toUpperCase() :
                        name.toLowerCase();
                if (isIdConstant.containsKey(name)) {
                    formalizedNamesOfId.put(name, newName);
                }
                return n.setIdentifier(newName);
            }
        }, null);
    }

    private Set<PatternV> getSetOfPatternsFromAnnotation(
            Class<? extends Annotation> annotationClass, Map<String, PatternV> patternsByName) {
        Optional<Expression> names = getSingleMemeberAnnotValue(annotationClass);
        return names.map(PatternV::stringArrayAnnotationValueToSet)
                // by default an empty set
                .orElse(Set.of()).stream().map(patternsByName::get).collect(Collectors.toCollection(TreeSet::new));
    }

    private Optional<Expression> getSingleMemeberAnnotValue(Class<? extends Annotation> annotationClass) {
        return methodDecl.getAnnotationByClass(annotationClass).map(
                annot -> annot.asSingleMemberAnnotationExpr().getMemberValue());
    }

    /**
     * Returns all the conditions of if expressions in this pattern.
     */
    private List<Expression> getIfConditions() {
        return methodDecl.getBody().get().findAll(IfStmt.class).stream()
                .map(IfStmt::getCondition).collect(Collectors.toList());
    }

    /**
     * Returns the expression for the action with the given name.
     * Assume there is only one in a @Pattern method.
     */
    private Expression getAction(String callName) {
        Optional<MethodCallExpr> optional = methodDecl.getBody().get()
                .findFirst(MethodCallExpr.class, e -> ApiUtil.isAction(e, callName));
        if (optional.isEmpty()) {
            return null;
        }
        return getArgOfActionCall(optional.get());
    }

    private boolean isIdConstant(String id) {
        return isIdConstant.get(id);
    }

    /*------------------------ Utilities. --------------------------*/

    /**
     * Returns true if the given expression is a negative number
     * literal.
     */
    private static boolean isNegNumberLit(UnaryExpr ue) {
        return ue.getOperator() == UnaryExpr.Operator.MINUS
                && JPUtil.isNumberLiteral(ue.getExpression());
    }

    /**
     * Returns the value of the given negative sign expression.
     */
    private static Number numberValue(UnaryExpr ue) {
        Expression e = ue.getExpression();
        if (!isNegNumberLit(ue)) {
            throw new RuntimeException("Unexpected number literal expression: " + e.toString());
        }
        Number v;
        if (e.isIntegerLiteralExpr()) {
            IntegerLiteralExpr ile = e.asIntegerLiteralExpr();
            v = -JPUtil.intValue(ile);
        } else if (e.isLongLiteralExpr()) {
            LongLiteralExpr lle = e.asLongLiteralExpr();
            v = -JPUtil.longValue(lle);
        } else {
            throw new RuntimeException("Unexpected number literal expression: " + e.toString());
        }
        return v;
    }

    /**
     * Get the expression from an Action method call expression.
     */
    private static Expression getArgOfActionCall(MethodCallExpr actionCall) {
        return actionCall.getArguments().get(0);
    }

    private static Set<String> stringArrayAnnotationValueToSet(Expression expr) {
        if (expr.isArrayInitializerExpr()) {
            return stringArrayInitializerToSet(expr.asArrayInitializerExpr());
        } else {
            return Set.of(expr.asStringLiteralExpr().asString());
        }
    }

    private static Set<String> stringArrayInitializerToSet(ArrayInitializerExpr expr) {
        return expr.getValues().stream()
                .map(e -> e.asStringLiteralExpr().asString())
                .collect(Collectors.toUnmodifiableSet());
    }
}
