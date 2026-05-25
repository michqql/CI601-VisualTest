package me.mp1282.visualtesttesting.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestBranchExecutable {

    private BranchExecutable branch;

    @Before
    public void setUp() {
        branch = new BranchExecutable();
        branch.init(null);
    }

    /* Boolean true input -> TRUE_BRANCH (0) */
    @Test
    public void testTrueBranchChosen() {
        int index = branch.getChosenBranchIndex(new Object[]{true}, new NodeData());
        Assert.assertEquals(BranchExecutable.TRUE_BRANCH, index);
    }

    /* Boolean false input -> FALSE_BRANCH (1) */
    @Test
    public void testFalseBranchChosen() {
        int index = branch.getChosenBranchIndex(new Object[]{false}, new NodeData());
        Assert.assertEquals(BranchExecutable.FALSE_BRANCH, index);
    }

    /* Non-boolean object in inputs[0] -> FALSE_BRANCH */
    @Test
    public void testNonBooleanDefaultsFalse() {
        int index = branch.getChosenBranchIndex(new Object[]{"not a boolean"}, new NodeData());
        Assert.assertEquals(BranchExecutable.FALSE_BRANCH, index);
    }

    /* null in inputs[0] -> FALSE_BRANCH */
    @Test
    public void testNullInputDefaultsFalse() {
        int index = branch.getChosenBranchIndex(new Object[]{null}, new NodeData());
        Assert.assertEquals(BranchExecutable.FALSE_BRANCH, index);
    }

    /* Execution path labels are "TRUE", "FALSE", and null for out-of-range */
    @Test
    public void testExecutionPathLabels() {
        Assert.assertEquals("TRUE",  branch.getExecutionPathLabel(BranchExecutable.TRUE_BRANCH));
        Assert.assertEquals("FALSE", branch.getExecutionPathLabel(BranchExecutable.FALSE_BRANCH));
        Assert.assertNull(branch.getExecutionPathLabel(2));
    }

    /* Branch executable exposes exactly 2 execution path outputs */
    @Test
    public void testExecutionPathOutputCount() {
        Assert.assertEquals(2, branch.getExecutionPathOutputCount());
    }

    /* execute() is a no-op: outputs array must not be modified */
    @Test
    public void testExecuteIsNoop() throws Exception {
        Object[] outputs = new Object[]{"sentinel"};
        branch.execute(new Object[]{true}, outputs, new NodeData());
        Assert.assertEquals("sentinel", outputs[0]);
    }

    /* After init(), exactly 1 parameter (the boolean condition) */
    @Test
    public void testParameterCount() {
        Assert.assertEquals(1, branch.getNumberOfParameters());
    }

    /* After init(), no data return values — branching is via execution paths only */
    @Test
    public void testReturnCount() {
        Assert.assertEquals(0, branch.getNumberOfReturnValues());
    }
}
