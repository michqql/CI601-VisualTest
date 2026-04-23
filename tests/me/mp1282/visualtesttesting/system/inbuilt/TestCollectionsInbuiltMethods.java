package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.CollectionsInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestCollectionsInbuiltMethods {

    /* --- creation --- */

    @Test
    public void testNewArrayList() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList(1, 2, 3);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(1, list.get(0));
        Assert.assertEquals(3, list.get(2));
    }

    @Test
    public void testNewEmptyArrayList() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newEmptyArrayList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    /* --- add / size / get --- */

    @Test
    public void testAddAndSize() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newEmptyArrayList();
        CollectionsInbuiltMethods.add(list, "a");
        CollectionsInbuiltMethods.add(list, "b");
        Assert.assertEquals(2, CollectionsInbuiltMethods.size(list));
        Assert.assertEquals("a", CollectionsInbuiltMethods.get(list, 0));
        Assert.assertEquals("b", CollectionsInbuiltMethods.get(list, 1));
    }

    /* --- remove --- */

    @Test
    public void testRemoveAt() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("x", "y", "z");
        Object removed = CollectionsInbuiltMethods.removeAt(list, 1);
        Assert.assertEquals("y", removed);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("z", list.get(1));
    }

    @Test
    public void testRemoveObject() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c");
        boolean removed = CollectionsInbuiltMethods.removeObject(list, "b");
        Assert.assertTrue(removed);
        Assert.assertEquals(2, list.size());
        Assert.assertFalse(CollectionsInbuiltMethods.removeObject(list, "z"));
    }

    /* --- contains / isEmpty / clear --- */

    @Test
    public void testContains() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c");
        Assert.assertTrue (CollectionsInbuiltMethods.contains(list, "b"));
        Assert.assertFalse(CollectionsInbuiltMethods.contains(list, "z"));
    }

    @Test
    public void testIsEmpty() {
        ArrayList<Object> empty = CollectionsInbuiltMethods.newEmptyArrayList();
        Assert.assertTrue(CollectionsInbuiltMethods.isEmpty(empty));
        CollectionsInbuiltMethods.add(empty, 1);
        Assert.assertFalse(CollectionsInbuiltMethods.isEmpty(empty));
    }

    @Test
    public void testClear() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList(1, 2, 3);
        CollectionsInbuiltMethods.clear(list);
        Assert.assertTrue(CollectionsInbuiltMethods.isEmpty(list));
    }

    /* --- set / indexOf / lastIndexOf --- */

    @Test
    public void testSet() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c");
        Object previous = CollectionsInbuiltMethods.set(list, 1, "X");
        Assert.assertEquals("b", previous);
        Assert.assertEquals("X", list.get(1));
    }

    @Test
    public void testIndexOf() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c", "b");
        Assert.assertEquals(1,  CollectionsInbuiltMethods.indexOf(list, "b"));
        Assert.assertEquals(-1, CollectionsInbuiltMethods.indexOf(list, "z"));
    }

    @Test
    public void testLastIndexOf() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c", "b");
        Assert.assertEquals(3, CollectionsInbuiltMethods.lastIndexOf(list, "b"));
        Assert.assertEquals(0, CollectionsInbuiltMethods.lastIndexOf(list, "a"));
    }

    /* --- addAll / copy --- */

    @Test
    public void testAddAll() {
        ArrayList<Object> list  = CollectionsInbuiltMethods.newArrayList("a", "b");
        ArrayList<Object> extra = CollectionsInbuiltMethods.newArrayList("c", "d");
        CollectionsInbuiltMethods.addAll(list, extra);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("c", list.get(2));
    }

    @Test
    public void testCopy() {
        ArrayList<Object> original = CollectionsInbuiltMethods.newArrayList(1, 2, 3);
        ArrayList<Object> copy     = CollectionsInbuiltMethods.copy(original);
        Assert.assertEquals(original, copy);
        /* modifications to the copy must not affect the original */
        copy.add(4);
        Assert.assertEquals(3, original.size());
    }

    /* --- reverse / swap --- */

    @Test
    public void testReverse() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList(1, 2, 3);
        CollectionsInbuiltMethods.reverse(list);
        Assert.assertEquals(3, list.get(0));
        Assert.assertEquals(2, list.get(1));
        Assert.assertEquals(1, list.get(2));
    }

    @Test
    public void testSwap() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c");
        CollectionsInbuiltMethods.swap(list, 0, 2);
        Assert.assertEquals("c", list.get(0));
        Assert.assertEquals("a", list.get(2));
    }

    /* --- subList / join / frequency --- */

    @Test
    public void testSubList() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c", "d");
        ArrayList<Object> sub  = CollectionsInbuiltMethods.subList(list, 1, 3);
        Assert.assertEquals(2, sub.size());
        Assert.assertEquals("b", sub.get(0));
        Assert.assertEquals("c", sub.get(1));
    }

    @Test
    public void testJoin() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "c");
        Assert.assertEquals("a,b,c",   CollectionsInbuiltMethods.join(list, ","));
        Assert.assertEquals("a - b - c", CollectionsInbuiltMethods.join(list, " - "));
    }

    @Test
    public void testFrequency() {
        ArrayList<Object> list = CollectionsInbuiltMethods.newArrayList("a", "b", "a", "c", "a");
        Assert.assertEquals(3, CollectionsInbuiltMethods.frequency(list, "a"));
        Assert.assertEquals(1, CollectionsInbuiltMethods.frequency(list, "b"));
        Assert.assertEquals(0, CollectionsInbuiltMethods.frequency(list, "z"));
    }
}
