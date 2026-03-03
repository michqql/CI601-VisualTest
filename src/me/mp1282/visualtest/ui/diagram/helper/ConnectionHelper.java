package me.mp1282.visualtest.ui.diagram.helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.ConnectorHolderUi;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;
import me.mp1282.visualtest.ui.other.ArrowLineUi;
import me.mp1282.visualtest.util.MouseDelta;
import me.mp1282.visualtest.util.ObservableBounds;

public class ConnectionHelper {

    private static final Color DATA_PORT_LINE_COLOUR      = Color.GREEN;
    private static final Color EXECUTION_PATH_LINE_COLOUR = Color.GREEN;

    private final DiagramUi diagramUi;
    private final ConnectorHolderUi connectorHolderUi;
    private final Line dataPortConnectionLine;
    private final ArrowLineUi executionPathConnectionLine;

    /* Variables to handle connecting of data/flow ports together */
    private final ObjectProperty<IDataPort<?>> dataPortSource;
    private final ObjectProperty<DiagramNodeUi> executionPathSourceNode;

    public ConnectionHelper(DiagramUi diagramUi, ConnectorHolderUi connectorHolderUi) {
        this.diagramUi                   = diagramUi;
        this.connectorHolderUi           = connectorHolderUi;
        this.dataPortConnectionLine      = new Line();
        this.executionPathConnectionLine = new ArrowLineUi();
        this.dataPortSource              = new SimpleObjectProperty<>();
        this.executionPathSourceNode     = new SimpleObjectProperty<>();

        dataPortConnectionLine.setVisible(false);
        dataPortConnectionLine.setStroke(DATA_PORT_LINE_COLOUR);
        dataPortConnectionLine.setMouseTransparent(true);
        executionPathConnectionLine.setVisible(false);
        executionPathConnectionLine.setColour(EXECUTION_PATH_LINE_COLOUR);
        executionPathConnectionLine.setMouseTransparent(true);

        dataPortSource         .addListener((_, _, _) -> redrawConnectingLine());
        executionPathSourceNode.addListener((_, _, _) -> redrawConnectingLine());
    }

    public Line getDataPortConnectionLine() {
        return dataPortConnectionLine;
    }

    public ArrowLineUi getExecutionPathConnectionLine() {
        return executionPathConnectionLine;
    }

    /**
     * Tries to handle connecting of data ports. <br>
     *
     * @param event The {@link DataPortMouseEvent} that was fire when the data port was clicked by the user.
     * @implNote The {@link javafx.event.Event} will be consumed.
     */
    public void onDataPortComponentClickEvent(DataPortMouseEvent event) {
        /* Consume this event to stop other parts of the code using the click event by accident */
        event.consume();

        /* Ensure the user is not currently connecting an execution path */
        if(executionPathSourceNode.get() != null)
            return;

        /* If no current source data port is set, set this data port as the source.
         * Otherwise, handle connecting the two data ports together.
         */
        if(dataPortSource.get() == null) {
            dataPortSource.set(event.getDataPort());

            /* Bind the position of the bounds to the start of the data port connection line */
            ObservableBounds bounds = event.getDiagramNodeUi().getDataPortAreaProperty(event.getDataPort());
            dataPortConnectionLine.startXProperty().bind(bounds.centerXProperty());
            dataPortConnectionLine.startYProperty().bind(bounds.centerYProperty());
        } else {
            /* The user has previously selected a data port, and now they have just selected another data port */
            final IDataPort<?> previous = dataPortSource.get();
            final IDataPort<?> current  = event.getDataPort();

            boolean connected = false;
            /* The data ports must be of different data types (parameter / return),
             * so check they are different by checking their instanceof type.
             */
            if(previous instanceof OutputReturn output && current instanceof InputParameter input) {
                connected = diagramUi.getDiagram().connectDataPorts(output, input);
            } else if(current instanceof OutputReturn output && previous instanceof InputParameter input) {
                connected = diagramUi.getDiagram().connectDataPorts(output, input);
            }

            if (connected) {
                connectorHolderUi.rebuildConnectors();

                /* Set source variables back to null as they are no longer needed */
                dataPortSource.set(null);
                /* Unbind the start position of the connecting line */
                dataPortConnectionLine.startXProperty().unbind();
                dataPortConnectionLine.startYProperty().unbind();
            }
        }
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
                connectorHolderUi.rebuildConnectors();

                /* Set source variables back to null as they are no longer needed */
                executionPathSourceNode.set(null);
            }
        }

        /* Request handled */
        return true;
    }

    public void cancelConnection() {
        dataPortSource.set(null);
        /* Unbind the start position of the connecting line */
        dataPortConnectionLine.startXProperty().unbind();
        dataPortConnectionLine.startYProperty().unbind();

        executionPathSourceNode.set(null);
    }

    public void redrawConnectingLine() {
        final DiagramNodeUi executionPathNodeUi = executionPathSourceNode.get();
        final Point2D pos = diagramUi.sceneToLocal(MouseDelta.lastSceneMouseX.get(), MouseDelta.lastSceneMouseY.get());

        if(dataPortSource.get() != null) {
            /* Draw line between source data port and mouse cursor
             * The data port connection line's start position is set when the data port is initially clicked
             */
            dataPortConnectionLine.setEndX(pos.getX());
            dataPortConnectionLine.setEndY(pos.getY());

        } else if(executionPathNodeUi != null) {
            final DiagramNode node = executionPathNodeUi.getNode();

            /* Draw line between source node and mouse cursor */
            executionPathConnectionLine.setStartX(executionPathNodeUi.getTranslateX() + node.xProperty().get() + (node.widthProperty().get() / 2));
            executionPathConnectionLine.setStartY(executionPathNodeUi.getTranslateY() + node.yProperty().get() + (node.heightProperty().get() / 2));
            executionPathConnectionLine.setEndX  (pos.getX());
            executionPathConnectionLine.setEndY  (pos.getY());
        }

        /* Redraw line first before setting visibility to remove visual flicker */
        dataPortConnectionLine.setVisible(dataPortSource.get() != null);
        executionPathConnectionLine.setVisible(executionPathNodeUi != null);
    }
}
