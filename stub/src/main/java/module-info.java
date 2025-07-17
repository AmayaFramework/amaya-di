/**
 * The stub module of the Amaya DI framework.
 * <p>
 * Provides stub generation tools for runtime dependency injection based on schema metadata.
 * <p>
 * Exports:
 * <ul>
 *     <li>{@code io.github.amayaframework.di.stub}</li>
 * </ul>
 * Requires:
 * <ul>
 *     <li>{@code io.github.amayaframework.di.schema} — contains type and member dependency schemas
 *         built using reflection or custom logic, describing injection structure.</li>
 *     <li>{@code io.github.amayaframework.di.core} — provides core types and interfaces
 *         such as {@code ObjectFactory} and {@code TypeRepository}, used for runtime injection.</li>
 * </ul>
 */
module io.github.amayaframework.di.stub {
    // Imports
    requires io.github.amayaframework.di.schema;
    requires io.github.amayaframework.di.core;
    // Exports
    exports io.github.amayaframework.di.stub;
}
