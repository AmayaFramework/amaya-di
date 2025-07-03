//package io.github.amayaframework.di.reflect;
//
//import com.github.romanqed.jtype.Types;
//import io.github.amayaframework.di.scheme.ClassScheme;
//import io.github.amayaframework.di.scheme.ConstructorScheme;
//import io.github.amayaframework.di.scheme.FieldScheme;
//import io.github.amayaframework.di.scheme.MethodScheme;
//import io.github.amayaframework.di.stub.StubFactory;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.Type;
//import java.util.Set;
//
//public class StubTest extends Assertions {
//    public void testStubFactory(StubFactory factory) throws Throwable {
//        var a1 = Types.of(String.class, new Object[]{"1"});
//        var a2 = Types.of(String.class, new Object[]{"2"});
//        var a3 = Types.of(String.class, new Object[]{"3"});
//        var field = new FieldScheme(TestClass.class.getField("field"), a1);
//        var ctor = new ConstructorScheme(
//                TestClass.class.getConstructor(String.class),
//                Set.of(a2),
//                new Type[]{a2}
//        );
//        var method = new MethodScheme(
//                TestClass.class.getMethod("mt", String.class),
//                Set.of(a3),
//                new Type[]{a3}
//        );
//        var scheme = new ClassScheme(
//                TestClass.class,
//                ctor,
//                Set.of(field),
//                Set.of(method)
//        );
//        var stub = factory.create(scheme, a -> {
//            if (a1.equals(a)) {
//                return () -> "1";
//            }
//            if (a2.equals(a)) {
//                return () -> "2";
//            }
//            if (a3.equals(a)) {
//                return () -> "3";
//            }
//            throw new IllegalArgumentException("Unknown type");
//        });
//        var object = (TestClass) stub.invoke();
//        assertAll(
//                () -> assertEquals("1", object.field),
//                () -> assertEquals("2", object.s),
//                () -> assertEquals("3", object.m)
//        );
//    }
//
//    @Test
//    public void testBytecodeStub() throws Throwable {
//        testStubFactory(new ReflectStubFactory());
//    }
//
//    public static final class TestClass {
//        public String field;
//        public String s;
//        public String m;
//
//        public TestClass(String s) {
//            this.s = s;
//        }
//
//        public void mt(String m) {
//            this.m = m;
//        }
//    }
//}
