package jog.shadow;

import jog.ast.expr.CGExpr;
import jog.codegen.PatternV;

import java.util.List;

public class PatternS {
    public CGExpr before;
    public CGExpr after;
    public List<CGExpr> preconditions;

    public PatternS(PatternV p) {
        this(p.getBeforeNode(), p.getAfterNode(), p.getPreconditions());
    }

    public PatternS(CGExpr before, CGExpr after, CGExpr... preconditions) {
        this.before = before;
        this.after = after;
        this.preconditions = List.of(preconditions);
    }

    public PatternS(CGExpr before, CGExpr after, List<CGExpr> preconditions) {
        this.before = before;
        this.after = after;
        this.preconditions = preconditions;
    }

    @Override
    public String toString() {
        return "PatternS{" +
                "before=" + before +
                ", after=" + after +
                ", preconditions=" + preconditions +
                '}';
    }
}
