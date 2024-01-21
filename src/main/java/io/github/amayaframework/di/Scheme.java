package io.github.amayaframework.di;

import java.lang.reflect.Member;
import java.util.List;

public interface Scheme<T extends Member> {
    T getTarget();

    List<Dependency> getDependencies();

    void accept(SchemeVisitor visitor);
}
