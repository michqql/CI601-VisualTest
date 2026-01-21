package me.mp1282.visualtest.ui.diagram.port;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.List;

public class ExecutionPathConnectorLineUi extends Group implements IConnectorUi, ISelectableUi, IDiagramElement {

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

        System.out.println(
                "Source: " + sourceNodeUi.getNode().getExecutable().getName() +
                ", Target: " + targetNodeUi.getNode().getExecutable().getName());

        this.selected = new SimpleBooleanProperty();

        final DiagramNode sourceNode = sourceNodeUi.getNode();
        final DiagramNode targetNode = targetNodeUi.getNode();

        /* When either of the nodes' positions change, rebuild the arrow line */
        PropertyHelper.addListenerForEach(
                List.of(sourceNode.xProperty(), sourceNode.yProperty(), sourceNode.widthProperty(), sourceNode.heightProperty(),
                        targetNode.xProperty(), targetNode.yProperty(), targetNode.widthProperty(), targetNode.heightProperty()),
                _ -> rebuildArrowLine());

        /* Build the arrow line initially */
        rebuildArrowLine();
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

    private void rebuildArrowLine() {
        getChildren().clear();

        final double startX = sourceNodeUi.getNode().xProperty().get() + sourceNodeUi.getNode().widthProperty ().get() / 2D;
        final double startY = sourceNodeUi.getNode().yProperty().get() + sourceNodeUi.getNode().heightProperty().get() / 2D;
        final double endX   = targetNodeUi.getNode().xProperty().get() + targetNodeUi.getNode().widthProperty ().get() / 2D;
        final double endY   = targetNodeUi.getNode().yProperty().get() + targetNodeUi.getNode().heightProperty().get() / 2D;

        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx); /* Angle in radians */

        final double arrowSize = 5;
        final int arrowCount = Math.max(2, (int) length / 10);
        final double arrowSpacing = length / arrowCount;

        for (int i = 0; i < arrowCount; i++) {
            double x = startX + (i * arrowSpacing) * Math.cos(angle);
            double y = startY + (i * arrowSpacing) * Math.sin(angle);

            Polygon triangle = new Polygon(
                    /* Point A => */ 0, 0,
                    /* Point B => */ -arrowSize,  arrowSize / 2,
                    /* Point C => */ -arrowSize, -arrowSize / 2
            );
            triangle.setFill(Color.BLACK);

            triangle.setTranslateX(x);
            triangle.setTranslateY(y);
            triangle.setRotate(Math.toDegrees(angle));

            getChildren().add(triangle);
        }
    }

    @Override
    public int getZOrder() {
        return 2;
    }
}