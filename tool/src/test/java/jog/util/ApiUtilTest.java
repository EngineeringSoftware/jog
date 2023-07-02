package jog.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import jog.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApiUtilTest {

    private static JavaParser parser;

    @BeforeClass
    public static void setUpClass() {
        parser = JPUtil.createJavaParser();
    }

    @Test
    public void testAPIActionBeforeStaticImported() {
        String code = String.format(
                "import static %s.%s; class Test { void m(int x, int c0) { %s(x - c0); }}",
                Constants.ACTION_CLZ, Constants.BEFORE_METH, Constants.BEFORE_METH);
        CompilationUnit cu = parser.parse(code).getResult().get();
        MethodCallExpr expr = cu.findAll(MethodCallExpr.class).get(0);
        Assert.assertTrue(String.format("%s is not %s.%s",
                        expr, Constants.ACTION_CLZ, Constants.BEFORE_METH),
                ApiUtil.isActionBefore(expr));
    }

    @Test
    public void testAPIActionBefore() {
        String code = String.format(
                "import %s; class Test { void m(int x, int c0) { %s.%s(x - c0); }}",
                Constants.ACTION_CLZ, Constants.ACTION_CLZ, Constants.BEFORE_METH);
        CompilationUnit cu = parser.parse(code).getResult().get();
        MethodCallExpr expr = cu.findAll(MethodCallExpr.class).get(0);
        Assert.assertTrue(String.format("%s is not %s.%s",
                        expr, Constants.ACTION_CLZ, Constants.BEFORE_METH),
                ApiUtil.isActionBefore(expr));
    }

    @Test
    public void testAPIActionAfterStaticImported() {
        String code = String.format(
                "import static %s.%s; class Test { void m(int x, int c0) { %s(x - c0); }}",
                Constants.ACTION_CLZ, Constants.AFTER_METH, Constants.AFTER_METH);
        CompilationUnit cu = parser.parse(code).getResult().get();
        MethodCallExpr expr = cu.findAll(MethodCallExpr.class).get(0);
        Assert.assertTrue(String.format("%s is not %s.%s",
                        expr, Constants.ACTION_CLZ, Constants.AFTER_METH),
                ApiUtil.isActionAfter(expr));
    }

    @Test
    public void testAPILibOkToConvertStaticImported() {
        String code = String.format(
                "import static %s.%s; class Test { void m(int x, int y) { %s(x, y); }}",
                Constants.LIB_CLZ, Constants.OK_TO_CONVERT_METH, Constants.OK_TO_CONVERT_METH);
        CompilationUnit cu = parser.parse(code).getResult().get();
        MethodCallExpr expr = cu.findAll(MethodCallExpr.class).get(0);
        Assert.assertTrue(String.format("%s is not %s API",
                        expr, Constants.LIB_CLZ),
                ApiUtil.isLibMethod(expr));
    }

    @Test
    public void testAPILibType() {
        String code = String.format(
                "class Test { %s m() { return %s.TOP; }}",
                Constants.LIB_TYPE_CLZ, Constants.LIB_TYPE_CLZ);
        CompilationUnit cu = parser.parse(code).getResult().get();
        FieldAccessExpr expr = cu.findAll(FieldAccessExpr.class).get(0);
        Assert.assertTrue(String.format("%s is not %s API",
                        expr, Constants.LIB_TYPE_CLZ),
                ApiUtil.isLibTypeField(expr));
    }
}
