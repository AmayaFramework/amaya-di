package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Executable;

public interface ExecutableScheme<T extends Executable> extends Scheme<T> {
    Artifact[] getMapping();
}
