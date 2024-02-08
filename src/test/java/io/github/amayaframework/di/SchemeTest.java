package io.github.amayaframework.di;

import io.github.amayaframework.di.scheme.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class SchemeTest extends Assertions {
    private static final SchemeFactory REFLECTION_FACTORY = new ReflectionSchemeFactory(Inject.class);

    public void testEmptyClass(SchemeFactory factory) {
        var scheme = factory.create(Empty.class);
        assertAll(
                () -> assertEquals(Empty.class, scheme.getTarget()),
                () -> assertTrue(scheme.getArtifacts().isEmpty()),
                () -> assertTrue(scheme.getFieldSchemes().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemes().isEmpty()),
                () -> assertEquals(Empty.class.getConstructor(), scheme.getConstructorScheme().getTarget())
        );
    }

    @Test
    public void testReflectionEmptyClass() {
        testEmptyClass(REFLECTION_FACTORY);
    }

    public void testNoConstructors(SchemeFactory factory) {
        assertThrows(IllegalSchemeException.class, () -> factory.create(NoConstructors.class));
    }

    @Test
    public void testReflectionNoConstructors() {
        testNoConstructors(REFLECTION_FACTORY);
    }

    public void testOneConstructor(SchemeFactory factory) {
        var scheme = factory.create(OneConstructor.class);
        assertAll(
                () -> assertEquals(OneConstructor.class, scheme.getTarget()),
                () -> assertEquals(scheme.getArtifacts().size(), 1),
                () -> assertTrue(scheme.getFieldSchemes().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemes().isEmpty()),
                () -> assertEquals(
                        OneConstructor.class.getConstructor(Object.class),
                        scheme.getConstructorScheme().getTarget()
                ),
                () -> assertEquals(Set.of(new Artifact(Object.class)), scheme.getConstructorScheme().getArtifacts())
        );
    }

    @Test
    public void testReflectionOneConstructor() {
        testOneConstructor(REFLECTION_FACTORY);
    }

    public void testManyConstructors(SchemeFactory factory) {
        assertThrows(IllegalSchemeException.class, () -> factory.create(ManyConstructors.class));
    }

    @Test
    public void testReflectionManyConstructors() {
        testManyConstructors(REFLECTION_FACTORY);
    }

    public void testAnnotatedConstructor(SchemeFactory factory) {
        var scheme = factory.create(AnnotatedConstructor.class);
        assertAll(
                () -> assertEquals(AnnotatedConstructor.class, scheme.getTarget()),
                () -> assertEquals(scheme.getArtifacts().size(), 1),
                () -> assertTrue(scheme.getFieldSchemes().isEmpty()),
                () -> assertTrue(scheme.getMethodSchemes().isEmpty()),
                () -> assertEquals(
                        AnnotatedConstructor.class.getConstructor(Object.class),
                        scheme.getConstructorScheme().getTarget()
                ),
                () -> assertEquals(Set.of(new Artifact(Object.class)), scheme.getConstructorScheme().getArtifacts())
        );
    }

    @Test
    public void testReflectionAnnotatedConstructor() {
        testAnnotatedConstructor(REFLECTION_FACTORY);
    }

    public void testFields(SchemeFactory factory) throws NoSuchFieldException {
        var scheme = factory.create(Fields.class);
        var artifact = new Artifact(Object.class);
        var schemes = Set.of(new FieldScheme(Fields.class.getField("f1"), artifact));
        assertAll(
                () -> assertEquals(Fields.class, scheme.getTarget()),
                () -> assertEquals(Set.of(artifact), scheme.getArtifacts()),
                () -> assertEquals(schemes, scheme.getFieldSchemes())
        );
    }

    @Test
    public void testReflectionFields() throws NoSuchFieldException {
        testFields(REFLECTION_FACTORY);
    }

    public void testMethods(SchemeFactory factory) throws NoSuchMethodException {
        var scheme = factory.create(Methods.class);
        var artifact = new Artifact(Object.class);
        var artifacts = Set.of(artifact);
        var mapping = new Artifact[]{artifact};
        var schemes = Set.of(
                new MethodScheme(Methods.class.getMethod("psm2", Methods.class, Object.class), artifacts, mapping),
                new MethodScheme(Methods.class.getMethod("pm2", Object.class), artifacts, mapping)
        );
        assertAll(
                () -> assertEquals(Methods.class, scheme.getTarget()),
                () -> assertEquals(artifacts, scheme.getArtifacts()),
                () -> assertEquals(schemes, scheme.getMethodSchemes())
        );
    }

    @Test
    public void testReflectionMethods() throws NoSuchMethodException {
        testMethods(REFLECTION_FACTORY);
    }

    public void testInvalidStaticSetter(SchemeFactory factory) {
        assertThrows(IllegalSchemeException.class, () -> factory.create(InvalidStaticSetter.class));
    }

    @Test
    public void testReflectionInvalidStaticSetter() {
        testInvalidStaticSetter(REFLECTION_FACTORY);
    }

    public void testWildcards(SchemeFactory factory) {
        var scheme = factory.create(Wildcards.class);
        var artifacts = Set.of(
                new Artifact(List.class),
                new Artifact(BiConsumer.class),
                new Artifact(List.class, String.class)
        );
        assertAll(
                () -> assertEquals(Wildcards.class, scheme.getTarget()),
                () -> assertEquals(artifacts, scheme.getArtifacts())
        );
    }

    @Test
    public void testReflectionWildcards() {
        testWildcards(REFLECTION_FACTORY);
    }

    public void testSuperWildcard(SchemeFactory factory) {
        assertThrows(IllegalTypeException.class, () -> factory.create(InvalidWildcard.class));
    }

    @Test
    public void testReflectionSuperWildcard() {
        testSuperWildcard(REFLECTION_FACTORY);
    }

    public void testGenerics(SchemeFactory factory) {
        var scheme = factory.create(Generics.class);
        var artifacts = Set.of(
                new Artifact(List.class, String.class),
                Artifact.of(BiConsumer.class,
                        new Artifact(List.class, new Artifact(List.class, String.class)),
                        new Artifact(List.class, String.class)),
                new Artifact(List.class, String[].class),
                new Artifact(List.class, new Artifact(List[].class, String.class)),
                new Artifact(List.class, new Artifact(List[][].class, new Artifact(List.class, String[].class)))
        );
        assertAll(
                () -> assertEquals(Generics.class, scheme.getTarget()),
                () -> assertEquals(artifacts, scheme.getArtifacts())
        );
    }

    @Test
    public void testReflectionGenerics() {
        testGenerics(REFLECTION_FACTORY);
    }

    public void testGenericClass(SchemeFactory factory) {
        assertThrows(IllegalSchemeException.class, () -> factory.create(GenericClass.class));
    }

    @Test
    public void testReflectionGenericClass() {
        testGenericClass(REFLECTION_FACTORY);
    }

    public void testGenericMethod(SchemeFactory factory) {
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

    public static final class GenericClass<T> {
    }

    public static final class GenericMethod {
        @Inject
        public <T> void gm(T t) {
        }
    }
}
