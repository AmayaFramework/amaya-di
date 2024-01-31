package io.github.amayaframework.di.stub;

import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.Artifact;
import io.github.amayaframework.di.scheme.ClassScheme;

import java.util.function.Function;

public interface StubFactory {
    Function0<?> create(ClassScheme scheme, Function<Artifact, Function0<Object>> provider);
}
