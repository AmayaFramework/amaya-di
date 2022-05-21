package io.github.amayaframework.di;

import io.github.amayaframework.nodes.ConstructorNode;
import io.github.amayaframework.nodes.FieldNode;
import io.github.amayaframework.nodes.MethodNode;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public final class InjectNodeFactory implements InjectFactory {

    @Override
    public List<InjectType> getTypes() {
        Iterable<Class<?>> found = ClassIndex.getAnnotated(DirectInject.class);
        List<InjectType> ret = new LinkedList<>();
        found.forEach(e -> ret.add(getInjectType(e)));
        return ret;
    }

    @Override
    public InjectType getInjectType(Class<?> clazz) {
        boolean direct = clazz.isAnnotationPresent(DirectInject.class);
        List<ConstructorNode> constructors = getConstructors(clazz);
        List<MethodNode> methods = getMethods(clazz);
        List<FieldNode> fields = getFields(clazz);
        return new AbstractInjectType(clazz) {
            @Override
            public boolean isDirect() {
                return direct;
            }

            @Override
            public List<FieldNode> getFields() {
                return fields;
            }

            @Override
            public List<MethodNode> getMethods() {
                return methods;
            }

            @Override
            public List<ConstructorNode> getConstructors() {
                return constructors;
            }
        };
    }

    @Override
    public List<ConstructorNode> getConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        List<ConstructorNode> ret = new LinkedList<>();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                ret.add(new ConstructorNode(constructor));
            }
        }
        return ret;
    }

    @Override
    public List<MethodNode> getMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        List<MethodNode> ret = new LinkedList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Inject.class)) {
                ret.add(new MethodNode(method));
            }
        }
        return ret;
    }

    @Override
    public List<FieldNode> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<FieldNode> ret = new LinkedList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                ret.add(new FieldNode(field));
            }
        }
        return ret;
    }
}
