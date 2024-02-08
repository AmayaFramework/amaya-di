package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

public interface Repository {
    Function0<Object> get(Artifact artifact);

    boolean contains(Artifact artifact);

    boolean add(Artifact artifact, Function0<Object> supplier);

    boolean remove(Artifact artifact);

    void clear();
}
