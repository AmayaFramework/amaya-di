package com.github.romanqed.di.examples;

import com.github.romanqed.jeflect.loader.CachedClassLoader;
import com.github.romanqed.jeflect.loader.ClassCache;
import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.AsmStubFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class CachedAsmHelloWorld {

    public static void main(String[] args) {
        var baseLoader = new DefineClassLoader();
        var cachedLoader = new CachedClassLoader(baseLoader, new LocalClassCache());
        var stubFactory = new AsmStubFactory(cachedLoader);
        var provider = ProviderBuilders.createScoped(stubFactory)
                .addSingleton(IGreeter.class, GlobalGreeter.class)
                .addScoped(String.class)
                .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("Scope One");
        var scope2 = provider.createScoped();
        scope2.repository().put("Scope Two");
        System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
    }

    public static final class LocalClassCache implements ClassCache {
        private static final Path CACHE_ROOT = Path.of("cache").toAbsolutePath();

        static {
            if (!Files.isDirectory(CACHE_ROOT)) {
                Exceptions.suppress(() -> Files.createDirectories(CACHE_ROOT));
            }
        }

        @Override
        public byte[] get(String name) {
            var path = CACHE_ROOT.resolve(name + ".class");
            if (!Files.isRegularFile(path)) {
                return null;
            }
            try (var reader = Files.newInputStream(path)){
                return reader.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void set(String name, byte[] buffer) {
            var path = CACHE_ROOT.resolve(name + ".class");
            try (var writer = Files.newOutputStream(
                    path,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
            )) {
                writer.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface IGreeter {
        String sayHello(String name);
    }

    public static final class GlobalGreeter implements IGreeter {

        @Override
        public String sayHello(String name) {
            return "Hello, " + name + "!";
        }
    }

    public static final class ScopedGreeter implements IGreeter {
        private final String scope;

        public ScopedGreeter(String scope) {
            this.scope = scope;
        }

        @Override
        public String sayHello(String name) {
            return "Hello from scope '" + scope + "', " + name + "!";
        }
    }
}
