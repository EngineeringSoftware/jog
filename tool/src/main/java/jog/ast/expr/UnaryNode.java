package jog.ast.expr;

import jog.ast.visitor.ReturnVisitor;
import jog.ast.visitor.VoidVisitor;
import jog.ast.visitor.CodeGenUtil.ValType;

public class UnaryNode extends CGExpr {

    public enum Op {
        MINUS("-"),
        LOGIC_NOT("!"),
        BITWISE_NOT("~");

        private final String name;

        Op(String name) {
            this.name = name;
        }

        public String asStr() {
            return name;
        }

        @Override
        public String toString() {
            return "Op{" +
                    "\"" + name + '\"' +
                    '}';
        }
    }

    private CGExpr operand;
    private final Op operator;

    public UnaryNode(CGExpr operand, Op operator, ValType valType) {
        super(valType);
        this.operand = operand;
        this.operator = operator;
    }

    public Op getOperator() {
        return operator;
    }

    public CGExpr getOperand() {
        return operand;
    }

    public void setOperand(CGExpr operand) {
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "UnaryNode{" +
                operator +
                operand +
                '}';
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
