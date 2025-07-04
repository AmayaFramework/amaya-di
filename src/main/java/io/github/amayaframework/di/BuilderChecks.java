package io.github.amayaframework.di;

public final class BuilderChecks {
    public static final int VALIDATE_CYCLES = 0b01;
    public static final int VALIDATE_MISSING_TYPES = 0b10;
    public static final int VALIDATE_ALL = VALIDATE_CYCLES | VALIDATE_MISSING_TYPES;
    private BuilderChecks() {
    }

    public static boolean checkEnabled(int checks, int check) {
        return (checks & check) != 0;
    }
}
