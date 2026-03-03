package me.mp1282.visualtest.ui.diagram.port;

import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeHolderUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.util.ReadOnlyMap;

import java.util.HashSet;
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
        final Set<DiagramNode> visited = new HashSet<>(); /* The set of visited nodes */

        /* Loop over each DiagramNode in the Diagram */
        for (DiagramNode node : diagramUi.getDiagram().nodesProperty()) {
            final DiagramNodeUi currentNodeUi = nodeToUiMap.get(node);
            if(currentNodeUi == null) /* TODO: If this is null we have a serious problem */
                throw new RuntimeException("DiagramNode does not have a DiagramNodeUi when rebuilding execution path! (A)");

            /* Only need to create an execution path connector ui for each node after,
             * because all nodes before that are connected will be connected to a node after.
             */
            if(node.getNodeAfter().getOther() != null) {
                createExecutionPathConnectorUi(nodeToUiMap.get(node.getNodeAfter().getOther()), currentNodeUi);
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

    private void createExecutionPathConnectorUi(DiagramNodeUi before, DiagramNodeUi after) {
        final ExecutionPathConnectorLineUi connector = new ExecutionPathConnectorLineUi(before, after);

        if(onExecutionPathConnectorAdd != null)
            onExecutionPathConnectorAdd.accept(connector);

        getChildren().add(connector);
    }
}
