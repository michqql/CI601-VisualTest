package me.mp1282.visualtest.ui.diagram.port;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;

public class DataPortConnectorLineUi extends Line implements IConnectorUi, ISelectableUi, IDiagramElement {

    private static final Color DEFAULT_COLOUR  = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.RED;
    private static final int DEFAULT_WIDTH = 2;
    private static final int HOVERED_WIDTH = 7;

    private final DiagramNodeUi sourceNodeUi;
    private final DiagramNodeUi targetNodeUi;
    private final DataPortArea sourcePort;
    private final DataPortArea targetPort;

    private final BooleanProperty selected;

    public DataPortConnectorLineUi(DiagramNodeUi sourceNodeUi, DataPortArea sourcePort,
                                   DiagramNodeUi targetNodeUi, DataPortArea targetPort) {

        this.sourceNodeUi = sourceNodeUi;
        this.targetNodeUi = targetNodeUi;
        this.sourcePort   = sourcePort;
        this.targetPort   = targetPort;

        this.selected = new SimpleBooleanProperty();

        setStroke(DEFAULT_COLOUR);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setStrokeWidth(DEFAULT_WIDTH);

        final DiagramNode sourceNode = sourceNodeUi.getNode();
        final DiagramNode targetNode = targetNodeUi.getNode();

        /* Bind the start and end positions of the line to the
         * positions of the source and target ports
         */
        startXProperty().bind(sourceNode.xProperty().add(sourcePort.getMidX()));
        startYProperty().bind(sourceNode.yProperty().add(sourcePort.getMidY()));
        endXProperty  ().bind(targetNode.xProperty().add(targetPort.getMidX()));
        endYProperty  ().bind(targetNode.yProperty().add(targetPort.getMidY()));

        hoverProperty().addListener((_, _, hovered) -> {
            setStrokeWidth(hovered ? HOVERED_WIDTH : DEFAULT_WIDTH);

            sourceNodeUi.hoveredDataPortProperty().set(hovered ? sourcePort : null);
            targetNodeUi.hoveredDataPortProperty().set(hovered ? targetPort : null);
        });

        selected.addListener((_, _, selected) -> setStroke(selected ? SELECTED_COLOUR : DEFAULT_COLOUR));
    }

    public DiagramNodeUi getSourceNodeUi() {
        return sourceNodeUi;
    }

    public DiagramNodeUi getTargetNodeUi() {
        return targetNodeUi;
    }

    public DataPortArea getSourcePort() {
        return sourcePort;
    }

    public DataPortArea getTargetPort() {
        return targetPort;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean equals(DiagramNodeUi nodeA, DataPortArea areaA,
                          DiagramNodeUi nodeB, DataPortArea areaB) {
        boolean nodesEqual =
                (sourceNodeUi.equals(nodeA) && targetNodeUi.equals(nodeB)) ||
                (sourceNodeUi.equals(nodeB) && targetNodeUi.equals(nodeA));

        boolean portsEqual =
                (sourcePort.equals(areaA) && targetPort.equals(areaB)) ||
                (sourcePort.equals(areaB) && targetPort.equals(areaA));

        return nodesEqual && portsEqual;
    }

    @Override
    public int getZOrder() {
        return 1;
    }
}
