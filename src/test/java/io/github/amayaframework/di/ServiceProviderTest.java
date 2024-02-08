package io.github.amayaframework.di;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceProviderTest extends Assertions {
    public ServiceProvider create(ServiceProviderBuilder builder) {
        return builder
                .addTransient(Service1.class)
                .addService(new Artifact(Service2.class, String.class), () -> new Service2<>("2"))
                .addTransient(App.class)
                .build();
    }

    public void testProvider(ServiceProviderBuilder builder) {
        var provider = create(builder);
        var app = provider.instantiate(App.class);
        assertAll(
                () -> assertNotNull(app),
                () -> assertNotNull(app.s1),
                () -> assertNotNull(app.s2),
                () -> assertNotNull(app.s2.value),
                () -> assertEquals("2", app.s2.value)
        );
    }

    @Test
    public void testCheckedProvider() {
        testProvider(CheckedProviderBuilder.create());
    }

    public static final class Service1 {
    }

    public static final class Service2<T> {
        final T value;

        public Service2(T value) {
            this.value = value;
        }
    }

    public static final class App {
        final Service1 s1;
        final Service2<String> s2;

        public App(Service1 s1, Service2<String> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }
    }
}
