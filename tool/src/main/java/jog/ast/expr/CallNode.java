package jog.ast.expr;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallNode extends CGExpr {

    public enum Name {
        GET_HI,
        GET_LO,
        GET_TYPE,
        MATCH_RULE_SUPPORTED,
        OK_TO_CONVERT,
        OUT_CNT,
    }

    private Name name;
    private List<CGExpr> arguments;

    public CallNode(Name name, ValType valType, CGExpr... arguments) {
        this(name, List.of(arguments), valType);
    }

    public CallNode(Name name, List<CGExpr> arguments, ValType valType) {
        super(valType);
        this.name = name;
        this.arguments = arguments;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<CGExpr> getArguments() {
        return arguments;
    }

    public void setArguments(List<CGExpr> arguments) {
        this.arguments = arguments;
    }

    /**
     * Modify every single argument in the list instead of updating
     * the entire list.
     */
    public void updateArguments(List<CGExpr> arguments) {
        Map<Integer, CGExpr> newArguments = new HashMap<>();
        arguments.forEach(s -> newArguments.put(newArguments.size(), s));
        for (int i = 0; i < this.arguments.size(); i++) {
            if (newArguments.get(i) != this.arguments.get(i)) {
                this.arguments.set(i, newArguments.get(i));
            }
        }
    }

    @Override
    public <R> R accept(ReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <A> void accept(VoidVisitor<A> visitor, A arg) {
        visitor.visit(this, arg);
    }

    @Override
    public String toString() {
        return "CallNode{" +
                name +
                arguments +
                '}';
    }
}
