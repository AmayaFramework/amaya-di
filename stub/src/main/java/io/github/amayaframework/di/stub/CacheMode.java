package io.github.amayaframework.di.stub;

/**
 * Specifies the caching strategy to use when generating {@link io.github.amayaframework.di.core.ObjectFactory}
 * instances via a {@link StubFactory}.
 * <p>
 * The selected mode controls how the generated factory handles sub-factory reuse,
 * memoization of resolved dependencies, and fallback to the underlying dependency source.
 */
public enum CacheMode {

    /**
     * Full caching mode.
     * <p>
     * The generated {@link CachedObjectFactory} resolves dependencies
     * strictly from its internal cache of sub-factories. If a requested dependency
     * is not present in the cache, resolution fails immediately.
     */
    FULL,

    /**
     * Partial caching mode.
     * <p>
     * The factory first attempts to resolve dependencies from its internal cache.
     * If a dependency is not found, it falls back to resolving it from the underlying
     * source and caches the result.
     */
    PARTIAL,

    /**
     * No caching mode.
     * <p>
     * The factory does not cache any sub-factories. Each dependency resolution
     * results in object factory lookup.
     */
    NONE
}
