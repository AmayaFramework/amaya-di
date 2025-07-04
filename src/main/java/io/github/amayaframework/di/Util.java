package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

final class Util {
    private static final Pattern QUALIFIER_REGEX = Pattern.compile("(?:[^.<>$,?]+\\.)+");

    private Util() {
    }

    static String getName(Type type) {
        if (type == null) {
            return "null";
        }
        if (type.getClass() == Class.class) {
            return ((Class<?>) type).getSimpleName();
        }
        var ret = type.getTypeName();
        if (ret.indexOf('.') < 0) {
            return ret;
        }
        var matcher = QUALIFIER_REGEX.matcher(ret);
        return matcher.replaceAll("");
    }

    static String getNames(List<Type> types) {
        var iterator = types.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        var builder = new StringBuilder(getName(iterator.next()));
        while (iterator.hasNext()) {
            builder.append(", ").append(getName(iterator.next()));
        }
        return builder.toString();
    }
}
