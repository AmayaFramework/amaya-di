/**
 * The schema module of the Amaya DI framework.
 * <p>
 * Provides a reflection-based system for generating dependency injection metadata (schemas)
 * for classes, constructors, fields, and methods.
 * <p>
 * Exports:
 * <ul>
 *     <li>{@code io.github.amayaframework.di.schema}</li>
 * </ul>
 * Requires:
 * <ul>
 *     <li>{@code com.github.romanqed.jtype} — for generic type analysis and modeling</li>
 * </ul>
 */
module amayaframework.di.schema {
    // Imports
    requires com.github.romanqed.jtype;
    // Exports
    exports io.github.amayaframework.di.schema;
}
