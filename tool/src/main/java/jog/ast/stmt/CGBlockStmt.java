package jog.ast.stmt;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CGBlockStmt extends CGStmt {

    private List<CGStmt> stmts;

    public CGBlockStmt(List<CGStmt> stmts) {
        this.stmts = new ArrayList<>(stmts);
    }

    public List<CGStmt> getStmts() {
        return stmts;
    }

    public void setStmts(List<CGStmt> stmts) {
        this.stmts = new ArrayList<>(stmts);
    }

    /**
     * Modify every single statement in the list instead of updating
     * the entire list.
     */
    public void updateStmts(List<CGStmt> stmts) {
        Map<Integer, CGStmt> newStmts = new HashMap<>();
        stmts.forEach(s -> newStmts.put(newStmts.size(), s));
        for (int i = 0; i < this.stmts.size(); i++) {
            if (newStmts.get(i) != this.stmts.get(i)) {
                this.stmts.set(i, newStmts.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return "{\n" + stmts.stream().map(CGStmt::toString).collect(Collectors.joining("\n")) + "\n}";
    }

    @Override
    public <R> R accept(ReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <A> void accept(VoidVisitor<A> visitor, A arg) {
        visitor.visit(this, arg);
    }
}
