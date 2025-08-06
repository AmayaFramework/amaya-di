/**
 * Core module for the Amaya DI framework.
 * <p>
 * This module provides the basic dependency injection (DI) infrastructure,
 * including types for {@code ObjectFactory}, {@code TypeRepository},
 * and scoped resolution logic.
 * <p>
 * Dependencies:
 * <ul>
 *     <li>{@code com.github.romanqed.jfunc} — functional utilities used for factory and provider abstractions</li>
 *     <li>{@code com.github.romanqed.jtype} — lightweight type system utilities for working with generic types</li>
 * </ul>
 * <p>
 * Exported package:
 * <ul>
 *     <li>{@code io.github.amayaframework.di.core}</li>
 * </ul>
 */
module amayaframework.di.core {
    // Imports
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jtype;
    // Exports
    exports io.github.amayaframework.di.core;
}
