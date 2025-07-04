package io.github.amayaframework.di.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public final class ScopedRepositoryTest {

    @Test
    public void testEmptyCurrent() throws Throwable {
        var current = of();
        var parent = of(String.class, Integer.class, Character.class);
        var scoped = new ScopedTypeRepository(current, parent);
        // get
        assertNotNull(scoped.get(String.class));
        assertNotNull(scoped.get(Integer.class));
        assertNotNull(scoped.get(Character.class));
        // canProvide
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertTrue(scoped.canProvide(Character.class));
        // set and remove
        scoped.put(String.class, v -> "hi");
        scoped.put(Integer.class, v -> 5);
        scoped.put(Character.class, v -> 'h');
        assertEquals("hi", scoped.get(String.class).create(null));
        assertEquals(5, scoped.get(Integer.class).create(null));
        assertEquals('h', scoped.get(Character.class).create(null));
        scoped.remove(String.class);
        scoped.remove(Integer.class);
        scoped.remove(Character.class);
        assertNull(scoped.get(String.class).create(null));
        assertNull(scoped.get(Integer.class).create(null));
        assertNull(scoped.get(Character.class).create(null));
        // For each loop
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachLoop(scoped)));
        // forEach func
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachFunc(scoped)));
        // forEach bi func
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachBiFunc(scoped)));
        // iterator
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofIterator(scoped, 3)));
    }

    @Test
    public void testEmptyParent() {
        var current = of(String.class, Integer.class, Character.class);
        var parent = of();
        var scoped = new ScopedTypeRepository(current, parent);
        // get
        assertNotNull(scoped.get(String.class));
        assertNotNull(scoped.get(Integer.class));
        assertNotNull(scoped.get(Character.class));
        // canProvide
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertTrue(scoped.canProvide(Character.class));
        // remove
        scoped.remove(String.class);
        scoped.remove(Integer.class);
        scoped.remove(Character.class);
        assertNull(scoped.get(String.class));
        assertNull(scoped.get(Integer.class));
        assertNull(scoped.get(Character.class));
        // For each loop
        assertTrue(ofForEachLoop(scoped).isEmpty());
        // forEach func
        assertTrue(ofForEachFunc(scoped).isEmpty());
        // forEach bi func
        assertTrue(ofForEachBiFunc(scoped).isEmpty());
        // iterator
        var it = scoped.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void testCurrentExtendsParent() throws Throwable {
        var current = of(Map.of(Character.class, 'c'));
        var parent = of(Map.of(String.class, "parent", Integer.class, 5));
        var scoped = new ScopedTypeRepository(current, parent);
        // get
        assertEquals('c', scoped.get(Character.class).create(null));
        assertEquals("parent", scoped.get(String.class).create(null));
        assertEquals(5, scoped.get(Integer.class).create(null));
        // canProvide
        assertTrue(scoped.canProvide(Character.class));
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        // remove
        scoped.remove(String.class);
        scoped.remove(Integer.class);
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        // For each loop
        assertTrue(compareUnordered(List.of(Character.class, String.class, Integer.class), ofForEachLoop(scoped)));
        // forEach func
        assertTrue(compareUnordered(List.of(Character.class, String.class, Integer.class), ofForEachFunc(scoped)));
        // forEach bi func
        assertTrue(compareUnordered(List.of(Character.class, String.class, Integer.class), ofForEachBiFunc(scoped)));
        // iterator
        assertTrue(compareUnordered(List.of(Character.class, String.class, Integer.class), ofIterator(scoped, 3)));
    }

    @Test
    public void testCurrentOverridesParent() throws Throwable {
        var current = of(Map.of(String.class, "current", Integer.class, 10));
        var parent = of(Map.of(String.class, "parent", Integer.class, 5));
        var scoped = new ScopedTypeRepository(current, parent);
        // get
        assertEquals("current", scoped.get(String.class).create(null));
        assertEquals(10, scoped.get(Integer.class).create(null));
        // canProvide
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        // remove
        var s = scoped.remove(String.class);
        var i = scoped.remove(Integer.class);
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertEquals("parent", scoped.get(String.class).create(null));
        assertEquals(5, scoped.get(Integer.class).create(null));
        // set
        scoped.put(String.class, s);
        scoped.put(Integer.class, i);
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertEquals("current", scoped.get(String.class).create(null));
        assertEquals(10, scoped.get(Integer.class).create(null));
        // For each loop
        assertTrue(compareUnordered(List.of(String.class, Integer.class), ofForEachLoop(scoped)));
        // forEach func
        assertTrue(compareUnordered(List.of(String.class, Integer.class), ofForEachFunc(scoped)));
        // forEach bi func
        assertTrue(compareUnordered(List.of(String.class, Integer.class), ofForEachBiFunc(scoped)));
        // iterator
        assertTrue(compareUnordered(List.of(String.class, Integer.class), ofIterator(scoped, 2)));
    }

    @Test
    public void testCurrentExtendsAndOverridesParent() throws Throwable {
        var current = of(Map.of(String.class, "current", Integer.class, 10, Character.class, 'c'));
        var parent = of(Map.of(String.class, "parent", Integer.class, 5));
        var scoped = new ScopedTypeRepository(current, parent);
        // get
        assertEquals("current", scoped.get(String.class).create(null));
        assertEquals(10, scoped.get(Integer.class).create(null));
        assertEquals('c', scoped.get(Character.class).create(null));
        // canProvide
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertTrue(scoped.canProvide(Character.class));
        // remove
        var s = scoped.remove(String.class);
        var i = scoped.remove(Integer.class);
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertTrue(scoped.canProvide(Character.class));
        assertEquals("parent", scoped.get(String.class).create(null));
        assertEquals(5, scoped.get(Integer.class).create(null));
        assertEquals('c', scoped.get(Character.class).create(null));
        // set
        scoped.put(String.class, s);
        scoped.put(Integer.class, i);
        assertTrue(scoped.canProvide(String.class));
        assertTrue(scoped.canProvide(Integer.class));
        assertTrue(scoped.canProvide(Character.class));
        assertEquals("current", scoped.get(String.class).create(null));
        assertEquals(10, scoped.get(Integer.class).create(null));
        assertEquals('c', scoped.get(Character.class).create(null));
        // For each loop
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachLoop(scoped)));
        // forEach func
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachFunc(scoped)));
        // forEach bi func
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofForEachBiFunc(scoped)));
        // iterator
        assertTrue(compareUnordered(List.of(String.class, Integer.class, Character.class), ofIterator(scoped, 3)));
    }

    static boolean compareUnordered(List<?> left, List<?> right) {
        var map = new HashMap<Object, Integer>();
        var lambda = (Consumer<Object>) o -> {
            map.putIfAbsent(o, 0);
            map.put(o, map.get(o) + 1);
        };
        left.forEach(lambda);
        right.forEach(lambda);
        for (var cnt : map.values()) {
            if (cnt != 2) {
                return false;
            }
        }
        return true;
    }

    static List<Type> ofIterator(TypeRepository repository, int count) {
        var ret = new LinkedList<Type>();
        var iterator = repository.iterator();
        for (var i = 0; i < count; ++i) {
            ret.add(iterator.next());
        }
        return ret;
    }

    static List<Type> ofForEachBiFunc(TypeRepository repository) {
        var ret = new LinkedList<Type>();
        repository.forEach((type, factory) -> ret.add(type));
        return ret;
    }

    static List<Type> ofForEachFunc(TypeRepository repository) {
        var ret = new LinkedList<Type>();
        repository.forEach(type -> ret.add(type));
        return ret;
    }

    static List<Type> ofForEachLoop(TypeRepository repository) {
        var ret = new LinkedList<Type>();
        for (var type : repository) {
            ret.add(type);
        }
        return ret;
    }

    static TypeRepository of(Map<Type, Object> vals) {
        var ret = new HashTypeRepository();
        for (var entry : vals.entrySet()) {
            var val = entry.getValue();
            ret.put(entry.getKey(), v -> val);
        }
        return ret;
    }

    static TypeRepository of(Type... types) {
        var ret = new HashTypeRepository();
        for (var type : types) {
            ret.put(type, v -> null);
        }
        return ret;
    }
}
