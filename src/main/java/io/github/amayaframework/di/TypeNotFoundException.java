package io.github.amayaframework.di;

import com.github.romanqed.jtype.IllegalTypeException;

import java.lang.reflect.Type;

/**
 * Thrown to indicate that the dependency specialized by the specified type was not found.
 */
public class TypeNotFoundException extends IllegalTypeException {

    /**
     * Constructs an {@link TypeNotFoundException} with the missing type.
     *
     * @param type the missing type
     */
    public TypeNotFoundException(Type type) {
        super("The type " + FormatUtil.getName(type) + " was not found", type);
    }
}
