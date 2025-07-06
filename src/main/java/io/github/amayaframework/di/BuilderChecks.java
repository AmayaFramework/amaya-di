package io.github.amayaframework.di;

public final class BuilderChecks {
    public static final int NO_CHECKS = 0;
    public static final int VALIDATE_MISSING_TYPES = 0b01;
    public static final int VALIDATE_CYCLES = 0b10;
    public static final int VALIDATE_ALL = VALIDATE_MISSING_TYPES | VALIDATE_CYCLES;

    private BuilderChecks() {
    }

    public static boolean checkEnabled(int checks, int check) {
        return (checks & check) != 0;
    }
}
