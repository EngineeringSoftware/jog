package jog.ast.visitor;

public interface Visitable {

    <R> R accept(ReturnVisitor<R> visitor);

    <A> void accept(VoidVisitor<A> visitor, A arg);
}
