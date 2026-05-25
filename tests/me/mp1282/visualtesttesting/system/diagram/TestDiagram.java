package me.mp1282.visualtesttesting.system.diagram;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ConstantExecutable;
import me.mp1282.visualtest.system.inbuilt.special.RangeCheckExecutable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class TestDiagram {

    private Diagram diagram;

    /** Creates a fully initialised DiagramNode for the given executable. */
    private static DiagramNode makeNode(Executable exe) {
        exe.init(null);
        return new DiagramNode(exe);
    }

    @Before
    public void setUp() {
        diagram = new Diagram();
    }

    /* --- Node management --- */

    /* addDiagramNode: node appears in the list */
    @Test
    public void testAddNode() {
        DiagramNode node = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(node);
        Assert.assertEquals(1, diagram.nodesProperty().size());
        Assert.assertTrue(diagram.nodesProperty().contains(node));
    }

    /* removeDiagramNode: list is empty after removal */
    @Test
    public void testRemoveNode() {
        DiagramNode node = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(node);
        diagram.removeDiagramNode(node);
        Assert.assertEquals(0, diagram.nodesProperty().size());
    }

    /* --- Unsaved flag --- */

    /* Adding a node marks the diagram as unsaved */
    @Test
    public void testUnsavedOnNodeAdd() {
        diagram.unsavedProperty().set(false);
        diagram.addDiagramNode(makeNode(new ConstantExecutable()));
        Assert.assertTrue(diagram.unsavedProperty().get());
    }

    /* Changing the name marks the diagram as unsaved */
    @Test
    public void testUnsavedOnNameChange() {
        diagram.unsavedProperty().set(false);
        diagram.nameProperty().set("NewName");
        Assert.assertTrue(diagram.unsavedProperty().get());
    }

    /* --- Execution path connections --- */

    /* connectExecutionPath links source branch 0 -> target */
    @Test
    public void testConnectExecutionPath() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);

        boolean connected = diagram.connectExecutionPath(a, 0, b);

        Assert.assertTrue(connected);
        Assert.assertSame(b, a.getNodeAfterPath(0).getOther());
        Assert.assertSame(a, b.getNodeBefore().getOther());
    }

    /* A->B already connected; connecting B->A must be rejected (cycle) */
    @Test
    public void testConnectExecutionPathCycleRejected() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.connectExecutionPath(a, 0, b);

        boolean connected = diagram.connectExecutionPath(b, 0, a);

        Assert.assertFalse(connected);
    }

    /* disconnectExecutionPath nulls both ends of the link */
    @Test
    public void testDisconnectExecutionPath() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.connectExecutionPath(a, 0, b);

        diagram.disconnectExecutionPath(a, b);

        Assert.assertNull(a.getNodeAfterPath(0).getOther());
        Assert.assertNull(b.getNodeBefore().getOther());
    }

    /* disconnectAllExecutionPaths clears all incoming and outgoing paths */
    @Test
    public void testDisconnectAllExecutionPaths() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        DiagramNode c = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.addDiagramNode(c);
        diagram.connectExecutionPath(a, 0, b);
        diagram.connectExecutionPath(b, 0, c);

        diagram.disconnectAllExecutionPaths(b);

        Assert.assertNull(a.getNodeAfterPath(0).getOther());
        Assert.assertNull(b.getNodeBefore().getOther());
        Assert.assertNull(b.getNodeAfterPath(0).getOther());
        Assert.assertNull(c.getNodeBefore().getOther());
    }

    /* BranchExecutable has 2 execution paths; both can be connected */
    @Test
    public void testBranchExecutableTwoPaths() {
        DiagramNode branch = makeNode(new BranchExecutable());
        DiagramNode trueNode  = makeNode(new ConstantExecutable());
        DiagramNode falseNode = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(branch);
        diagram.addDiagramNode(trueNode);
        diagram.addDiagramNode(falseNode);

        Assert.assertTrue(diagram.connectExecutionPath(branch, BranchExecutable.TRUE_BRANCH,  trueNode));
        Assert.assertTrue(diagram.connectExecutionPath(branch, BranchExecutable.FALSE_BRANCH, falseNode));
        Assert.assertSame(trueNode,  branch.getNodeAfterPath(BranchExecutable.TRUE_BRANCH).getOther());
        Assert.assertSame(falseNode, branch.getNodeAfterPath(BranchExecutable.FALSE_BRANCH).getOther());
    }

    /* --- Data port connections --- */

    /* connectDataPorts links an output to an input and back */
    @Test
    public void testConnectDataPorts() {
        DiagramNode src  = makeNode(new ConstantExecutable());   /* 0 inputs, 1 output */
        DiagramNode dest = makeNode(new RangeCheckExecutable()); /* 1 input,  1 output */
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);

        boolean connected = diagram.connectDataPorts(
                src.getOutputs().get(0),
                dest.getInputs().get(0)
        );

        Assert.assertTrue(connected);
        Assert.assertSame(dest.getInputs().get(0),  src.getOutputs().get(0).getTo());
        Assert.assertSame(src.getOutputs().get(0),  dest.getInputs().get(0).getFrom());
    }

    /* Tests that a cyclic data port connection is rejected */
    @Test
    public void testConnectDataPortsCycleRejected() {
        /* src -> dest via data port, then attempt dest -> src */
        DiagramNode src  = makeNode(new RangeCheckExecutable()); /* 1 input, 1 output */
        DiagramNode dest = makeNode(new RangeCheckExecutable()); /* 1 input, 1 output */
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);

        boolean connected = diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        Assert.assertTrue(connected);
        /* Asserts initial connection state after single connection */
        Assert.assertNull(src.getInputs().get(0).getFrom());
        Assert.assertNotNull(src.getOutputs().get(0).getTo());
        Assert.assertNotNull(dest.getInputs().get(0).getFrom());
        Assert.assertNull(dest.getOutputs().get(0).getTo());

        connected = diagram.connectDataPorts(dest.getOutputs().get(0), src.getInputs().get(0));

        Assert.assertFalse(connected);
        /* Checks that connection state has not changed */
        Assert.assertNull(src.getInputs().get(0).getFrom());
        Assert.assertNotNull(src.getOutputs().get(0).getTo());
        Assert.assertNotNull(dest.getInputs().get(0).getFrom());
        Assert.assertNull(dest.getOutputs().get(0).getTo());
    }

    /* disconnectDataPort severs the link from both sides */
    @Test
    public void testDisconnectDataPort() {
        DiagramNode src  = makeNode(new ConstantExecutable());
        DiagramNode dest = makeNode(new RangeCheckExecutable());
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);
        diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        diagram.disconnectDataPort(src.getOutputs().get(0));

        Assert.assertNull(src.getOutputs().get(0).getTo());
        Assert.assertNull(dest.getInputs().get(0).getFrom());
    }

    /* --- Query methods --- */

    /* Unconnected ports are returned; connected ones are not */
    @Test
    public void testGetDataPortsWithoutConnections() {
        DiagramNode src  = makeNode(new ConstantExecutable());   /* 1 output */
        DiagramNode dest = makeNode(new RangeCheckExecutable()); /* 1 input, 1 output */
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);

        /* Before connection: src.output + dest.input + dest.output = 3 unconnected */
        List<IDataPort<?>> before = diagram.getDataPortsWithoutConnections();
        Assert.assertEquals(3, before.size());

        diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        /* After connection: dest.output = 1 unconnected */
        List<IDataPort<?>> after = diagram.getDataPortsWithoutConnections();
        Assert.assertEquals(1, after.size());
    }

    /* getAllConnectedOutputs returns only outputs that have a destination */
    @Test
    public void testGetAllConnectedOutputs() {
        DiagramNode src  = makeNode(new ConstantExecutable());
        DiagramNode dest = makeNode(new RangeCheckExecutable());
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);

        Assert.assertEquals(0, diagram.getAllConnectedOutputs().size());

        diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        List<OutputReturn> connected = diagram.getAllConnectedOutputs();
        Assert.assertEquals(1, connected.size());
        Assert.assertSame(src.getOutputs().get(0), connected.get(0));
    }

    /* getAllExecutionPathConnections returns correct (source, branchIndex, target) tuples */
    @Test
    public void testGetAllExecutionPathConnections() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);

        Assert.assertEquals(0, diagram.getAllExecutionPathConnections().size());

        diagram.connectExecutionPath(a, 0, b);

        List<Diagram.ExecutionPathConnection> connections = diagram.getAllExecutionPathConnections();
        Assert.assertEquals(1, connections.size());
        Assert.assertSame(a, connections.get(0).source());
        Assert.assertEquals(0, connections.get(0).branchIndex());
        Assert.assertSame(b, connections.get(0).target());
    }

    /* getDiagramNodeByUniqueId finds the right node and returns null for unknown UUIDs */
    @Test
    public void testGetDiagramNodeByUniqueId() {
        DiagramNode node = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(node);

        Assert.assertSame(node, diagram.getDiagramNodeByUniqueId(node.getUniqueId()));
        Assert.assertNull(diagram.getDiagramNodeByUniqueId(UUID.randomUUID()));
    }

    /* Removing a connected node also severs all its execution path connections */
    @Test
    public void testRemoveNodeDisconnectsExecutionPaths() {
        DiagramNode a = makeNode(new ConstantExecutable());
        DiagramNode b = makeNode(new ConstantExecutable());
        diagram.addDiagramNode(a);
        diagram.addDiagramNode(b);
        diagram.connectExecutionPath(a, 0, b);

        diagram.removeDiagramNode(b);

        Assert.assertNull(a.getNodeAfterPath(0).getOther());
        Assert.assertNull(b.getNodeBefore().getOther());
    }

    /* Removing a connected node also severs its data port connections */
    @Test
    public void testRemoveNodeDisconnectsDataPorts() {
        DiagramNode src  = makeNode(new ConstantExecutable());
        DiagramNode dest = makeNode(new RangeCheckExecutable());
        diagram.addDiagramNode(src);
        diagram.addDiagramNode(dest);
        diagram.connectDataPorts(src.getOutputs().get(0), dest.getInputs().get(0));

        diagram.removeDiagramNode(dest);

        Assert.assertNull(src.getOutputs().get(0).getTo());
        Assert.assertNull(dest.getInputs().get(0).getFrom());
    }
}
