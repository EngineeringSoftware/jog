package jog.ast.expr;

import jog.ast.nodetype.LeafNode;
import jog.ast.visitor.CodeGenUtil.ValType;

/**
 * An int identifier.
 */
public abstract class IdNode extends CGExpr implements LeafNode {

    private String identifier;

    protected IdNode(String identifier, ValType valType) {
        super(valType);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public abstract boolean isCon();
}
