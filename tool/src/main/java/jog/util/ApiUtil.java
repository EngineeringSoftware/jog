package jog.util;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import jog.Constants;

import java.util.function.Predicate;

/**
 * Utility methods to help match the APIs defined.
 */
public class ApiUtil {

    public static boolean isActionBefore(Statement stmt) {
        return stmt.isExpressionStmt() && isActionBefore(stmt.asExpressionStmt().getExpression());
    }

    /**
     * Matches {@code Action.before(*)}
     */
    public static boolean isActionBefore(Expression expr) {
        return isAction(expr, Constants.BEFORE_METH);
    }

    /**
     * Matches {@code Action.after(*)}
     */
    public static boolean isActionAfter(Expression expr) {
        return isAction(expr, Constants.AFTER_METH);
    }

    public static Predicate<MethodCallExpr> isActionPred(String name) {
        return expr -> isAction(expr, name);
    }

    public static boolean isAction(Expression expr, String name) {
        return isAction(expr) && getCallName(expr).equals(name);
    }

    /**
     * Matches {@code Action.*}.
     */
    public static boolean isAction(Expression expr) {
        return isApiMethod(expr, Constants.ACTION_CLZ);
    }

    public static boolean isAction(Statement stmt) {
        return stmt.isExpressionStmt() && isAction(stmt.asExpressionStmt().getExpression());
    }

    /**
     * Matches {@code Lib.<method>}.
     */
    public static boolean isLibMethod(Expression expr) {
        return isApiMethod(expr, Constants.LIB_CLZ);
    }

    public static boolean isSpecialConstruct(Expression expr) {
        return isMathMethod(expr) || isIntegerMethod(expr);
    }

    /**
     * Matches {@code Math.min} and {@code Math.max}.
     */
    public static boolean isMathMethod(Expression expr) {
        if (!isApiMethod(expr, "java.lang.Math")) {
            return false;
        }
        String name = getCallName(expr);
        return name.equals(Constants.MIN)
                || name.equals(Constants.MAX);
    }

    /**
     * Matches {@code Integer.rotateLeft} and {@code Integer.rotateRight}.
     */
    public static boolean isIntegerMethod(Expression expr) {
        if (!isApiMethod(expr, "java.lang.Integer")) {
            return false;
        }
        String name = getCallName(expr);
        return name.equals(Constants.ROTATE_LEFT)
                || name.equals(Constants.ROTATE_RIGHT);
    }

    /**
     * Matches {@code Lib.Type.*}.
     */
    public static boolean isLibTypeField(Expression expr) {
        return isApiField(expr, Constants.LIB_TYPE_CLZ);
    }

    /**
     * Matches {@code Lib.Operator.*}.
     */
    public static boolean isLibOperatorField(Expression expr) {
        return isApiField(expr, Constants.LIB_OPERATOR_CLZ);
    }

    /**
     * Matches {@code <name>.<field>}
     */
    private static boolean isApiField(Expression expr, String name) {
        if (!expr.isFieldAccessExpr()) {
            return false;
        }
        FieldAccessExpr fieldAccess = expr.asFieldAccessExpr();
        try {
            ResolvedValueDeclaration resolvedValueDeclaration = fieldAccess.resolve();
            if (!resolvedValueDeclaration.isEnumConstant()) {
                return false;
            }
            String qualifiedClassName =
                    resolvedValueDeclaration.getType().asReferenceType().getQualifiedName();
            return qualifiedClassName.equals(name);
        } catch (UnsolvedSymbolException e) {
            throw new RuntimeException("Unsolved FieldAccessExpr: " + fieldAccess);
        }
    }

    /**
     * Matches {@code <name>.<method>}
     */
    private static boolean isApiMethod(Expression expr, String name) {
        if (!expr.isMethodCallExpr()) {
            return false;
        }
        MethodCallExpr call = expr.asMethodCallExpr();
        try {
            ResolvedMethodDeclaration resolvedMethodDecl = call.resolve();
            String qualifiedClassName = resolvedMethodDecl.declaringType().getQualifiedName();
            return qualifiedClassName.equals(name);
        } catch (UnsolvedSymbolException e) {
            throw new RuntimeException("Unsolved method call expression: " + call);
        }
    }

    public static String getCallName(Expression expr) {
        return expr.asMethodCallExpr().getNameAsString();
    }
}
