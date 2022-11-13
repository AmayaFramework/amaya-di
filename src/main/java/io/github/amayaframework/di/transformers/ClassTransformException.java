package io.github.amayaframework.di.transformers;

import java.util.List;

public class ClassTransformException extends IllegalStateException {
    private final List<Throwable> problems;

    public ClassTransformException(Class<?> clazz, List<Throwable> problems) {
        super("Can't transform class " + clazz + " due to some problems");
        this.problems = problems;
    }

    public List<Throwable> getProblems() {
        return problems;
    }
}
