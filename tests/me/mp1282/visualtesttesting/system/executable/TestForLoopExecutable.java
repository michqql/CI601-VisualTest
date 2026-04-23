package me.mp1282.visualtesttesting.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.inbuilt.special.ForLoopExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestForLoopExecutable {

    private ForLoopExecutable loop;

    @Before
    public void setUp() {
        loop = new ForLoopExecutable();
        loop.init(null);
    }

    /* Exposes exactly 2 execution paths: LOOP and DONE */
    @Test
    public void testExecutionPathOutputCount() {
        Assert.assertEquals(2, loop.getExecutionPathOutputCount());
    }

    /* LOOP path label */
    @Test
    public void testLoopLabel() {
        Assert.assertEquals("LOOP", loop.getExecutionPathLabel(ForLoopExecutable.LOOP_INDEX));
    }

    /* DONE path label */
    @Test
    public void testDoneLabel() {
        Assert.assertEquals("DONE", loop.getExecutionPathLabel(ForLoopExecutable.DONE_INDEX));
    }

    /* Out-of-range branch index returns null */
    @Test
    public void testOutOfRangeLabelNull() {
        Assert.assertNull(loop.getExecutionPathLabel(2));
        Assert.assertNull(loop.getExecutionPathLabel(-1));
    }

    /* execute() is a no-op: outputs array must not be modified */
    @Test
    public void testExecuteIsNoop() throws Exception {
        Object[] outputs = new Object[]{"sentinel"};
        loop.execute(new Object[]{5}, outputs, new NodeData());
        Assert.assertEquals("sentinel", outputs[0]);
    }

    /* Exactly 1 parameter: the iteration count (Integer) */
    @Test
    public void testParameterCount() {
        Assert.assertEquals(1, loop.getNumberOfParameters());
    }

    /* Exactly 1 return value: the current loop index (Integer) */
    @Test
    public void testReturnCount() {
        Assert.assertEquals(1, loop.getNumberOfReturnValues());
    }
}
