package io.github.amayaframework.di;

public interface SchemeVisitor {
    void visit(FieldScheme scheme);

    void visit(MethodScheme scheme);

    void visit(ConstructorScheme scheme);
}
