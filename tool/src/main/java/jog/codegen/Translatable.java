package jog.codegen;

import com.github.javaparser.ast.body.MethodDeclaration;
import jog.util.JPUtil;

public abstract class Translatable {

    protected final MethodDeclaration methodDecl;
    protected StringBuilder translationBuilder;

    protected Translatable(MethodDeclaration methodDecl, String annotation) {
        if (methodDecl.getAnnotationByName(annotation).isEmpty()) {
            throw new IllegalArgumentException("Annotation @" + annotation
                    + " not found for method " + JPUtil.getMethodSig(methodDecl));
        }
        this.methodDecl = methodDecl;
    }

    public abstract void translate();

    public String getTranslation() {
        return translationBuilder.toString();
    }

    public MethodDeclaration getMethodDecl() {
        return methodDecl;
    }
}
