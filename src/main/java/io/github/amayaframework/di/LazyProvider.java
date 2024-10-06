package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.stub.TypeProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A lazy implementation of the {@link TypeProvider}, which allows you to organize
 * the chain of building type implementations.
 */
public class LazyProvider implements TypeProvider {
    private final ServiceRepository repository;
    private final Map<Type, Function0<Function0<Object>>> body;

    /**
     * Constructs {@link LazyProvider} with the specified repository.
     *
     * @param repository the specified repository
     */
    public LazyProvider(ServiceRepository repository) {
        this.repository = Objects.requireNonNull(repository);
        this.body = new HashMap<>();
    }

    /**
     * Adds the deferred task of creating a type implementation.
     *
     * @param type the specified type
     * @param task the specified task
     */
    public void add(Type type, Function0<Function0<Object>> task) {
        body.put(type, task);
    }

    /**
     * Checks whether the provider contains a deferred task for the specified type.
     *
     * @param type the specified type
     * @return true, if contains, false otherwise
     */
    public boolean contains(Type type) {
        return body.containsKey(type);
    }

    /**
     * Removes the deferred task associated with the specified type.
     *
     * @param type the specified type
     * @return true if the task was removed, false otherwise
     */
    public boolean remove(Type type) {
        return body.remove(type) != null;
    }

    /**
     * Starts a chain of deferred tasks, committing changes to the repository.
     * All called tasks are removed.
     */
    public void commit() {
        for (var entry : body.entrySet()) {
            var type = entry.getKey();
            if (repository.contains(type)) {
                continue;
            }
            var supplier = Exceptions.suppress(entry.getValue());
            repository.add(type, supplier);
        }
        body.clear();
    }

    @Override
    public Function0<Object> apply(Type type) {
        var ret = repository.get(type);
        if (ret != null) {
            return ret;
        }
        var provided = body.get(type);
        if (provided == null) {
            return null;
        }
        var function = Exceptions.suppress(provided);
        repository.add(type, function);
        // It is important to request the type again from the repository so that it can apply the wrapper.
        return repository.get(type);
    }
}
