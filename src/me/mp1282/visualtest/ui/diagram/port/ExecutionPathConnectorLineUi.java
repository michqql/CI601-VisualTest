package me.mp1282.visualtest.ui.diagram.port;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.other.ArrowLineUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.List;

public class ExecutionPathConnectorLineUi extends ArrowLineUi implements IConnectorUi, ISelectableUi, IDiagramElement {

    private static final Color DEFAULT_COLOUR  = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.RED;
    private static final int DEFAULT_WIDTH = 2;
    private static final int HOVERED_WIDTH = 7;

    private final DiagramNodeUi sourceNodeUi;
    private final DiagramNodeUi targetNodeUi;

    private final BooleanProperty selected;

    public ExecutionPathConnectorLineUi(DiagramNodeUi sourceNodeUi, DiagramNodeUi targetNodeUi) {
        this.sourceNodeUi = sourceNodeUi;
        this.targetNodeUi = targetNodeUi;

        this.selected = new SimpleBooleanProperty();

        final DiagramNode sourceNode = sourceNodeUi.getNode();
        final DiagramNode targetNode = targetNodeUi.getNode();

        /* When either of the nodes' positions change, recalculate the start and end positions */
        PropertyHelper.addListenerForEach(
                List.of(sourceNode.xProperty(), sourceNode.yProperty(), sourceNode.widthProperty(), sourceNode.heightProperty(),
                        targetNode.xProperty(), targetNode.yProperty(), targetNode.widthProperty(), targetNode.heightProperty()),
                _ -> recalculateStartAndEndPositions());

        /* Calculate default start and end positions */
        recalculateStartAndEndPositions();
//
//        hoverProperty().addListener((_, _, hovered) -> {
//            setStrokeWidth(hovered ? HOVERED_WIDTH : DEFAULT_WIDTH);
//
//            sourceNodeUi.hoveredDataPortProperty().set(hovered ? sourcePort : null);
//            targetNodeUi.hoveredDataPortProperty().set(hovered ? targetPort : null);
//        });

//        selected.addListener((_, _, selected) -> setStroke(selected ? SELECTED_COLOUR : DEFAULT_COLOUR));
    }

    public DiagramNodeUi getSourceNodeUi() {
        return sourceNodeUi;
    }

    public DiagramNodeUi getTargetNodeUi() {
        return targetNodeUi;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean equals(DiagramNodeUi nodeA, DiagramNodeUi nodeB) {
        boolean nodesEqual =
                (sourceNodeUi.equals(nodeA) && targetNodeUi.equals(nodeB)) ||
                        (sourceNodeUi.equals(nodeB) && targetNodeUi.equals(nodeA));
        return nodesEqual;
    }

    private void recalculateStartAndEndPositions() {
        startX.set(sourceNodeUi.getNode().xProperty().get() + sourceNodeUi.getNode().widthProperty ().get() / 2D);
        startY.set(sourceNodeUi.getNode().yProperty().get() + sourceNodeUi.getNode().heightProperty().get() / 2D);
        endX  .set(targetNodeUi.getNode().xProperty().get() + targetNodeUi.getNode().widthProperty ().get() / 2D);
        endY  .set(targetNodeUi.getNode().yProperty().get() + targetNodeUi.getNode().heightProperty().get() / 2D);
    }

    @Override
    public int getZOrder() {
        return 2;
    }
}