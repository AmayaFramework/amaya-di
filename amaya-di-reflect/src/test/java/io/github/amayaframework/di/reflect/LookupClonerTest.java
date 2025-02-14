package io.github.amayaframework.di.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LookupClonerTest {
    private static final ReflectCloner CLONER = new LookupReflectCloner();
    private static final Class<Base> BASE = Base.class;
    private static final Class<Actual> ACTUAL = Actual.class;

    // Constructors
    @Test
    public void testClonePublicConstructor() throws Exception {
        var bc = BASE.getConstructor();
        var ac = ACTUAL.getConstructor();
        assertEquals(bc, CLONER.clone(bc));
        assertEquals(ac, CLONER.clone(ac));
    }

    @Test
    public void testCloneProtectedConstructor() throws Exception {
        var bc = BASE.getDeclaredConstructor(int.class, int.class);
        var ac = ACTUAL.getDeclaredConstructor(int.class, int.class);
        assertEquals(bc, CLONER.clone(bc));
        assertEquals(ac, CLONER.clone(ac));
    }

    @Test
    public void testClonePrivateConstructor() throws Exception {
        var bc = BASE.getDeclaredConstructor(int.class);
        var ac = ACTUAL.getDeclaredConstructor(int.class);
        assertEquals(bc, CLONER.clone(bc));
        assertEquals(ac, CLONER.clone(ac));
    }

    // Methods
    @Test
    public void testClonePublicMethod() throws Exception {
        var bm = BASE.getMethod("publicBaseMethod");
        var am = ACTUAL.getMethod("publicMethod");
        var abm = ACTUAL.getMethod("publicBaseMethod");
        assertEquals(bm, CLONER.clone(bm));
        assertEquals(am, CLONER.clone(am));
        assertEquals(abm, CLONER.clone(abm));
    }

    @Test
    public void testClonePublicStaticMethod() throws Exception {
        var bsm = BASE.getMethod("publicStaticBaseMethod");
        var asm = ACTUAL.getMethod("publicStaticMethod");
        var absm = ACTUAL.getMethod("publicStaticBaseMethod");
        assertEquals(bsm, CLONER.clone(bsm));
        assertEquals(asm, CLONER.clone(asm));
        assertEquals(absm, CLONER.clone(absm));
    }

    @Test
    public void testCloneProtectedMethod() throws Exception {
        var bm = BASE.getDeclaredMethod("protectedBaseMethod");
        var am = ACTUAL.getDeclaredMethod("protectedMethod");
        assertEquals(bm, CLONER.clone(bm));
        assertEquals(am, CLONER.clone(am));
    }

    @Test
    public void testCloneProtectedStaticMethod() throws Exception {
        var bsm = BASE.getDeclaredMethod("protectedStaticBaseMethod");
        var asm = ACTUAL.getDeclaredMethod("protectedStaticMethod");
        assertEquals(bsm, CLONER.clone(bsm));
        assertEquals(asm, CLONER.clone(asm));
    }

    @Test
    public void testClonePrivateMethod() throws Exception {
        var bm = BASE.getDeclaredMethod("privateBaseMethod");
        var am = ACTUAL.getDeclaredMethod("privateMethod");
        assertEquals(bm, CLONER.clone(bm));
        assertEquals(am, CLONER.clone(am));
    }

    @Test
    public void testClonePrivateStaticMethod() throws Exception {
        var bsm = BASE.getDeclaredMethod("privateStaticBaseMethod");
        var asm = ACTUAL.getDeclaredMethod("privateStaticMethod");
        assertEquals(bsm, CLONER.clone(bsm));
        assertEquals(asm, CLONER.clone(asm));
    }

    // Fields
    @Test
    public void testClonePublicField() throws Exception {
        var bf = BASE.getField("publicBaseField");
        var af = ACTUAL.getField("publicField");
        var abf = ACTUAL.getField("publicBaseField");
        assertEquals(bf, CLONER.clone(bf));
        assertEquals(af, CLONER.clone(af));
        assertEquals(abf, CLONER.clone(abf));
    }

    @Test
    public void testClonePublicStaticField() throws Exception {
        var bsf = BASE.getField("publicStaticBaseField");
        var asf = ACTUAL.getField("publicStaticField");
        var absf = ACTUAL.getField("publicStaticBaseField");
        assertEquals(bsf, CLONER.clone(bsf));
        assertEquals(asf, CLONER.clone(asf));
        assertEquals(absf, CLONER.clone(absf));
    }

    @Test
    public void testCloneProtectedField() throws Exception {
        var bf = BASE.getDeclaredField("protectedBaseField");
        var af = ACTUAL.getDeclaredField("protectedField");
        assertEquals(bf, CLONER.clone(bf));
        assertEquals(af, CLONER.clone(af));
    }

    @Test
    public void testCloneProtectedStaticField() throws Exception {
        var bsf = BASE.getDeclaredField("protectedStaticBaseField");
        var asf = ACTUAL.getDeclaredField("protectedStaticField");
        assertEquals(bsf, CLONER.clone(bsf));
        assertEquals(asf, CLONER.clone(asf));
    }

    @Test
    public void testClonePrivateField() throws Exception {
        var bf = BASE.getDeclaredField("privateBaseField");
        var af = ACTUAL.getDeclaredField("privateField");
        assertEquals(bf, CLONER.clone(bf));
        assertEquals(af, CLONER.clone(af));
    }

    @Test
    public void testClonePrivateStaticField() throws Exception {
        var bsf = BASE.getDeclaredField("privateStaticBaseField");
        var asf = ACTUAL.getDeclaredField("privateStaticField");
        assertEquals(bsf, CLONER.clone(bsf));
        assertEquals(asf, CLONER.clone(asf));
    }

    public static class Actual extends Base {
        public static Object publicStaticField;
        protected static Object protectedStaticField;
        private static Object privateStaticField;
        public Object publicField;
        protected Object protectedField;
        private Object privateField;

        private Actual(int i) {
        }

        protected Actual(int i, int i2) {
        }

        public Actual() {
        }

        private static void privateStaticMethod() {
        }

        protected static void protectedStaticMethod() {
        }

        public static void publicStaticMethod() {
        }

        private void privateMethod() {
        }

        protected void protectedMethod() {
        }

        public void publicMethod() {
        }
    }

    public static class Base {
        public static Object publicStaticBaseField;
        protected static Object protectedStaticBaseField;
        private static Object privateStaticBaseField;
        public Object publicBaseField;
        protected Object protectedBaseField;
        private Object privateBaseField;

        private Base(int i) {
        }

        protected Base(int i, int i2) {
        }

        public Base() {
        }

        private static void privateStaticBaseMethod() {
        }

        protected static void protectedStaticBaseMethod() {
        }

        public static void publicStaticBaseMethod() {
        }

        private void privateBaseMethod() {
        }

        protected void protectedBaseMethod() {
        }

        public void publicBaseMethod() {
        }
    }
}
