package me.mp1282.visualtesttesting.system.diagram;

import me.mp1282.visualtest.system.diagram.runtime.ObjectSnapshot;
import org.junit.Assert;
import org.junit.Test;

public class TestObjectSnapshot {

    /* Integer round-trip: snapshot → getObject() returns equal value */
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
}
