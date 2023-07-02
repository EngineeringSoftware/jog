package jog.ast.expr;

import jog.ast.Node;
import jog.ast.visitor.CodeGenUtil.ValType;

public abstract class CGExpr extends Node {

    private ValType valType;

    public CGExpr(ValType valType) {
        this.valType = valType;
    }

    public ValType getValType() {
        return valType;
    }
}
