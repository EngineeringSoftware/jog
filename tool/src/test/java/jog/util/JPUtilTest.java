package jog.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Assert;
import org.junit.Test;

public class JPUtilTest {

    @Test
    public void testIsAssignStmt() {
        Statement stmt = StaticJavaParser.parseStatement("tmp = x + 1;");
        Assert.assertTrue(JPUtil.isAssignStmt(stmt));
    }

    @Test
    public void testIsDeclStmt() {
        Statement stmt = StaticJavaParser.parseStatement("int tmp = x + 1;");
        Assert.assertTrue(JPUtil.isDeclStmt(stmt));
    }
}
