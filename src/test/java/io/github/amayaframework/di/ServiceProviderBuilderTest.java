package io.github.amayaframework.di;

import io.github.amayaframework.di.stub.StubFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class ServiceProviderBuilderTest extends Assertions {
    private static final StubFactory BYTECODE_FACTORY = (scheme, provider) -> () -> null;
    private static final ServiceProviderBuilder CHECKED_BUILDER = ProviderBuilders.createChecked(BYTECODE_FACTORY);
    private static final ManualProviderBuilder MANUAL_BUILDER = ProviderBuilders.createManual(BYTECODE_FACTORY);

    public void testCorrect(ServiceProviderBuilder builder) {
        var provider = builder
                .addTransient(Service.class)
                .addTransient(App.class)
                .build();
        var app = provider.getRepository().get(App.class);
        assertAll(() -> assertNotNull(app));
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
        assertThrows(TypeNotFoundException.class, () -> builder
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
        assertNotNull(provider.getRepository().get(ManualApp.class));
    }

    public void testMutualExclusion(ServiceProviderBuilder builder) {
        var provider = builder
                .addTransient(App.class)
                .addService(Service.class, () -> null)
                .addTransient(Service.class)
                .build();
        assertNotNull(provider.getRepository().get(App.class));
    }

    @Test
    public void testCheckedMutualExclusion() {
        testMutualExclusion(CHECKED_BUILDER);
    }

    public void testMutualExclusion(ManualProviderBuilder builder) {
        var provider = builder
                .addTransient(App.class)
                .addService(Service.class, () -> null)
                .addManual(Service.class, v -> () -> null)
                .addTransient(Service.class)
                .build();
        assertNotNull(provider.getRepository().get(App.class));
    }

    @Test
    public void testManualMutualExclusion() {
        testMutualExclusion(MANUAL_BUILDER);
    }

    public void testRemoval(ServiceProviderBuilder builder) {
        assertThrows(TypeNotFoundException.class, () -> builder
                .addTransient(App.class)
                .addTransient(Service.class)
                .addService(Service.class, () -> null)
                .removeService(Service.class)
                .build());
    }

    @Test
    public void testCheckedRemoval() {
        testRemoval(CHECKED_BUILDER);
    }

    @Test
    public void testManualRemoval() {
        testRemoval(MANUAL_BUILDER);
    }

    public static final class Service2 {
        public Service2(Service s) {
            Objects.requireNonNull(s);
        }
    }

    public static final class ManualApp {
        public ManualApp(Service s, Service2 s2) {
            Objects.requireNonNull(s);
            Objects.requireNonNull(s2);
        }
    }

    public static final class Service {
    }

    public static final class App {
        final Service service;

        public App(Service service) {
            this.service = Objects.requireNonNull(service);
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
