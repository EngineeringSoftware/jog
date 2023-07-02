package jog.codegen;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import jog.Constants;
import jog.ast.expr.BinNode;
import jog.ast.expr.CGExpr;
import jog.ast.expr.ConstantFoldingNode;
import jog.ast.expr.UnaryNode;
import jog.ast.visitor.ExprVisitorAdapter;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.stats.Stats;
import jog.util.JPUtil;
import jog.util.MiscUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TestMethodGen {

    private final PatternV pattern;
    private final Stats stats;
    private final Map<String, Boolean> isIdConstant;
    private final Map<String, Number> constantToNumber;
    private final Map<String, String> varToArg;
    private final ValType valType;

    private Statement assertion;
    private MethodDeclaration testMethod;

    public TestMethodGen(PatternV pattern, Map<String, Boolean> isFormalizedIdConstant) {
        this.pattern = pattern;
        this.valType = pattern.getValType();
        this.stats = pattern.getStats();
        this.isIdConstant = isFormalizedIdConstant;
        this.constantToNumber = makeConstantToNumber();
        this.varToArg = makeVarMappings('a');
        generateAssertion();
        generateTestMethod();
    }

    public Map<String, String> getParamToArg() {
        return varToArg;
    }

    public Statement getAssertion() {
        return assertion;
    }

    public MethodDeclaration getTestMethod() {
        return testMethod;
    }

    public String getTestMethodName() {
        return "test" + stats.getName();
    }

    // Asserts.assertEQ(a - 2022, test1(a));
    private void generateAssertion() {
        // Use argument a, b, c instead of params
        assertion = new ExpressionStmt(new MethodCallExpr(
                new NameExpr("Asserts"),
                "assertEQ",
                new NodeList<>(
                        replaceConstantWithNumber(renameVar(stats.getActionBefore(), varToArg), constantToNumber),
                        new MethodCallExpr(getTestMethodName()).setArguments(toNodeListOfParams(varToArg.values())))));
    }

    private static NodeList<Expression> toNodeListOfParams(Collection<String> collection) {
        return new NodeList<>(collection.stream().map(NameExpr::new).collect(Collectors.toList()));
    }

    /**
     * Rename test params with passed argument.
     */
    private Expression renameVar(Expression expr, Map<String, String> renames) {
        return (Expression) expr.clone().accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(NameExpr ne, Void arg) {
                String name = ne.getNameAsString();
                if (renames.containsKey(name)) {
                    return ne.setName(renames.get(name));
                }
                return super.visit(ne, arg);
            }
        }, null);
    }

    // @Pattern
    // public void pSub1(int x, @Constant int c0) {
    //     before(x - c0);
    //     after(x + -c0);
    // }
    // @Test
    // @IR(failOn = {IRNode.SUB})
    // @IR(counts = {IRNode.ADD, "1"})
    // // Checks (x - c0) => x + (-c0)
    // public int test1(int x) {
    //     return (x - 2022);
    // }
    private void generateTestMethod(){
        Expression before = stats.getActionBefore();
        Expression after = stats.getActionAfter();
        // replace constant with int number in before and after
        before = replaceConstantWithNumber(before, constantToNumber);
        after = replaceConstantWithNumber(after, constantToNumber);
        // signature
        MethodDeclaration md = StaticJavaParser.parseMethodDeclaration(
                "public " + valType.getTypeName() + " " + getTestMethodName() + "("
                + MiscUtil.makeParamDeclList(varToArg.keySet(), valType)
                + ") {}");
        // comment
        md.setLineComment("Checks " + before + " => " + after);
        // set body
        md.setBody(new BlockStmt().addStatement(new ReturnStmt(before)));
        // annotations
        md.addAnnotation(new MarkerAnnotationExpr("Test"));
        setIRAnnot(md);
        testMethod = md;
    }

    private void setIRAnnot(MethodDeclaration testMd) {
        CGExpr before = pattern.getBeforeNode();
        CGExpr after = pattern.getAfterNode();
        Map<BinNode.NodeType, Integer> beforeCounts = countNodeType(before);
        Map<BinNode.NodeType, Integer> afterCounts = countNodeType(after);

        // failOn = {IRNode.SUB, IRNode.ADD, ...}
        List<Expression> failOnNodes = beforeCounts.keySet().stream()
                .filter(n -> !afterCounts.containsKey(n))
                .map(TestMethodGen::toIRNodeFieldAccessExpr)
                .collect(Collectors.toList());
        if (failOnNodes.size() > 0) {
            // Only add if there are
            NormalAnnotationExpr irFailOnAnnot = new NormalAnnotationExpr()
                    .setName("IR")
                    .asNormalAnnotationExpr()
                    .addPair("failOn", JPUtil.arrayInitExpr(new NodeList<>(failOnNodes)));
            testMd.addAnnotation(irFailOnAnnot);
        }

        // counts = {IRNode.ADD, "1", IRNode.SUB, "1"}
        List<Expression> countsNodes = new LinkedList<>();
        for (Map.Entry<BinNode.NodeType, Integer> e : afterCounts.entrySet()) {
            countsNodes.add(toIRNodeFieldAccessExpr(e.getKey()));
            countsNodes.add(new StringLiteralExpr(e.getValue().toString()));
        }
        NormalAnnotationExpr irCountsAnnot = new NormalAnnotationExpr()
                .setName("IR")
                .asNormalAnnotationExpr()
                .addPair("counts", JPUtil.arrayInitExpr(countsNodes));
        testMd.addAnnotation(irCountsAnnot);
    }

    private static FieldAccessExpr toIRNodeFieldAccessExpr(BinNode.NodeType nodeType) {
        return new FieldAccessExpr(new NameExpr("IRNode"), nodeType.toTestCodeGen());
    }

    private Map<BinNode.NodeType, Integer> countNodeType(CGExpr node) {
        Map<BinNode.NodeType, Integer> counts = new HashMap<>();
        node.accept(new ExprVisitorAdapter() {
            @Override
            public void visit(BinNode binNode, Void arg) {
                counts.merge(binNode.getNodeType(), 1, Integer::sum);
                super.visit(binNode, arg);
            }

            @Override
            public void visit(UnaryNode unaryNode, Void arg) {
                // We do not have any nodeType for unary node.
                super.visit(unaryNode, arg);
            }

            @Override
            public void visit(ConstantFoldingNode constantFoldingNode, Void arg) {
                // Stop because the entire ContantFoldingNode should
                // be treated as a constant
                return;
            }
        }, null);
        return counts;
    }

    private Expression replaceConstantWithNumber(
            Expression expr, Map<String, Number> constantToNumber) {
        return (Expression) expr.clone().accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(NameExpr ne, Void arg) {
                String name = ne.getNameAsString();
                if (constantToNumber.containsKey(name)) {
                    return JPUtil.numberLiteralExpr(constantToNumber.get(name));
                }
                return super.visit(ne, arg);
            }
        }, null);
    }

    /**
     * Assign a random number for every constant.
     * Not that random for now for testing purpose :D
     * TODO: consider preconditions.
     */
    private Map<String, Number> makeConstantToNumber() {
        Map<String, Number> constantToNumber = new HashMap<>();
        Number start =  Constants.START_NUMBER;
        Number next = start;
        for (Map.Entry<String, Boolean> e : isIdConstant.entrySet()) {
            if (!e.getValue()) {
                continue;
            }
            String name = e.getKey();
            constantToNumber.put(name, next);
            next = nextNumber(start);
        }
        return constantToNumber;
    }

    private Number nextNumber(Number val) {
        switch (valType) {
        case INT:
            return val.intValue() + 1;
        case LONG:
            return val.longValue() + 1;
        default:
            throw new IllegalArgumentException("Unknown value type: " + valType);
        }
    }

    /**
     * Assign arguments or params for every variable.
     * TODO: consider preconditions.
     */
    private Map<String, String> makeVarMappings(char start) {
        // Use argument a, b, c to assign to params
        Map<String, String> varMappings = new TreeMap<>(); // make sure an alphabetic order
        for (Map.Entry<String, Boolean> e : isIdConstant.entrySet()) {
            if (!e.getValue()) {
                varMappings.put(e.getKey(), String.valueOf((char) (start + varMappings.size())));
            }
        }
        return varMappings;
    }
}
