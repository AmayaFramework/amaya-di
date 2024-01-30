package io.github.amayaframework.di.stub;

import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.Repository;
import io.github.amayaframework.di.scheme.ClassScheme;

public interface StubFactory {
    Function0<?> create(ClassScheme scheme, Repository repository);
}
