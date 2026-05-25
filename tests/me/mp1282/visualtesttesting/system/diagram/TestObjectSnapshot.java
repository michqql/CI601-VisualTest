package me.mp1282.visualtesttesting.system.diagram;

import me.mp1282.visualtest.system.diagram.runtime.ObjectSnapshot;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestObjectSnapshot {

    /* Integer round-trip: snapshot -> getObject() returns equal value */
    @Test
    public void testIntegerRoundTrip() {
        ObjectSnapshot snapshot = new ObjectSnapshot(7);
        Assert.assertEquals(7, snapshot.getObject());
    }

    /* String round-trip */
    @Test
    public void testStringRoundTrip() {
        ObjectSnapshot snapshot = new ObjectSnapshot("hello");
        Assert.assertEquals("hello", snapshot.getObject());
    }

    /* getObject() called twice returns the same cached reference */
    @Test
    public void testLazyCaching() {
        ObjectSnapshot snapshot = new ObjectSnapshot(42);
        Object first  = snapshot.getObject();
        Object second = snapshot.getObject();
        Assert.assertSame(first, second);
    }

    /* Construct via bytes constructor; getObject() deserialises to equal value */
    @Test
    public void testBytesConstructor() {
        ObjectSnapshot original = new ObjectSnapshot(99);
        byte[] bytes = original.getBytes();

        ObjectSnapshot fromBytes = new ObjectSnapshot(Integer.class, bytes);
        Assert.assertEquals(99, fromBytes.getObject());
    }

    /* getBytes() must be non-empty after snapshotting a value */
    @Test
    public void testGetBytesNonEmpty() {
        ObjectSnapshot snapshot = new ObjectSnapshot(123);
        Assert.assertTrue(snapshot.getBytes().length > 0);
    }

    @Test
    public void testListOfIntegers() {
        List<Integer> listOfIntegers = new ArrayList<>();
        for(int i = 0; i < 100; ++i) {
            listOfIntegers.add(i);
        }

        ObjectSnapshot snapshot = new ObjectSnapshot(listOfIntegers);
        Object obj = snapshot.getObject();
        Assert.assertTrue(obj instanceof List<?>);
        List<?> list = (List<?>) obj;
        Assert.assertEquals(list.size(), listOfIntegers.size());
        Object first = list.getFirst();
        Assert.assertTrue(first instanceof Integer);
        for(int i = 0; i < 100; ++i) {
            Assert.assertEquals(i, list.get(i));
        }
    }

    @Test
    public void testMapOfIntegers() {
        Map<Integer, String> mapOfIntegers = new HashMap<>();
        for(int i = 0; i < 100; ++i) {
            mapOfIntegers.put(i, String.valueOf(i));
        }

        ObjectSnapshot snapshot = new ObjectSnapshot(mapOfIntegers);
        Object obj = snapshot.getObject();
        Assert.assertTrue(obj instanceof Map<?,?>);
        Map<?,?> map = (Map<?,?>) obj;
        Assert.assertEquals(map.size(), mapOfIntegers.size());

        map.forEach((key, value) -> {
            Assert.assertTrue(key instanceof Integer);
            Assert.assertTrue(value instanceof String);

            Assert.assertEquals(String.valueOf(key), value);
        });
    }
}
