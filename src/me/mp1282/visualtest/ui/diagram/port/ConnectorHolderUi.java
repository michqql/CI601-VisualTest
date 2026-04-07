package me.mp1282.visualtest.ui.diagram.port;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.ExecutionPath;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeHolderUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.util.ReadOnlyMap;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/** ConnectorHolderUi is a dedicated UI element to only hold IConnectorUi elements.
 */
public class ConnectorHolderUi extends Pane {

    private final DiagramUi diagramUi;
    private final DiagramNodeHolderUi nodeHolderUi;

    private final Consumer<DataPortConnectorLineUi> onDataPortConnectorAdd;
    private final Consumer<ExecutionPathConnectorLineUi> onExecutionPathConnectorAdd;

    public ConnectorHolderUi(final DiagramUi diagramUi, final DiagramNodeHolderUi nodeHolderUi,
                             final Consumer<DataPortConnectorLineUi> onDataPortConnectorAdd,
                             final Consumer<ExecutionPathConnectorLineUi> onExecutionPathConnectorAdd) {
        this.diagramUi = diagramUi;
        this.nodeHolderUi = nodeHolderUi;
        this.onDataPortConnectorAdd = onDataPortConnectorAdd;
        this.onExecutionPathConnectorAdd = onExecutionPathConnectorAdd;
    }

    public void rebuildConnectors() {
        /* Firstly remove all children, as we are about to recreate them all */
        getChildren().clear();

        rebuildDataPortConnectors();
        rebuildExecutionPathConnectors();
    }

    private void rebuildDataPortConnectors() {
        final ReadOnlyMap<DiagramNode, DiagramNodeUi> nodeToUiMap = nodeHolderUi.getNodeToUiMap();

        /* Loop over each DiagramNode in the Diagram */
        for (DiagramNode node : diagramUi.getDiagram().nodesProperty()) {
            final DiagramNodeUi sourceNodeUi = nodeToUiMap.get(node);
            if(sourceNodeUi == null) /* TODO: If this is null we have a serious problem */
                throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding data ports! (A)");

            /* Loop over each output data port connection for this node
             * (We only need to loop over all output data ports, as these
             *  are connected to another input data port)
             */
            for (OutputReturn output : node.getOutputs()) {
                if(output.getTo() == null)
                    continue;

                createDataPortConnector(output, output.getTo());
            }
        }
    }

    private void rebuildExecutionPathConnectors() {
        final ReadOnlyMap<DiagramNode, DiagramNodeUi> nodeToUiMap = nodeHolderUi.getNodeToUiMap();
        /* Loop over each DiagramNode in the Diagram */
        for (DiagramNode node : diagramUi.getDiagram().nodesProperty()) {
            final DiagramNodeUi currentNodeUi = nodeToUiMap.get(node);
            if(currentNodeUi == null) /* TODO: If this is null we have a serious problem */
                throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding execution path! (A)");

            /* Only need to create an execution path connector ui for each "after" path,
             * because all nodes before that are connected will be connected to a node after.
             */
            final List<ExecutionPath> afterPaths = node.getNodeAfterPaths();
            for (int i = 0; i < afterPaths.size(); i++) {
                if (afterPaths.get(i).getOther() != null) {
                    createExecutionPathConnectorUi(currentNodeUi, i, nodeToUiMap.get(afterPaths.get(i).getOther()));
                }
            }
        }
    }

    private void createDataPortConnector(OutputReturn output, InputParameter input) {
        final ReadOnlyMap<DiagramNode, DiagramNodeUi> nodeToUiMap = nodeHolderUi.getNodeToUiMap();
        final DataPortConnectorLineUi connector = new DataPortConnectorLineUi(
                nodeToUiMap.get(output.getParentNode()), output,
                nodeToUiMap.get(input .getParentNode()), input);

        if(onDataPortConnectorAdd != null)
            onDataPortConnectorAdd.accept(connector);

        getChildren().add(connector);
    }

    public void applyExecutionPathStates(ExecuteTask task) {
        Set<DiagramNode> skipped = task.getSkippedNodes();
        for (Node child : getChildren()) {
            if (child instanceof ExecutionPathConnectorLineUi line) {
                DiagramNode target = line.getTargetNodeUi().getNode();
                boolean isSkipped = skipped.contains(target);
                line.setPathState(isSkipped
                        ? ExecutionPathConnectorLineUi.PathState.SKIPPED
                        : ExecutionPathConnectorLineUi.PathState.TAKEN);
            }
        }
    }

    public void clearExecutionPathStates() {
        for (Node child : getChildren()) {
            if (child instanceof ExecutionPathConnectorLineUi line)
                line.setPathState(ExecutionPathConnectorLineUi.PathState.NEUTRAL);
        }
    }

    private void createExecutionPathConnectorUi(DiagramNodeUi source, int branchIndex, DiagramNodeUi target) {
        final ExecutionPathConnectorLineUi connector = new ExecutionPathConnectorLineUi(source, branchIndex, target);

        if(onExecutionPathConnectorAdd != null)
            onExecutionPathConnectorAdd.accept(connector);

        getChildren().add(connector);
    }
}
