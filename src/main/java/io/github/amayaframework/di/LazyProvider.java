package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LazyProvider implements ArtifactProvider {
    private final Repository repository;
    private final Map<Artifact, Function0<Function0<Object>>> body;

    public LazyProvider(Repository repository) {
        this.repository = Objects.requireNonNull(repository);
        this.body = new HashMap<>();
    }

    public void add(Artifact artifact, Function0<Function0<Object>> provider) {
        body.put(artifact, provider);
    }

    public boolean contains(Artifact artifact) {
        return body.containsKey(artifact);
    }

    public boolean remove(Artifact artifact) {
        return body.remove(artifact) != null;
    }

    public void finish() {
        for (var entry : body.entrySet()) {
            var artifact = entry.getKey();
            if (repository.contains(artifact)) {
                continue;
            }
            var supplier = Exceptions.suppress(entry.getValue());
            repository.add(artifact, supplier);
        }
        body.clear();
    }

    @Override
    public Function0<Object> apply(Artifact artifact) {
        var ret = repository.get(artifact);
        if (ret != null) {
            return ret;
        }
        var provided = body.get(artifact);
        if (provided == null) {
            return null;
        }
        var function = Exceptions.suppress(provided);
        repository.add(artifact, function);
        // It is important to request the artifact again from the repository so that it can apply the wrapper.
        return repository.get(artifact);
    }
}
