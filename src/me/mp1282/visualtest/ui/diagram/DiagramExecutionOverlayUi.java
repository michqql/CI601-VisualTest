package me.mp1282.visualtest.ui.diagram;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeHolderUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.util.ObservableBounds;
import me.mp1282.visualtest.util.ReadOnlyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mouse-transparent overlay pane rendered above the diagram canvas.
 * After execution completes it shows small value labels next to each output
 * data port, and dims nodes that were skipped during execution.
 */
public class DiagramExecutionOverlayUi extends Pane {

    private final Map<OutputReturn, Label> portValueLabels = new HashMap<>();
    private final List<DiagramNodeUi> tintedNodes = new ArrayList<>();

    public DiagramExecutionOverlayUi() {
        setMouseTransparent(true);
        setPickOnBounds(false);
    }

    public void applyExecutionResult(ExecuteTask task, DiagramNodeHolderUi nodeHolderUi) {
        clearOverlay();

        ReadOnlyMap<DiagramNode, DiagramNodeUi> nodeToUiMap = nodeHolderUi.getNodeToUiMap();

        for (DiagramNode node : task.getDiagram().nodesProperty()) {
            DiagramNodeUi nodeUi = nodeToUiMap.get(node);
            if (nodeUi == null) continue;

            /* Tint skipped nodes */
            if (task.getSkippedNodes().contains(node)) {
                nodeUi.setSkipped(true);
                tintedNodes.add(nodeUi);
            }

            /* Place value labels next to each output port */
            for (OutputReturn output : node.getOutputs()) {
                Object value = task.getOutputPortValue(output);
                if (value == null) continue;

                Label label = new Label(value.toString());
                label.setStyle(
                        "-fx-font-size:9;" +
                        "-fx-background-color:rgba(255,255,200,0.9);" +
                        "-fx-padding:1 3 1 3;" +
                        "-fx-background-radius:3;"
                );

                ObservableBounds bounds = nodeUi.getDataPortAreaProperty(output);
                label.layoutXProperty().bind(bounds.centerXProperty().add(4));
                label.layoutYProperty().bind(bounds.minYProperty().subtract(16));

                portValueLabels.put(output, label);
                getChildren().add(label);
            }
        }
    }

    public void clearOverlay() {
        /* Unbind labels before removing to avoid dangling bindings */
        for (Label label : portValueLabels.values()) {
            label.layoutXProperty().unbind();
            label.layoutYProperty().unbind();
        }
        getChildren().clear();
        portValueLabels.clear();

        /* Reset skipped tinting */
        for (DiagramNodeUi nodeUi : tintedNodes) {
            nodeUi.setSkipped(false);
        }
        tintedNodes.clear();
    }
}
