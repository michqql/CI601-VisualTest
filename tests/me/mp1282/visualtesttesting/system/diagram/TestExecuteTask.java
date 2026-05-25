package me.mp1282.visualtesttesting.system.diagram;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.MethodExecutable;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodExecutable;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodRepository;
import me.mp1282.visualtest.system.inbuilt.category.BooleanLogicInbuiltMethods;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ConstantExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ForLoopExecutable;
import me.mp1282.visualtest.system.inbuilt.special.RangeCheckExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestExecuteTask {

    private static final System.Logger LOG = System.getLogger("test");

    private Diagram diagram;

    private static DiagramNode makeNode(Executable exe) {
        exe.init(null);
        return new DiagramNode(exe);
    }

    @Before
    public void setUp() {
        diagram = new Diagram();
    }

    /* --- Topological sort (createExecutionOrder) --- */

    /* A->B->C via execution paths; execution order must be A, B, C */
    @Test
    public void testLinearChainOrder() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        DiagramNode c = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.addDiagramNode(c);
        diagram.connectExecutionPath(a, 0, b);
        diagram.connectExecutionPath(b, 0, c);

        ExecuteTask task = new ExecuteTask(diagram);
        List<DiagramNode> order = task.getExecutionOrder();

        Assert.assertEquals(3, order.size());
        Assert.assertSame(a, order.get(0));
        Assert.assertSame(b, order.get(1));
        Assert.assertSame(c, order.get(2));
    }

    /* Data edge: Constant output -> RangeCheck input forces Constant to appear first */
    @Test
    public void testDataEdgeOrdering() {
        DiagramNode src  = makeNode(new ConstantExecutable());   /* 1 output */
        DiagramNode dest = makeNode(new RangeCheckExecutable()); /* 1 input */
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);
        diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        ExecuteTask task = new ExecuteTask(diagram);
        List<DiagramNode> order = task.getExecutionOrder();

        Assert.assertTrue("src must precede dest", order.indexOf(src) < order.indexOf(dest));
    }

    /* Both a data edge (A->B) and an execution path (C->B) constrain B to come last */
    @Test
    public void testMixedEdgesOrdering() {
        DiagramNode a = makeNode(new ConstantExecutable());   /* data output -> b */
        DiagramNode b = makeNode(new RangeCheckExecutable()); /* depends on a (data) and c (exec) */
        DiagramNode c = makeNode(new ConstantExecutable());   /* exec path -> b */
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.addDiagramNode(c);
        diagram.connectDataPorts(a.getOutputs().get(0), b.getInputs().get(0));
        diagram.connectExecutionPath(c, 0, b);

        ExecuteTask task = new ExecuteTask(diagram);
        List<DiagramNode> order = task.getExecutionOrder();

        Assert.assertEquals(3, order.size());
        Assert.assertTrue("a must precede b", order.indexOf(a) < order.indexOf(b));
        Assert.assertTrue("c must precede b", order.indexOf(c) < order.indexOf(b));
    }

    /* Nodes with no connections must still appear in the execution order */
    @Test
    public void testDisconnectedNodesIncluded() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        DiagramNode c = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.addDiagramNode(c);

        ExecuteTask task = new ExecuteTask(diagram);

        Assert.assertEquals(3, task.getExecutionOrder().size());
    }

    /* A cycle in execution paths must cause the constructor to throw RuntimeException */
    @Test(expected = RuntimeException.class)
    public void testCycleThrows() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);

        /* Manually wire a cycle, bypassing Diagram's own cycle detection */
        a.getNodeAfterPath(0).setOther(b);
        b.getNodeBefore().setOther(a);
        b.getNodeAfterPath(0).setOther(a);
        a.getNodeBefore().setOther(b);

        new ExecuteTask(diagram); /* must throw */
    }

    /* --- Stepping and data flow --- */

    /* canStep() returns true while nodes remain, false when queue is exhausted */
    @Test
    public void testCanStep() throws Exception {
        DiagramNode node = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(node);

        ExecuteTask task = new ExecuteTask(diagram);
        Assert.assertTrue(task.canStep());

        task.step(LOG);
        Assert.assertFalse(task.canStep());
    }

    /* Value from Constant(42) must appear in outputPortToLastValueMap after stepping */
    @Test
    public void testStepDataFlow() throws Exception {
        DiagramNode constNode = makeNode(new ConstantExecutable());
        ((ConstantNodeData) constNode.getData()).constantProperty().set(42);

        DiagramNode rcNode = makeNode(new RangeCheckExecutable());
        diagram.addDiagramNode(constNode);
        diagram.addDiagramNode(rcNode);
        diagram.connectDataPorts(constNode.getOutputs().get(0), rcNode.getInputs().get(0));

        ExecuteTask task = new ExecuteTask(diagram);
        task.step(LOG); /* step Constant */

        Assert.assertEquals(42, task.getOutputPortValue(constNode.getOutputs().get(0)));
    }

    /* After Branch (null input -> false branch): true-path node is skipped, false-path is not */
    @Test
    public void testBranchSkipPropagation() throws Exception {
        DiagramNode branchNode = makeNode(new BranchExecutable());
        DiagramNode trueNode   = makeNode(new ConstantExecutable());
        DiagramNode falseNode  = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(branchNode);
        diagram.addDiagramNode(trueNode);
        diagram.addDiagramNode(falseNode);
        /* null input -> false branch chosen, so true-path is skipped */
        diagram.connectExecutionPath(branchNode, BranchExecutable.TRUE_BRANCH,  trueNode);
        diagram.connectExecutionPath(branchNode, BranchExecutable.FALSE_BRANCH, falseNode);

        ExecuteTask task = new ExecuteTask(diagram);
        task.step(LOG); /* step Branch */

        Assert.assertTrue(task.getSkippedNodes().contains(trueNode));
        Assert.assertFalse(task.getSkippedNodes().contains(falseNode));
    }

    /* After Branch (null input -> FALSE branch): TRUE path node is skipped, FALSE path is not,
     * OR node is skipped because TRUE path was skipped
     */
    @Test
    public void testBranchSkipPropagationToBothNode() throws Exception {
        DiagramNode branchNode = makeNode(new BranchExecutable());
        DiagramNode trueNode   = makeNode(new ConstantExecutable());
        DiagramNode falseNode  = makeNode(new ConstantExecutable());
        DiagramNode orNode     = makeNode(new MethodExecutable(
                BooleanLogicInbuiltMethods.class.getDeclaredMethod("or", boolean.class, boolean.class)) {});
        diagram.addDiagramNode(branchNode);
        diagram.addDiagramNode(trueNode);
        diagram.addDiagramNode(falseNode);
        diagram.addDiagramNode(orNode);
        /* null input -> false branch chosen, so true-path is skipped */
        diagram.connectExecutionPath(branchNode, BranchExecutable.TRUE_BRANCH,  trueNode);
        diagram.connectExecutionPath(branchNode, BranchExecutable.FALSE_BRANCH, falseNode);
        /* TRUE & FALSE input -> or node */
        diagram.connectDataPorts(trueNode.getOutputs().getFirst(), orNode.getInputs().get(0));
        diagram.connectDataPorts(falseNode.getOutputs().getFirst(), orNode.getInputs().get(1));

        ExecuteTask task = new ExecuteTask(diagram);
        task.step(LOG); /* step Branch */

        Assert.assertTrue(task.getSkippedNodes().contains(trueNode));
        Assert.assertFalse(task.getSkippedNodes().contains(falseNode));
        Assert.assertFalse(task.getSkippedNodes().contains(orNode));

        while(task.canStep())
            task.step(LOG);

        Assert.assertTrue(task.getSkippedNodes().contains(trueNode));
        Assert.assertFalse(task.getSkippedNodes().contains(falseNode));
        Assert.assertTrue(task.getSkippedNodes().contains(orNode));
    }

    /* --- ForLoop --- */

    /* ForLoop with count=0 and no body: doneNode is stepped, no error */
    @Test
    public void testForLoopZeroIterations() throws Exception {
        DiagramNode loopNode = makeNode(new ForLoopExecutable());
        DiagramNode doneNode = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(loopNode);
        diagram.addDiagramNode(doneNode);
        /* DONE path -> doneNode; LOOP path unconnected (null input -> count=0) */
        diagram.connectExecutionPath(loopNode, ForLoopExecutable.DONE_INDEX, doneNode);

        ExecuteTask task = new ExecuteTask(diagram);
        task.step(LOG); /* step ForLoop — handleForLoop runs 0 times */

        Assert.assertTrue("doneNode must still be pending", task.canStep());
        task.step(LOG); /* step doneNode */
        Assert.assertFalse(task.canStep());
    }

    /* ForLoop with count=3: body node executed inline, marked skipped; done path taken */
    @Test
    public void testForLoopNIterations() throws Exception {
        /* countNode outputs 3 -> loopNode input */
        DiagramNode countNode = makeNode(new ConstantExecutable());
        ((ConstantNodeData) countNode.getData()).constantProperty().set(3);

        DiagramNode loopNode = makeNode(new ForLoopExecutable());
        DiagramNode bodyNode = makeNode(new ConstantExecutable()); /* loop body */
        DiagramNode doneNode = makeNode(new ConstantExecutable()); /* post-loop */

        diagram.addDiagramNode(countNode);
        diagram.addDiagramNode(loopNode);
        diagram.addDiagramNode(bodyNode);
        diagram.addDiagramNode(doneNode);

        /* Data edge: countNode -> loopNode provides the iteration count */
        diagram.connectDataPorts(countNode.getOutputs().get(0), loopNode.getInputs().get(0));
        /* Execution paths */
        diagram.connectExecutionPath(loopNode, ForLoopExecutable.LOOP_INDEX, bodyNode);
        diagram.connectExecutionPath(loopNode, ForLoopExecutable.DONE_INDEX, doneNode);

        ExecuteTask task = new ExecuteTask(diagram);
        task.step(LOG); /* step countNode */
        task.step(LOG); /* step loopNode — handleForLoop runs bodyNode 3 times inline */

        /* bodyNode was pulled out of executionOrder by handleForLoop, then marked skipped */
        Assert.assertTrue(task.getSkippedNodes().contains(bodyNode));
        /* doneNode must be pending and NOT skipped */
        Assert.assertTrue(task.canStep());
        Assert.assertFalse(task.getSkippedNodes().contains(doneNode));

        task.step(LOG); /* step doneNode */
        Assert.assertFalse(task.canStep());
    }
}
