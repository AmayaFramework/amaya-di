package io.github.amayaframework;

import io.github.amayaframework.di.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class ManualInjectTest extends Assertions {
    private static DI DI;

    @BeforeAll
    public static void prepare() {
        DI = new DIBuilder()
                .setProvider(Values.class)
                .setAutoTransform(false)
                .build();
    }

    @Test
    public void testPrototype() throws Exception {
        PrototypeTest obj = DI.prepare(PrototypeTest.class).call();
        assertAll(
                () -> assertInstanceOf(SourceClass.class, obj.field1),
                () -> assertInstanceOf(SourceClass.class, obj.field2)
        );
    }

    @Test
    public void testSingleton() throws Exception {
        Callable<SingletonTest> constructor = DI.prepare(SingletonTest.class);
        SingletonTest first = constructor.call();
        SingletonTest second = constructor.call();
        assertAll(
                () -> assertInstanceOf(SourceClass.class, first.field1),
                () -> assertInstanceOf(SourceClass.class, second.field1),
                () -> assertEquals(first.field1.value, second.field1.value)
        );
    }

    @Test
    public void testConcurrencySingleton() throws InterruptedException {
        Callable<SingletonTest> constructor = DI.prepare(SingletonTest.class);
        AtomicReference<SingletonTest> first = new AtomicReference<>();
        AtomicReference<SingletonTest> second = new AtomicReference<>();
        Thread thread1 = new Thread(() -> {
            try {
                first.set(constructor.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        Thread thread2 = new Thread(() -> {
            try {
                second.set(constructor.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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
    public void testValue() throws Exception {
        ValueTest valueTest = DI.prepare(ValueTest.class).call();
        assertAll(
                () -> assertEquals(1, valueTest.field1),
                () -> assertEquals(2, valueTest.field2),
                () -> assertEquals(3, valueTest.field3),
                () -> assertEquals(4, valueTest.field4),
                () -> assertNull(valueTest.field5)
        );
    }

    @Test
    public void testInner() throws Exception {
        DI.transform();
        PrototypeTest obj = DI.prepare(PrototypeTest.class).call();
        assertAll(
                () -> assertInstanceOf(AutoInjectTest.SourceChildrenClass.class, obj.field1),
                () -> assertInstanceOf(AutoInjectTest.SourceChildrenClass.class, obj.field2),
                () -> assertInstanceOf(SourceClass.InnerFinal.class,
                        ((AutoInjectTest.SourceChildrenClass) obj.field1).field),
                () -> assertInstanceOf(SourceClass.InnerFinal.class,
                        ((AutoInjectTest.SourceChildrenClass) obj.field2).field)
        );
    }

    @Inject
    public static class PrototypeTest {
        private final SourceClass field1;
        private SourceClass field2;

        @Prototype
        public PrototypeTest(SourceClass source) {
            this.field1 = source;
        }

        @Prototype
        public void setField2(SourceClass source) {
            this.field2 = source;
        }
    }

    @Inject
    public static class SingletonTest {
        private final SourceClass field1;

        @Singleton
        public SingletonTest(SourceClass source) {
            this.field1 = source;
        }
    }

    @Inject
    public static class ValueTest {
        private Integer field1;
        private Integer field2;
        private Integer field3;
        private Integer field4;
        private Integer field5;

        @Value("FIELD1")
        public void setField1(Integer field1) {
            this.field1 = field1;
        }

        @Value("FIELD2")
        public void setField2(Integer field2) {
            this.field2 = field2;
        }

        @Value("FIELD3")
        public void setField3(Integer field3) {
            this.field3 = field3;
        }

        @Value("FIELD4")
        public void setField4(Integer field4) {
            this.field4 = field4;
        }

        @Value("FIELD5")
        public void setField5(Integer field5) {
            this.field5 = field5;
        }
    }
}
