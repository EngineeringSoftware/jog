package jog.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * Utility methods to help use JavaParser.
 */
public class JPUtil {

    public static VariableDeclarationExpr getDeclExpr(Statement stmt) {
        return stmt.asExpressionStmt().getExpression().asVariableDeclarationExpr();
    }

    public static AssignExpr getAssignExpr(Statement stmt) {
        return stmt.asExpressionStmt().getExpression().asAssignExpr();
    }

    public static boolean isDeclStmt(Statement stmt) {
        return stmt.isExpressionStmt()
                && stmt.asExpressionStmt().getExpression().isVariableDeclarationExpr();
    }

    public static boolean isAssignStmt(Statement stmt) {
        return stmt.isExpressionStmt()
                && stmt.asExpressionStmt().getExpression().isAssignExpr();
    }

    public static int intValue(IntegerLiteralExpr expr) {
        return expr.asNumber().intValue();
    }

    public static long longValue(LongLiteralExpr expr) {
        return expr.asNumber().longValue();
    }

    public static Expression getEnclosed(Expression expr) {
        while (expr.isEnclosedExpr()) {
            expr = expr.asEnclosedExpr().getInner();
        }
        return expr;
    }

    /**
     * Returns the method signature of the given method declaration.
     * @param md the given method declaration
     * @return the method signature
     */
    public static String getMethodSig(MethodDeclaration md) {
        String name = md.getNameAsString();
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (Parameter parameter : md.getParameters()) {
            sj.add(parameter.getType().toDescriptor());
        }
        return name + sj;
    }

    /**
     * Create a JavaParser with using java symbol solver.
     */
    public static JavaParser createJavaParser() {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        // jreOnly - if true, will only resolve types from the java or
        // javax packages. If false, will resolve any kind of type on
        // the classpath used
        combinedTypeSolver.add(new ReflectionTypeSolver(false));
        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser jp = new JavaParser();
        jp.getParserConfiguration().setSymbolResolver(symbolSolver)
                .setAttributeComments(false); // ignore comments.
        return jp;
    }

    public static ArrayInitializerExpr arrayInitExpr(Collection<Expression> elems) {
        return new ArrayInitializerExpr(new NodeList<>(elems));
    }

    public static boolean isNumberLiteral(Expression expr) {
        return expr.isIntegerLiteralExpr() || expr.isLongLiteralExpr();
    }

    public static LiteralStringValueExpr numberLiteralExpr(Number value) {
        if (value instanceof Integer) {
            return integerLiteralExpr((Integer) value);
        } else if (value instanceof Long) {
            return longLiteralExpr((Long) value);
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + value.getClass());
        }
    }

    private static IntegerLiteralExpr integerLiteralExpr(int value) {
        return new IntegerLiteralExpr(String.valueOf(value));
    }

    private static LongLiteralExpr longLiteralExpr(long value) {
        return new LongLiteralExpr(String.valueOf(value));
    }
}
