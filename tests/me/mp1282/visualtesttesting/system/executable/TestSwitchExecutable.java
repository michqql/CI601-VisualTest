package me.mp1282.visualtesttesting.system.executable;

import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.node.data.SwitchNodeData;
import me.mp1282.visualtest.system.inbuilt.special.SwitchExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSwitchExecutable {

    private SwitchExecutable sw;
    private SwitchNodeData   data;

    @Before
    public void setUp() {
        sw   = new SwitchExecutable(2); /* 2 cases + 1 DEFAULT = 3 paths */
        sw.init(null);
        data = (SwitchNodeData) sw.createNodeData();
        /* Set case values: case 0 = "hello", case 1 = "world" */
        data.getCases().get(0).valueProperty().set("hello");
        data.getCases().get(1).valueProperty().set("world");
    }

    /* Input matches first case -> returns 0 */
    @Test
    public void testMatchingCaseChosen() {
        int index = sw.getChosenBranchIndex(new Object[]{"hello"}, data);
        Assert.assertEquals(0, index);
    }

    /* Input matches second case -> returns 1 */
    @Test
    public void testSecondCaseChosen() {
        int index = sw.getChosenBranchIndex(new Object[]{"world"}, data);
        Assert.assertEquals(1, index);
    }

    /* Input matches no case -> returns caseCount (DEFAULT index) */
    @Test
    public void testNoMatchDefaultChosen() {
        int index = sw.getChosenBranchIndex(new Object[]{"unknown"}, data);
        Assert.assertEquals(sw.getCaseCount(), index);
    }

    /* A case whose value is blank must never match any input, including blank input */
    @Test
    public void testEmptyCaseValueSkipped() {
        data.getCases().get(0).valueProperty().set(""); /* blank — must not match */
        int index = sw.getChosenBranchIndex(new Object[]{""}, data);
        Assert.assertEquals(sw.getCaseCount(), index); /* -> DEFAULT */
    }

    /* If NodeData is not SwitchNodeData, must fall through to DEFAULT */
    @Test
    public void testWrongDataTypeDefaultChosen() {
        int index = sw.getChosenBranchIndex(new Object[]{"hello"}, new NodeData());
        Assert.assertEquals(sw.getCaseCount(), index);
    }

    /* getExecutionPathOutputCount() == caseCount + 1 */
    @Test
    public void testExecutionPathOutputCount() {
        SwitchExecutable sw3 = new SwitchExecutable(3);
        sw3.init(null);
        Assert.assertEquals(4, sw3.getExecutionPathOutputCount());
    }

    /* DEFAULT path label is "DEFAULT" at index caseCount */
    @Test
    public void testDefaultLabel() {
        Assert.assertEquals("DEFAULT", sw.getExecutionPathLabel(sw.getCaseCount()));
    }

    /* Case port labels are null (rendered dynamically by the skin) */
    @Test
    public void testCaseLabelIsNull() {
        Assert.assertNull(sw.getExecutionPathLabel(0));
        Assert.assertNull(sw.getExecutionPathLabel(1));
    }

    /* createForNode() produces a distinct instance with the same caseCount */
    @Test
    public void testCreateForNodeIsIndependent() {
        SwitchExecutable forNode = (SwitchExecutable) sw.createForNode();
        Assert.assertNotSame(sw, forNode);
        Assert.assertEquals(sw.getCaseCount(), forNode.getCaseCount());
    }

    /* getExtraConfig() -> restoreFromConfig() preserves caseCount */
    @Test
    public void testExtraConfigRoundTrip() {
        SwitchExecutable sw5 = new SwitchExecutable(5);
        sw5.init(null);
        SwitchExecutable restored = (SwitchExecutable) sw5.restoreFromConfig(sw5.getExtraConfig());
        Assert.assertEquals(5, restored.getCaseCount());
    }

    /* execute() is a no-op: outputs array must not be modified */
    @Test
    public void testExecuteIsNoop() throws Exception {
        Object[] outputs = new Object[]{"sentinel"};
        sw.execute(new Object[]{"hello"}, outputs, data);
        Assert.assertEquals("sentinel", outputs[0]);
    }

    /* After init(), exactly 1 parameter (the value to switch on) */
    @Test
    public void testParameterCount() {
        Assert.assertEquals(1, sw.getNumberOfParameters());
    }

    /* Switch has no data return values — branching is via execution paths */
    @Test
    public void testReturnCount() {
        Assert.assertEquals(0, sw.getNumberOfReturnValues());
    }
}
