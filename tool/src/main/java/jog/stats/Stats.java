package jog.stats;

import com.github.javaparser.ast.expr.Expression;
import jog.ast.expr.BinNode;
import jog.ast.visitor.CodeGenUtil.ValType;
import jog.codegen.PatternV;

import java.util.List;
import java.util.Set;

/**
 * Statistics of a pattern.
 */
public class Stats {

    private final String name;
    // Which type of node the translation of this pattern belongs to
    private final BinNode.NodeType nodeType;
    private final ValType valType;
    private final String origin;
    private final int pr;
    private final Expression actionBefore;
    private final List<Expression> preconditions;
    private final Expression actionAfter;
    private final Set<PatternV> shadows;
    private final Set<PatternV> composites;
    private final String patternCode;
    private final String generatedCode;

    public Stats(
            String name,
            BinNode.NodeType nodeType,
            ValType valType,
            Expression actionBefore,
            List<Expression> preconditions,
            Expression actionAfter,
            String origin,
            int pr,
            Set<PatternV> shadows,
            Set<PatternV> composites,
            String patternCode,
            String generatedCode) {
        this.name = name;
        this.nodeType = nodeType;
        this.valType = valType;
        this.actionBefore = actionBefore;
        this.preconditions = preconditions;
        this.actionAfter = actionAfter;
        this.origin = origin;
        this.pr = pr;
        this.shadows = shadows;
        this.composites = composites;
        this.patternCode = patternCode;
        this.generatedCode = generatedCode;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public String getPatternCode() {
        return patternCode;
    }

    public Set<PatternV> getComposites() {
        return composites;
    }

    public Set<PatternV> getShadows() {
        return shadows;
    }

    public int getPr() {
        return pr;
    }

    public String getOrigin() {
        return origin;
    }

    public BinNode.NodeType getNodeType() {
        return nodeType;
    }

    public ValType getValType() {
        return valType;
    }

    public String getName() {
        return name;
    }

    public Expression getActionBefore() {
        return actionBefore;
    }

    public List<Expression> getPreconditions() {
        return preconditions;
    }

    public Expression getActionAfter() {
        return actionAfter;
    }
}
