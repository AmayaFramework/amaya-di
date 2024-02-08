package io.github.amayaframework.di;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceProviderBuilderTest extends Assertions {
    private static final ServiceProviderBuilder CHECKED_BUILDER = CheckedProviderBuilder.create();

    public void testCorrect(ServiceProviderBuilder builder) {
        var provider = builder
                .addTransient(Service.class)
                .addTransient(App.class)
                .build();
        var app = provider.instantiate(App.class);
        assertAll(
                () -> assertNotNull(app),
                () -> assertNotNull(app.service)
        );
    }

    @Test
    public void testCheckedCorrect() {
        testCorrect(CHECKED_BUILDER);
    }

    public void testMissingDependency(ServiceProviderBuilder builder) {
        assertThrows(ArtifactNotFoundException.class, () -> builder
                .addTransient(App.class)
                .build());
    }

    @Test
    public void testCheckedMissingDependency() {
        testMissingDependency(CHECKED_BUILDER);
    }

    public void testCycle(ServiceProviderBuilder builder) {
        assertThrows(CycleFoundException.class, () -> builder
                .addTransient(CycleService.class)
                .addTransient(CycleApp.class)
                .build());
    }

    @Test
    public void testCheckedCycle() {
        testCycle(CHECKED_BUILDER);
    }

    public static final class Service {
    }

    public static final class App {
        final Service service;

        public App(Service service) {
            this.service = service;
        }
    }

    public static final class CycleApp {
        public CycleApp(CycleService service) {
        }
    }

    public static final class CycleService {
        public CycleService(CycleApp app) {
        }
    }
}
