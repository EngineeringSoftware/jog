package jog.shadow;

import jog.ast.Node;
import jog.ast.expr.CGExpr;
import jog.ast.expr.ConNode;
import jog.ast.expr.IdNode;
import jog.ast.expr.VarNode;
import jog.ast.visitor.ModifierVisitor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternRenamer {

    private final PatternS pattern;
    private final String varPrefix;
    private final String conPrefix;
    private int varCount;
    private int conCount;
    private final Set<CGExpr> renamed;

    private PatternRenamer(PatternS pattern, String prefix) {
        this.pattern = pattern;
        this.varPrefix = prefix;
        conPrefix = "C";
        varCount = 0;
        conCount = 0;
        renamed = new HashSet<>();
    }

    public static void rename(PatternS pattern, String prefix) {
        new PatternRenamer(pattern, prefix).rename();
    }

    private void rename() {
        pattern.before = rename(pattern.before);
        pattern.after = rename(pattern.after);
        pattern.preconditions = pattern.preconditions.stream().map(this::rename).collect(Collectors.toList());
    }

    private CGExpr rename(CGExpr root) {
        return (CGExpr) root.accept(new ModifierVisitor() {
            @Override
            public Node visit(VarNode varNode) {
                return rename(varNode, varPrefix + ++varCount);
            }

            @Override
            public Node visit(ConNode conNode) {
                return rename(conNode, conPrefix + ++conCount);
            }

            private Node rename(IdNode n, String newId) {
                if (!renamed.add(n)) {
                    return n;
                }
                n.setIdentifier(newId);
                return n;
            }
        });
    }
}
