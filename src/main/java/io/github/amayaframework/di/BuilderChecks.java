package io.github.amayaframework.di;

/**
 * Defines constants for validation checks that can be performed during the build process of the service provider.
 * These flags allow enabling or disabling specific checks such as verifying missing types
 * or detecting dependency cycles.
 * Used to control and customize validation behavior to catch configuration errors early.
 */
public final class BuilderChecks {
    /**
     * No validation checks will be performed.
     */
    public static final int NO_CHECKS = 0;

    /**
     * Enables validation to detect missing types in the dependency graph,
     * ensuring all required dependencies are registered.
     */
    public static final int VALIDATE_MISSING_TYPES = 0b01;

    /**
     * Enables validation to detect cycles in the dependency graph,
     * which can cause infinite loops or stack overflows at runtime.
     */
    public static final int VALIDATE_CYCLES = 0b10;

    /**
     * Enables all available validation checks: missing types and cycles.
     */
    public static final int VALIDATE_ALL = VALIDATE_MISSING_TYPES | VALIDATE_CYCLES;

    private BuilderChecks() {
    }

    /**
     * Checks if a specific validation flag is enabled within the provided checks bitmask.
     *
     * @param checks the bitmask of currently enabled validation checks
     * @param check  the specific validation flag to test
     * @return {@code true} if the specified validation check is enabled; {@code false} otherwise
     */
    public static boolean checkEnabled(int checks, int check) {
        return (checks & check) != 0;
    }
}
