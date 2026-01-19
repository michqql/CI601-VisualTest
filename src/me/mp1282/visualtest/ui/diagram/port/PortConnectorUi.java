package me.mp1282.visualtest.ui.diagram.port;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import me.mp1282.visualtest.system.diagram.DiagramNode;

public class PortConnectorUi extends Line {

    private static final Color DEFAULT_COLOUR = Color.BLACK;
    private static final int DEFAULT_WIDTH = 2;
    private static final int HOVERED_WIDTH = 7;

    public PortConnectorUi(DiagramNode sourceNode, DataPortArea sourcePort,
                           DiagramNode targetNode, DataPortArea targetPort) {

        setStroke(DEFAULT_COLOUR);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setStrokeWidth(DEFAULT_WIDTH);

        /* Bind the start and end positions of the line to the
         * positions of the source and target ports
         */
        startXProperty().bind(sourceNode.xProperty().add(sourcePort.getMidX()));
        startYProperty().bind(sourceNode.yProperty().add(sourcePort.getMidY()));
        endXProperty().bind(targetNode.xProperty().add(targetPort.getMidX()));
        endYProperty().bind(targetNode.yProperty().add(targetPort.getMidY()));

        hoverProperty().addListener((obs, old, newValue) ->
                setStrokeWidth(newValue ? HOVERED_WIDTH : DEFAULT_WIDTH));
    }
}
