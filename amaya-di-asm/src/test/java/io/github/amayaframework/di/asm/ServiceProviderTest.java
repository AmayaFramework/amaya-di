package io.github.amayaframework.di.asm;

import com.github.romanqed.jtype.Types;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.ServiceProvider;
import io.github.amayaframework.di.ServiceProviderBuilder;
import io.github.amayaframework.di.stub.StubFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceProviderTest extends Assertions {
    private static final StubFactory BYTECODE_FACTORY = new BytecodeStubFactory();

    public ServiceProvider create(ServiceProviderBuilder builder) {
        return builder
                .addTransient(Service1.class)
                .addService(
                        Types.of(Service2.class, String.class),
                        () -> new Service2<>("2")
                )
                .addTransient(App.class)
                .addSingleton(Service3.class)
                .addInstance(String.class, "SomeString")
                .build();
    }

    public void testProvider(ServiceProviderBuilder builder) {
        var provider = create(builder);
        var app = provider.get(App.class);
        assertAll(
                () -> assertNotNull(app),
                () -> assertNotNull(app.s1),
                () -> assertNotNull(app.s2),
                () -> assertNotNull(app.s2.value),
                () -> assertNotNull(app.s3),
                () -> assertNotNull(app.s31),
                () -> assertEquals(app.s3, app.s31),
                () -> assertEquals("2", app.s2.value),
                () -> assertEquals("SomeString", app.str)
        );
    }

    @Test
    public void testCheckedProvider() {
        testProvider(ProviderBuilders.createChecked(BYTECODE_FACTORY));
    }

    @Test
    public void testManualProvider() {
        testProvider(ProviderBuilders.createManual(BYTECODE_FACTORY));
    }

    public static final class Service1 {
    }

    public static final class Service2<T> {
        final T value;

        public Service2(T value) {
            this.value = value;
        }
    }

    public static final class Service3 {
    }

    public static final class App {
        final Service1 s1;
        final Service2<String> s2;
        final Service3 s3;
        final Service3 s31;
        final String str;

        public App(Service1 s1, Service2<String> s2, Service3 s3, Service3 s31, String str) {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.s31 = s31;
            this.str = str;
        }
    }
}
