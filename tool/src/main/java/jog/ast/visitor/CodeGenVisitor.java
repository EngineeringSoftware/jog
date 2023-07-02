package jog.ast.visitor;

import jog.ast.stmt.CGAfterStmt;
import jog.ast.stmt.CGTmpDeclStmt;
import jog.ast.stmt.CGBeforeStmt;
import jog.ast.stmt.CGBlockStmt;
import jog.ast.stmt.CGGeneralStmt;
import jog.ast.stmt.CGIfStmt;
import jog.ast.stmt.CGStmt;
import jog.ast.expr.CGExpr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CodeGenVisitor extends StmtVisitorAdapter {

    private final Deque<String> stack = new ArrayDeque<>();

    private final Map<CGExpr, String> nodeToCodeGen;
    private final Map<CGExpr, List<String>> nodeToCodeGenList;

    public CodeGenVisitor(Map<CGExpr, String> nodeToCodeGen,
            Map<CGExpr, List<String>> nodeToCodeGenList) {
        this.nodeToCodeGen = nodeToCodeGen;
        this.nodeToCodeGenList = nodeToCodeGenList;
    }

    public String getResult() {
        return stack.peek();
    }

    // Has to be invoked first than after/pred
    @Override
    public void visit(CGBeforeStmt cgBeforeStmt, Void arg) {
        StringBuilder sb = new StringBuilder();
        CGExpr before = cgBeforeStmt.getCondition();
        BeforeVisitor bv = new BeforeVisitor(nodeToCodeGen, nodeToCodeGenList);
        before.accept(bv, null);

        sb.append("if (").append(bv.getResult()).append(") ");
        cgBeforeStmt.getThenStmt().accept(this, arg);
        sb.append(stack.pop());
        stack.push(sb.toString());
    }

    @Override
    public void visit(CGAfterStmt cgAfterStmt, Void arg) {
        StringBuilder sb = new StringBuilder();
        CGExpr after = cgAfterStmt.getExpression();
        AfterVisitor av = new AfterVisitor(nodeToCodeGen);
        after.accept(av, null);
        sb.append("return ").append(av.getResult()).append(";");
        stack.push(sb.toString());
    }

    @Override
    public void visit(CGBlockStmt cgBlockStmt, Void arg) {
        List<CGStmt> stmts = cgBlockStmt.getStmts();
        StringJoiner sj = new StringJoiner("\n", "{\n", "\n}");
        for (CGStmt s : stmts) {
            s.accept(this, null);
            String c = stack.pop();
            // extra check for assignment statement because it will
            // yield an empty string.
            if (!c.isEmpty()) {
                sj.add(c);
            }
        }
        stack.push(sj.toString());
    }

    @Override
    public void visit(CGGeneralStmt cgGeneralStmt, Void arg) {
        stack.push(cgGeneralStmt.getStmt());
    }

    @Override
    public void visit(CGIfStmt cgIfStmt, Void arg) {
        StringBuilder sb = new StringBuilder();
        CGExpr pred = cgIfStmt.getCondition();
        PredVisitor pv = new PredVisitor(nodeToCodeGen);
        pred.accept(pv, null);

        sb.append("if (").append(pv.getResult()).append(") ");
        cgIfStmt.getThenStmt().accept(this, arg);
        sb.append(stack.pop());
        if (cgIfStmt.hasElseBranch()) {
            cgIfStmt.getElseStmt().accept(this, arg);
            sb.append(" else ").append(stack.pop());
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(CGTmpDeclStmt cgTmpDeclStmt, Void arg) {
        // do not generate any code for assignment statement
        stack.push("");
    }
}
