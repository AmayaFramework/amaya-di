package io.github.amayaframework;

import io.github.amayaframework.di.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

public class AutoInjectTest extends Assertions {

    @BeforeAll
    public static void prepare() {
        DI DI = new DIBuilder()
                .setProvider(Values.class)
                .build();
    }

    @Test
    public void testPrototype() {
        PrototypeTest obj = new PrototypeTest();
        assertAll(
                () -> assertInstanceOf(SourceClass.class, obj.field1),
                () -> assertInstanceOf(SourceClass.class, obj.field2)
        );
    }

    @Test
    public void testSingleton() {
        SingletonTest first = new SingletonTest();
        SingletonTest second = new SingletonTest();
        assertAll(
                () -> assertInstanceOf(SourceClass.class, first.field1),
                () -> assertInstanceOf(SourceClass.class, second.field1),
                () -> assertEquals(first.field1.value, second.field1.value)
        );
    }

    @Test
    public void testConcurrencySingleton() throws InterruptedException {
        AtomicReference<SingletonTest> first = new AtomicReference<>();
        AtomicReference<SingletonTest> second = new AtomicReference<>();
        Thread thread1 = new Thread(() -> first.set(new SingletonTest()));
        thread1.start();
        Thread thread2 = new Thread(() -> second.set(new SingletonTest()));
        thread2.start();
        thread1.join();
        thread2.join();
        assertAll(
                () -> assertInstanceOf(SourceClass.class, first.get().field1),
                () -> assertInstanceOf(SourceClass.class, second.get().field1),
                () -> assertEquals(first.get().field1.value, second.get().field1.value)
        );
    }

    @Test
    public void testValue() {
        ValueTest valueTest = new ValueTest();
        assertAll(
                () -> assertEquals(1, valueTest.field1),
                () -> assertEquals(2, valueTest.field2),
                () -> assertEquals(3, valueTest.field3),
                () -> assertEquals(4, valueTest.field4),
                () -> assertNull(valueTest.field5)
        );
    }

    @Test
    public void testInner() {
        PrototypeTest obj = new PrototypeTest();
        assertAll(
                () -> assertInstanceOf(SourceChildrenClass.class, obj.field1),
                () -> assertInstanceOf(SourceChildrenClass.class, obj.field2),
                () -> assertInstanceOf(SourceClass.InnerFinal.class, ((SourceChildrenClass) obj.field1).field),
                () -> assertInstanceOf(SourceClass.InnerFinal.class, ((SourceChildrenClass) obj.field2).field)
        );
    }

    @Test
    public void testManyConstructors() {
        ManyConstructorsTest obj1 = new ManyConstructorsTest();
        ManyConstructorsTest obj2 = new ManyConstructorsTest(1);
        ManyConstructorsTest obj3 = new ManyConstructorsTest(1, 1);
        assertAll(
                () -> assertEquals(0, obj1.field),
                () -> assertEquals(1, obj2.field),
                () -> assertEquals(2, obj3.field),
                () -> assertNotNull(obj1.counter),
                () -> assertNotNull(obj2.counter),
                () -> assertNotNull(obj3.counter),
                () -> assertEquals(3, InitCounter.initCount)
        );
    }

    @Inject
    @AutoTransform
    public static class ManyConstructorsTest {
        @Prototype
        InitCounter counter;

        int field;

        public ManyConstructorsTest() {
            this.field = 0;
        }

        public ManyConstructorsTest(int field) {
            this.field = field;
        }

        public ManyConstructorsTest(int a, int b) {
            this(a + b);
        }
    }

    public static class InitCounter {
        static int initCount = 0;

        public InitCounter() {
            ++initCount;
        }
    }

    @Inject
    @AutoTransform
    public static class PrototypeTest {
        @Prototype
        private SourceClass field1;
        @Prototype
        private SourceClass field2;
    }

    @Inject
    @AutoTransform
    public static class SingletonTest {
        @Singleton
        private SourceClass field1;
    }

    @Inject
    @AutoTransform
    public static class ValueTest {
        @Value("FIELD1")
        private Integer field1;
        @Value("FIELD2")
        private Integer field2;
        @Value("FIELD3")
        private Integer field3;
        @Value("FIELD4")
        private Integer field4;
        @Value("FIELD5")
        private Integer field5;
    }

    @Source
    @Inject
    @AutoTransform
    public static class SourceChildrenClass extends SourceClass {
        @Prototype
        public InnerFinal field;
    }
}
