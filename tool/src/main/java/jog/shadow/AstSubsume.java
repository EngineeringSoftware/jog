package jog.shadow;

import jog.ast.expr.CGExpr;

import java.util.List;

/**
 * Check is astX subsumes astY.
 */
public class AstSubsume extends AstRelationBase {

    public AstSubsume(CGExpr astX, CGExpr astY) {
        super(astX, astY);
    }

    public AstSubsume(CGExpr astX, CGExpr astY, List<CGExpr> preXs, List<CGExpr> preYs) {
        super(astX, astY, preXs, preYs);
    }

    public Status check() {
        if (!shapeMatch()) {
            return Status.NOT_OK;
        }
        createZ3Vars();
        constrainShapeAndValue();
        constrainEqv();
        Status shapeCheckRes = checkShapes();
        if (shapeCheckRes != Status.OK) {
            return shapeCheckRes;
        }
        if (noPreconditions()) {
            return Status.OK;
        }
        // Do value check only if shape check passes and there are
        // preconditions to check
        constrainPreds();
        return checkValues();
    }

    private Status checkShapes() {
        return proofStatusToStatus.get(z3Solver.proveSubsume(false));
    }

    private Status checkValues() {
        return proofStatusToStatus.get(z3Solver.proveSubsume(true));
    }
}
