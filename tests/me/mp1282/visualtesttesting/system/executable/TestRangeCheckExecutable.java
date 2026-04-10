package me.mp1282.visualtesttesting.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.node.data.RangeCheckNodeData;
import me.mp1282.visualtest.system.inbuilt.special.RangeCheckExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestRangeCheckExecutable {

    private RangeCheckExecutable rangeCheck;

    /** Returns a RangeCheckNodeData with the given min and max already set. */
    private static RangeCheckNodeData nodeData(Number min, Number max) {
        RangeCheckNodeData data = new RangeCheckNodeData();
        data.minProperty().set(min);
        data.maxProperty().set(max);
        return data;
    }

    @Before
    public void setUp() {
        rangeCheck = new RangeCheckExecutable();
        rangeCheck.init(null);
    }

    /* 5.0 is inside [0, 10] → true */
    @Test
    public void testValueInRange() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{5.0}, outputs, nodeData(0, 10));
        Assert.assertEquals(true, outputs[0]);
    }

    /* Boundary: 0.0 is exactly at min → true (inclusive) */
    @Test
    public void testValueAtMin() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{0.0}, outputs, nodeData(0, 10));
        Assert.assertEquals(true, outputs[0]);
    }

    /* Boundary: 10.0 is exactly at max → true (inclusive) */
    @Test
    public void testValueAtMax() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{10.0}, outputs, nodeData(0, 10));
        Assert.assertEquals(true, outputs[0]);
    }

    /* -1.0 is below min → false */
    @Test
    public void testValueBelowMin() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{-1.0}, outputs, nodeData(0, 10));
        Assert.assertEquals(false, outputs[0]);
    }

    /* 11.0 is above max → false */
    @Test
    public void testValueAboveMax() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{11.0}, outputs, nodeData(0, 10));
        Assert.assertEquals(false, outputs[0]);
    }

    /* Null min → false */
    @Test
    public void testNullMin() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{5.0}, outputs, nodeData(null, 10));
        Assert.assertEquals(false, outputs[0]);
    }

    /* Null max → false */
    @Test
    public void testNullMax() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{5.0}, outputs, nodeData(0, null));
        Assert.assertEquals(false, outputs[0]);
    }

    /* Null input value → false */
    @Test
    public void testNullInput() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{null}, outputs, nodeData(0, 10));
        Assert.assertEquals(false, outputs[0]);
    }

    /* Wrong NodeData subtype → false */
    @Test
    public void testWrongNodeDataType() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{5.0}, outputs, new NodeData());
        Assert.assertEquals(false, outputs[0]);
    }

    /* Integer input is accepted as a Number → true when in range */
    @Test
    public void testIntegerInput() throws Exception {
        Object[] outputs = new Object[1];
        rangeCheck.execute(new Object[]{5}, outputs, nodeData(0, 10));
        Assert.assertEquals(true, outputs[0]);
    }

    /* createNodeData() returns a non-null RangeCheckNodeData */
    @Test
    public void testNodeDataCreation() {
        NodeData data = rangeCheck.createNodeData();
        Assert.assertNotNull(data);
        Assert.assertSame(RangeCheckNodeData.class, data.getClass());
    }
}
