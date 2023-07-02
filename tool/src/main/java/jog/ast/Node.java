package jog.ast;

import jog.ast.visitor.CloneVisitor;
import jog.ast.visitor.Visitable;

public abstract class Node implements Visitable {

    @Override
    public Node clone() {
        return accept(new CloneVisitor());
    }
}
