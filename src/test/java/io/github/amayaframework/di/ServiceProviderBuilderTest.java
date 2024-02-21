package io.github.amayaframework.di;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceProviderBuilderTest extends Assertions {
    private static final ServiceProviderBuilder CHECKED_BUILDER = Builders.createChecked();
    private static final ManualProviderBuilder MANUAL_BUILDER = Builders.createManual();

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

    @Test
    public void testManualCorrect() {
        testCorrect(MANUAL_BUILDER);
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

    @Test
    public void testManualMissingDependency() {
        testMissingDependency(MANUAL_BUILDER);
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

    @Test
    public void testManualCycle() {
        testCycle(MANUAL_BUILDER);
    }

    @Test
    public void testManual() {
        var provider = MANUAL_BUILDER
                .addTransient(Service.class)
                .addTransient(ManualApp.class)
                .addManual(Service2.class, sub -> {
                    var s = sub.apply(Service.class);
                    return () -> new Service2(s.invoke());
                })
                .build();
        assertNotNull(provider.instantiate(ManualApp.class));
    }

    public static final class Service2 {
        public Service2(Service s) {
        }
    }

    public static final class ManualApp {
        public ManualApp(Service s, Service2 s2) {
        }
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
