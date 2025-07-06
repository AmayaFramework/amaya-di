package io.github.amayaframework.di.asm;

import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.HashTypeRepository;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeProvider;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.ReflectSchemaFactory;
import io.github.amayaframework.di.schema.SchemaFactory;

import java.lang.reflect.Type;

final class Util {
    static final Type S2_STR = new JType<Service2<String>>() {}.getType();
    static final Type S2_INT = new JType<Service2<Integer>>() {}.getType();
    static final SchemaFactory SCHEMA_FACTORY = new ReflectSchemaFactory(Inject.class);
    static final ClassSchema S1_SCHEMA = SCHEMA_FACTORY.create(Service1.class);
    static final ClassSchema S3_SCHEMA = SCHEMA_FACTORY.create(Service3.class);
    static final ClassSchema APP_SCHEMA = SCHEMA_FACTORY.create(App.class);
    static final ClassSchema EMPTY_SCHEMA = SCHEMA_FACTORY.create(EmptyService.class);

    @SuppressWarnings("unchecked")
    static <T> T get(TypeProvider provider, Class<T> type) {
        var factory = provider.get(type);
        try {
            return (T) factory.create(provider);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T get(ObjectFactory factory, Class<T> type) {
        try {
            return (T) factory.create(null);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static TypeRepository create(String s, Integer i) {
        var ret = new HashTypeRepository();
        ret.put(String.class, v -> s);
        ret.put(Integer.class, v -> i);
        return ret;
    }

    static TypeRepository create(String s, Integer i, String s2, Integer i2) {
        var ret = create(s, i);
        ret.put(S2_STR, v -> new Service2<>(s2));
        ret.put(S2_INT, v -> new Service2<>(i2));
        return ret;
    }
}
