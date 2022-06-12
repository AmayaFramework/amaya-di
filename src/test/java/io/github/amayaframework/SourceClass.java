package io.github.amayaframework;

import java.util.concurrent.ThreadLocalRandom;

public class SourceClass {
    final int value = ThreadLocalRandom.current().nextInt();

    @Override
    public String toString() {
        String name = "Source";
        return "Source{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    public static final class InnerFinal {
        final int value = ThreadLocalRandom.current().nextInt();
    }
}
