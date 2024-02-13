package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.function.Function;

public interface ArtifactProvider extends Function<Artifact, Function0<Object>> {
    Function0<Object> apply(Artifact artifact);
}
