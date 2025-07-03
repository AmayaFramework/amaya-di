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
    private static final SchemaFactory REFLECTION_FACTORY = new ReflectSchemaFactory(Inject.class);

    public void testEmptyClass(SchemaFactory factory) {
        var scheme = factory.create(Empty.class);
        assertAll(
                () -> assertEquals(Empty.class, scheme.getTarget()),
                () -> assertTrue(scheme.getTypes().isEmpty()),
                () -> assertTrue(scheme.getFieldSchemas().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemas().isEmpty()),
                () -> assertEquals(Empty.class.getConstructor(), scheme.getConstructorSchema().getTarget())
        );
    }

    @Test
    public void testReflectionEmptyClass() {
        testEmptyClass(REFLECTION_FACTORY);
    }

    public void testNoConstructors(SchemaFactory factory) {
        assertThrows(IllegalClassException.class, () -> factory.create(NoConstructors.class));
    }

    @Test
    public void testReflectionNoConstructors() {
        testNoConstructors(REFLECTION_FACTORY);
    }

    public void testOneConstructor(SchemaFactory factory) {
        var scheme = factory.create(OneConstructor.class);
        assertAll(
                () -> assertEquals(OneConstructor.class, scheme.getTarget()),
                () -> assertEquals(1, scheme.getTypes().size()),
                () -> assertTrue(scheme.getFieldSchemas().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemas().isEmpty()),
                () -> assertEquals(
                        OneConstructor.class.getConstructor(Object.class),
                        scheme.getConstructorSchema().getTarget()
                ),
                () -> assertEquals(Set.of(Object.class), scheme.getConstructorSchema().getTypes())
        );
    }

    @Test
    public void testReflectionOneConstructor() {
        testOneConstructor(REFLECTION_FACTORY);
    }

    public void testManyConstructors(SchemaFactory factory) {
        assertThrows(IllegalClassException.class, () -> factory.create(ManyConstructors.class));
    }

    @Test
    public void testReflectionManyConstructors() {
        testManyConstructors(REFLECTION_FACTORY);
    }

    public void testAnnotatedConstructor(SchemaFactory factory) {
        var scheme = factory.create(AnnotatedConstructor.class);
        assertAll(
                () -> assertEquals(AnnotatedConstructor.class, scheme.getTarget()),
                () -> assertEquals(1, scheme.getTypes().size()),
                () -> assertTrue(scheme.getFieldSchemas().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemas().isEmpty()),
                () -> assertEquals(
                        AnnotatedConstructor.class.getConstructor(Object.class),
                        scheme.getConstructorSchema().getTarget()
                ),
                () -> assertEquals(Set.of(Object.class), scheme.getConstructorSchema().getTypes())
        );
    }

    @Test
    public void testReflectionAnnotatedConstructor() {
        testAnnotatedConstructor(REFLECTION_FACTORY);
    }

    public void testFields(SchemaFactory factory) throws NoSuchFieldException {
        var scheme = factory.create(Fields.class);
        var type = Object.class;
        var schemes = Set.of(new FieldSchema(Fields.class.getField("f1"), type));
        assertAll(
                () -> assertEquals(Fields.class, scheme.getTarget()),
                () -> assertEquals(Set.of(type), scheme.getTypes()),
                () -> assertEquals(schemes, scheme.getFieldSchemas())
        );
    }

    @Test
    public void testReflectionFields() throws NoSuchFieldException {
        testFields(REFLECTION_FACTORY);
    }

    public void testMethods(SchemaFactory factory) throws NoSuchMethodException {
        var scheme = factory.create(Methods.class);
        var type = (Type) Object.class;
        var types = Set.of(type);
        var mapping = new Type[]{type};
        var schemes = Set.of(
                new MethodSchema(Methods.class.getMethod("psm2", Methods.class, Object.class), types, mapping),
                new MethodSchema(Methods.class.getMethod("pm2", Object.class), types, mapping)
        );
        assertAll(
                () -> assertEquals(Methods.class, scheme.getTarget()),
                () -> assertEquals(types, scheme.getTypes()),
                () -> assertEquals(schemes, scheme.getMethodSchemas())
        );
    }

    @Test
    public void testReflectionMethods() throws NoSuchMethodException {
        testMethods(REFLECTION_FACTORY);
    }

    public void testInvalidStaticSetter(SchemaFactory factory) {
        assertThrows(IllegalClassException.class, () -> factory.create(InvalidStaticSetter.class));
    }

    @Test
    public void testReflectionInvalidStaticSetter() {
        testInvalidStaticSetter(REFLECTION_FACTORY);
    }

    public void testWildcards(SchemaFactory factory) {
        var scheme = factory.create(Wildcards.class);
        var types = Set.of(
                Types.of(List.class, Object.class),
                Types.of(BiConsumer.class, Object.class, Object.class),
                Types.of(List.class, String.class)
        );
        assertAll(
                () -> assertEquals(Wildcards.class, scheme.getTarget()),
                () -> assertEquals(types, scheme.getTypes())
        );
    }

    @Test
    public void testReflectionWildcards() {
        testWildcards(REFLECTION_FACTORY);
    }

    public void testGenerics(SchemaFactory factory) {
        var scheme = factory.create(Generics.class);
        var types = Set.of(
                Types.of(List.class, String.class),
                Types.of(BiConsumer.class,
                        Types.of(List.class, Types.of(List.class, String.class)),
                        Types.of(List.class, String.class)),
                Types.of(List.class, String[].class),
                Types.of(List.class, Types.of(Types.of(List.class, String.class))),
                Types.of(List.class, Types.of(Types.of(List.class, Types.of(List.class, String[].class)), 2))
        );
        assertAll(
                () -> assertEquals(Generics.class, scheme.getTarget()),
                () -> assertEquals(types, scheme.getTypes())
        );
    }

    @Test
    public void testReflectionGenerics() {
        testGenerics(REFLECTION_FACTORY);
    }

    public void testGenericConstructor(SchemaFactory factory) {
        assertThrows(IllegalMemberException.class, () -> factory.create(GenericConstructor.class));
    }

    @Test
    public void testReflectionGenericConstructor() {
        testGenericConstructor(REFLECTION_FACTORY);
    }

    public void testGenericMethod(SchemaFactory factory) {
        assertThrows(IllegalMemberException.class, () -> factory.create(GenericMethod.class));
    }

    @Test
    public void testReflectionGenericMethod() {
        testGenericMethod(REFLECTION_FACTORY);
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

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Inject {
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
