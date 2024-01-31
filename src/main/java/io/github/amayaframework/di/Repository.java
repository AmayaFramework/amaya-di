package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

public interface Repository {
    Function0<Object> get(Artifact artifact);

    void add(Artifact artifact, Function0<Object> supplier);

    void remove(Artifact artifact);

    void clear();
}
