package me.mp1282.visualtesttesting.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.system.inbuilt.special.ConstantExecutable;
import org.junit.Assert;
import org.junit.Test;

public class TestConstantExecutable {

    /* Execute constant exe with null constant value */
    @Test
    public void test1() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(null);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = 1;

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now null */
        Assert.assertNull(outputs[0]);
    }

    /* Execute constant exe with boolean constant value */
    @Test
    public void test2() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(true);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = null;

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now TRUE */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Boolean.class, clazz);
        Assert.assertSame(Boolean.TRUE, outputs[0]);
        Assert.assertNotSame(Boolean.FALSE, outputs[0]);
    }

    /* Execute constant exe with byte constant value */
    @Test
    public void test3() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set((byte) 0xFF);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Byte.class, clazz);
        Assert.assertSame((byte) 0xFF, outputs[0]);
    }

    /* Execute constant exe with short constant value */
    @Test
    public void test4() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set((short) 0xDEAD);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xDEAD */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Short.class, clazz);
        Assert.assertEquals((short) 0xDEAD, outputs[0]);
    }

    /* Execute constant exe with int constant value */
    @Test
    public void test5() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(1234);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Integer.class, clazz);
        Assert.assertNotSame(int.class, clazz);
        Assert.assertEquals(1234, outputs[0]);
    }

    /* Execute constant exe with long constant value */
    @Test
    public void test6() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(Long.MIN_VALUE);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Long.class, clazz);
        Assert.assertEquals(Long.MIN_VALUE, outputs[0]);
    }

    /* Execute constant exe with byte constant value */
    @Test
    public void test7() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(Float.MAX_VALUE);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Float.class, clazz);
        Assert.assertEquals(Float.MAX_VALUE, outputs[0]);
    }

    /* Execute constant exe with double constant value */
    @Test
    public void test8() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set(Double.MAX_VALUE);

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Double.class, clazz);
        Assert.assertEquals(Double.MAX_VALUE, outputs[0]);
    }

    /* Execute constant exe with char constant value */
    @Test
    public void test9() {
        ConstantNodeData nodeData = new ConstantNodeData();
        nodeData.constantProperty().set('a');

        Object[] outputs = new Object[1]; /* Should only produce 1 output */
        outputs[0] = "HELLO WORLD";

        ConstantExecutable constantExecutable = new ConstantExecutable();
        constantExecutable.execute(new Object[0], outputs, nodeData);

        /* Check outputs[0] is now 0xFF */
        Class<?> clazz = outputs[0].getClass();
        Assert.assertSame(Character.class, clazz);
        Assert.assertEquals('a', outputs[0]);
    }

}
