package me.mp1282.visualtest.ui.diagram.helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.diagram.port.ExecutionPathConnectorLineUi;
import me.mp1282.visualtest.ui.other.ArrowLineUi;
import me.mp1282.visualtest.util.MouseDelta;
import me.mp1282.visualtest.util.Pair;

public class ConnectionHelper {

    private static final Color DATA_PORT_LINE_COLOUR      = Color.GREEN;
    private static final Color EXECUTION_PATH_LINE_COLOUR = Color.GREEN;

    private final DiagramUi diagramUi;
    private final Line tempConnectionLine;
    private final ArrowLineUi tempExecutionPathLine;

    /* Variables to handle connecting of data/flow ports together */
    private final ObjectProperty<Pair<DiagramNodeUi, DataPortArea>> dataPortSource;
    private final ObjectProperty<DiagramNodeUi> executionPathSourceNode;

    public ConnectionHelper(DiagramUi diagramUi) {
        this.diagramUi               = diagramUi;
        this.tempConnectionLine      = new Line();
        this.tempExecutionPathLine   = new ArrowLineUi();
        this.dataPortSource          = new SimpleObjectProperty<>();
        this.executionPathSourceNode = new SimpleObjectProperty<>();

        tempConnectionLine   .setVisible(false);
        tempConnectionLine   .setStroke(DATA_PORT_LINE_COLOUR);
        tempConnectionLine.setMouseTransparent(true);
        tempExecutionPathLine.setVisible(false);
        tempExecutionPathLine.setColour(EXECUTION_PATH_LINE_COLOUR);
        tempExecutionPathLine.setMouseTransparent(true);

        dataPortSource         .addListener((_, _, _) -> redrawConnectingLine());
        executionPathSourceNode.addListener((_, _, _) -> redrawConnectingLine());
    }

    public Line getTempConnectionLine() {
        return tempConnectionLine;
    }

    public ArrowLineUi getTempExecutionPathLine() {
        return tempExecutionPathLine;
    }

    /**
     * Tries to handle connecting of data ports.
     *
     * @param clickedNodeUi The {@code me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi} that was clicked by the user.
     * @return {@code true} if this function handled the request.
     */
    public boolean handleDataPortConnecting(DiagramNodeUi clickedNodeUi) {
        /* Ensure that the user is not currently connecting an execution path */
        if(executionPathSourceNode.get() != null)
            return false;

        /* Check that a data port is being hovered */
        final DataPortArea hoveredPort = clickedNodeUi.hoveredDataPortProperty().get();
        if(hoveredPort == null)
            return false;

        /* If no current source data port is set, set this data port as the source.
         * Otherwise, handle 'connecting' the two data ports together.
         */
        final Pair<DiagramNodeUi, DataPortArea> sourcePair = dataPortSource.get();
        if (sourcePair == null) {
            dataPortSource.set(new Pair<>(clickedNodeUi, clickedNodeUi.hoveredDataPortProperty().get()));
        } else {
            boolean connected = diagramUi.getDiagram().connectDataPorts(
                    /* Source Node => */ sourcePair.key().getNode(),
                    /* Source Port => */ sourcePair.value().getDataPort(),
                    /* Target Node => */ clickedNodeUi.getNode(),
                    /* Target Port => */ hoveredPort.getDataPort());

            if (connected) {
                diagramUi.rebuildDataPortConnections();

                /* Set source variables back to null as they are no longer needed */
                dataPortSource.set(null);
            }
        }

        /* Handled this event */
        return true;
    }

    /**
     * Tries to handle connecting the execution path of nodes.
     *
     * @param nodeUi The {@code me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi} that was clicked by the user
     *               or the action was initiated against.
     * @return {@code true} if this function handled the request.
     */
    public boolean handleExecutionPathConnecting(DiagramNodeUi nodeUi, boolean initial) {
        /* Ensure that the user is not currently connecting data ports */
        if(dataPortSource.get() != null)
            return false;

        /* Ensure that if initial is false, an execution path source node is already provided */
        if(!initial && executionPathSourceNode.get() == null)
            return false;

        /* Ensure a data port is NOT being hovered */
        if(nodeUi.hoveredDataPortProperty().get() != null)
            return false;

        /* If the source node for the execution path is currently null, set it.
         * Otherwise, create the execution path connection between the two nodes.
         */
        if(executionPathSourceNode.get() == null) {
            executionPathSourceNode.set(nodeUi);
        } else {
            final DiagramNode source = executionPathSourceNode.get().getNode();
            final DiagramNode target = nodeUi.getNode();

            boolean connected = diagramUi.getDiagram().connectExecutionPath(source, target);

            if(connected) {
                diagramUi.rebuildExecutionPathConnections();

                /* Set source variables back to null as they are no longer needed */
                executionPathSourceNode.set(null);
            }
        }

        /* Request handled */
        return true;
    }

    public void cancelConnection() {
        dataPortSource.set(null);
        executionPathSourceNode.set(null);
    }

    public void redrawConnectingLine() {
        final Pair<DiagramNodeUi, DataPortArea> sourcePair = dataPortSource.get();
        final DiagramNodeUi executionPathNodeUi = executionPathSourceNode.get();
        final Point2D pos = diagramUi.sceneToLocal(MouseDelta.lastSceneMouseX.get(), MouseDelta.lastSceneMouseY.get());

        if(sourcePair != null) {
            /* Draw line between source data port and mouse cursor */
            tempConnectionLine.setStartX(sourcePair.key().getLayoutX() +
                    sourcePair.key().getTranslateX() + sourcePair.value().getMidX());
            tempConnectionLine.setStartY(sourcePair.key().getLayoutY() +
                    sourcePair.key().getTranslateY() + sourcePair.value().getMidY());
            tempConnectionLine.setEndX(pos.getX());
            tempConnectionLine.setEndY(pos.getY());

        } else if(executionPathNodeUi != null) {
            final DiagramNode node = executionPathNodeUi.getNode();

            /* Draw line between source node and mouse cursor */
            tempExecutionPathLine.setStartX(executionPathNodeUi.getTranslateX() + node.xProperty().get() + (node.widthProperty().get() / 2));
            tempExecutionPathLine.setStartY(executionPathNodeUi.getTranslateY() + node.yProperty().get() + (node.heightProperty().get() / 2));
            tempExecutionPathLine.setEndX  (pos.getX());
            tempExecutionPathLine.setEndY  (pos.getY());
        }

        /* Redraw line first before setting visibility to remove visual flicker */
        tempConnectionLine   .setVisible(sourcePair          != null);
        tempExecutionPathLine.setVisible(executionPathNodeUi != null);
    }
}
