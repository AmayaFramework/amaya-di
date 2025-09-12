package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class ScopedRepositoryLocalTest {

    @Test
    public void testGetLocalIgnoresParent() throws Throwable {
        var current = new HashTypeRepository();
        current.put(String.class, v -> "local");
        var parent = new HashTypeRepository();
        parent.put(String.class, v -> "parent");

        var repo = new ScopedTypeRepository(current, parent);

        assertEquals("local", repo.getLocal(String.class).create(null));
        assertNull(repo.getLocal(Integer.class));
    }

    @Test
    public void testCanProvideLocal() {
        var current = new HashTypeRepository();
        current.put(String.class, v -> "local");
        var parent = new HashTypeRepository();
        parent.put(Integer.class, v -> 5);

        var repo = new ScopedTypeRepository(current, parent);

        assertTrue(repo.canProvideLocal(String.class));
        assertFalse(repo.canProvideLocal(Integer.class));
    }

    @Test
    public void testForEachLocalIteratesOnlyLocal() {
        var current = new HashTypeRepository();
        current.put(String.class, v -> "local");
        var parent = new HashTypeRepository();
        parent.put(Integer.class, v -> 5);

        var repo = new ScopedTypeRepository(current, parent);

        var sb = new StringBuilder();
        repo.forEachLocal(type -> sb.append(type.getTypeName()));
        assertEquals(String.class.getTypeName(), sb.toString());
    }
}
