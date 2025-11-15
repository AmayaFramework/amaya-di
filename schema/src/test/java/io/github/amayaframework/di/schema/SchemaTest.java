package io.github.amayaframework.di.schema;

import com.github.romanqed.jtype.Types;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class SchemaTest extends Assertions {
    private static final SchemaFactory FACTORY = new ReflectSchemaFactory(Inject.class);

    @Test
    public void testEmptyClass() throws NoSuchMethodException {
        var schema = FACTORY.create(Empty.class);
        assertEquals(Empty.class, schema.target());
        assertTrue(schema.types().isEmpty());
        assertTrue(schema.fieldSchemas().isEmpty());
        assertTrue(schema.methodSchemas().isEmpty());
        assertEquals(Empty.class.getConstructor(), schema.constructorSchema().target());
    }

    @Test
    public void testNoConstructors() {
        assertThrows(IllegalClassException.class, () -> FACTORY.create(NoConstructors.class));
    }

    @Test
    public void testOneConstructor() throws NoSuchMethodException {
        var schema = FACTORY.create(OneConstructor.class);
        assertEquals(OneConstructor.class, schema.target());
        assertEquals(1, schema.types().size());
        assertTrue(schema.fieldSchemas().isEmpty());
        assertTrue(schema.methodSchemas().isEmpty());
        assertEquals(
                OneConstructor.class.getConstructor(Object.class),
                schema.constructorSchema().target()
        );
        assertEquals(Set.of(Object.class), schema.constructorSchema().types());
    }

    @Test
    public void testManyConstructors() {
        assertThrows(IllegalClassException.class, () -> FACTORY.create(ManyConstructors.class));
    }

    @Test
    public void testAnnotatedConstructor() throws NoSuchMethodException {
        var schema = FACTORY.create(AnnotatedConstructor.class);
        assertEquals(AnnotatedConstructor.class, schema.target());
        assertEquals(1, schema.types().size());
        assertTrue(schema.fieldSchemas().isEmpty());
        assertTrue(schema.methodSchemas().isEmpty());
        assertEquals(
                AnnotatedConstructor.class.getConstructor(Object.class),
                schema.constructorSchema().target()
        );
        assertEquals(Set.of(Object.class), schema.constructorSchema().types());
    }

    @Test
    public void testFields() throws NoSuchFieldException {
        var schema = FACTORY.create(Fields.class);
        var type = Object.class;
        var schemas = Set.of(new FieldSchema(Fields.class.getField("f1"), type));
        assertEquals(Fields.class, schema.target());
        assertEquals(Set.of(type), schema.types());
        assertEquals(schemas, schema.fieldSchemas());
    }

    @Test
    public void testMethods() throws NoSuchMethodException {
        var schema = FACTORY.create(Methods.class);
        var type = (Type) Object.class;
        var types = Set.of(type);
        var mapping = new Type[]{type};
        var schemas = Set.of(
                new MethodSchema(Methods.class.getMethod("psm2", Methods.class, Object.class), types, mapping),
                new MethodSchema(Methods.class.getMethod("pm2", Object.class), types, mapping)
        );
        assertEquals(Methods.class, schema.target());
        assertEquals(types, schema.types());
        assertEquals(schemas, schema.methodSchemas());
    }

    @Test
    public void testInvalidStaticSetter() {
        assertThrows(IllegalClassException.class, () -> FACTORY.create(InvalidStaticSetter.class));
    }

    @Test
    public void testWildcards() {
        var schema = FACTORY.create(Wildcards.class);
        var types = Set.of(
                Types.of(List.class, Object.class),
                Types.of(BiConsumer.class, Object.class, Object.class),
                Types.of(List.class, String.class)
        );
        assertEquals(Wildcards.class, schema.target());
        assertEquals(types, schema.types());
    }

    @Test
    public void testGenerics() {
        var schema = FACTORY.create(Generics.class);
        var types = Set.of(
                Types.of(List.class, String.class),
                Types.of(BiConsumer.class,
                        Types.of(List.class, Types.of(List.class, String.class)),
                        Types.of(List.class, String.class)),
                Types.of(List.class, String[].class),
                Types.of(List.class, Types.of(Types.of(List.class, String.class))),
                Types.of(List.class, Types.of(Types.of(List.class, Types.of(List.class, String[].class)), 2))
        );
        assertEquals(Generics.class, schema.target());
        assertEquals(types, schema.types());
    }

    @Test
    public void testGenericClassWithField() {
        assertThrows(IllegalMemberException.class, () -> FACTORY.create(GenericClassWithField.class));
    }
    
    @Test
    public void testGenericClassWithConstructor() {
        assertThrows(IllegalMemberException.class, () -> FACTORY.create(GenericClassWithCtor.class));
    }
    
    @Test
    public void testGenericClassWithMethod() {
        assertThrows(IllegalMemberException.class, () -> FACTORY.create(GenericClassWithMethod.class));
    }

    public static final class GenericClassWithField<T> {
        @Inject
        public T field;
    }
    
    public static final class GenericClassWithCtor<T> {
        public GenericClassWithCtor(T v) {
        }
    }
    
    public static final class GenericClassWithMethod<T> {
        @Inject
        public void setField(T v) {
        }
    }

    @Test
    public void testGenericConstructor() {
        assertThrows(IllegalMemberException.class, () -> FACTORY.create(GenericConstructor.class));
    }

    @Test
    public void testGenericMethod() {
        assertThrows(IllegalMemberException.class, () -> FACTORY.create(GenericMethod.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Inject {
    }

    public static final class Empty {
    }

    public static final class NoConstructors {
        private NoConstructors() {
        }
    }

    public static final class OneConstructor {
        public OneConstructor(Object dep) {
        }
    }

    public static final class ManyConstructors {
        public ManyConstructors() {
        }

        public ManyConstructors(Object arg) {
        }
    }

    public static final class AnnotatedConstructor {
        public AnnotatedConstructor() {
        }

        @Inject
        public AnnotatedConstructor(Object arg) {
        }
    }

    public static class Fields {
        // Static fields
        @Inject
        public static Object sf1;
        @Inject
        static Object sf2;
        @Inject
        private static Object sf3;
        // Virtual fields
        @Inject
        public Object f1;
        @Inject
        protected Object f3;
        @Inject
        Object f2;
        @Inject
        private Object f4;
    }

    public static class Methods {
        @Inject
        public static void psm1(Object dep) {
        }

        @Inject
        public static void psm2(Methods m, Object dep) {
        }

        @Inject
        static void m3(Object dep) {
        }

        @Inject
        static void m4(Methods m, Object dep) {
        }

        @Inject
        private static void prm3(Object dep) {
        }

        @Inject
        private static void prm4(Methods m, Object dep) {
        }

        // Public methods
        @Inject
        public void pm1() {
        }

        @Inject
        public void pm2(Object dep) {
        }

        // Package-private methods
        @Inject
        void m1() {
        }

        @Inject
        void m2(Object dep) {
        }

        // Protected methods
        @Inject
        void ptm1() {
        }

        @Inject
        void ptm2(Object dep) {
        }

        // Private methods
        @Inject
        private void prm1() {
        }

        @Inject
        private void prm2(Object dep) {
        }
    }

    public static final class InvalidStaticSetter {
        @Inject
        public static void s(String ref, Object dep) {
        }
    }

    public static final class Wildcards {
        @Inject
        public List<?> w1;
        @Inject
        public List<Object> w2;
        @Inject
        public BiConsumer<Object, Object> w3;
        @Inject
        public BiConsumer<?, Object> w4;
        @Inject
        public BiConsumer<Object, ?> w5;
        @Inject
        public BiConsumer<?, ?> w6;
        @Inject
        public List<? extends String> w7;
    }

    public static final class InvalidWildcard {
        @Inject
        public List<? super Integer> f1;
    }

    public static final class Generics {
        @Inject
        public List<String> g1;
        @Inject
        public BiConsumer<List<List<String>>, List<String>> g2;
        @Inject
        public List<String[]> g3;
        @Inject
        public List<List<String>[]> g4;
        @Inject
        public List<? extends List<? extends List<? extends String[]>>[][]> g5;
    }

    public static final class GenericConstructor {
        public <T> GenericConstructor(T t) {
        }
    }

    public static final class GenericMethod {
        @Inject
        public <T> void gm(T t) {
        }
    }
}
