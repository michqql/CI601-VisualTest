package me.mp1282.visualtest.ui.diagram.helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.ConnectorHolderUi;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;
import me.mp1282.visualtest.ui.event.ExecutionPathPortMouseEvent;
import me.mp1282.visualtest.ui.other.ArrowLineUi;
import me.mp1282.visualtest.util.MouseDelta;
import me.mp1282.visualtest.util.ObservableBounds;

public class ConnectionHelper {

    private static final Color DATA_PORT_LINE_COLOUR         = Color.GREEN;
    private static final Color DATA_PORT_INVALID_LINE_COLOUR = Color.RED;
    private static final Color EXECUTION_PATH_LINE_COLOUR    = Color.GREEN;

    private final DiagramUi diagramUi;
    private ConnectorHolderUi connectorHolderUi;
    private final Line dataPortConnectionLine;
    private final ArrowLineUi executionPathConnectionLine;

    /* Variables to handle connecting of data/flow ports together */
    private final ObjectProperty<IDataPort<?>> dataPortSource;
    private final ObjectProperty<DiagramNodeUi> executionPathSourceNode;
    private int pendingBranchIndex = 0;

    /* The data port currently under the cursor — used to colour the preview line */
    private IDataPort<?> hoveredPort = null;

    public ConnectionHelper(DiagramUi diagramUi) {
        this.diagramUi                   = diagramUi;
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

    public void setConnectorHolderUi(ConnectorHolderUi connectorHolderUi) {
        this.connectorHolderUi = connectorHolderUi;
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
                hoveredPort = null;
                dataPortSource.set(null);
                dataPortConnectionLine.setStroke(DATA_PORT_LINE_COLOUR);
                /* Unbind the start position of the connecting line */
                dataPortConnectionLine.startXProperty().unbind();
                dataPortConnectionLine.startYProperty().unbind();
            }
        }
    }



    /**
     * Tries to handle connecting execution paths via port component click events.
     *
     * @param event The {@link ExecutionPathPortMouseEvent} fired when a port was clicked.
     */
    public void onExecutionPathPortClickEvent(ExecutionPathPortMouseEvent event) {
        event.consume();

        if (dataPortSource.get() != null)
            return;

        final DiagramNodeUi ui = event.getDiagramNodeUi();

        if (event.isIncoming()) {
            /* IN port clicked — complete a pending connection */
            if (executionPathSourceNode.get() == null)
                return;
            handleExecutionPathConnecting(ui, false, pendingBranchIndex);
        } else {
            /* OUT port clicked — start (or restart) a connection */
            executionPathSourceNode.set(null);
            handleExecutionPathConnecting(ui, true, event.getBranchIndex());
        }
    }

    /**
     * Tries to handle connecting the execution path of nodes using branch index 0.
     *
     * @param nodeUi The {@code me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi} that was clicked.
     * @return {@code true} if this function handled the request.
     */
    public boolean handleExecutionPathConnecting(DiagramNodeUi nodeUi, boolean initial) {
        return handleExecutionPathConnecting(nodeUi, initial, 0);
    }

    /**
     * Tries to handle connecting the execution path of nodes.
     *
     * @param nodeUi      The node that was clicked by the user or the action was initiated against.
     * @param initial     {@code true} when starting a new connection, {@code false} when completing one.
     * @param branchIndex The execution path output index on the source node to connect.
     * @return {@code true} if this function handled the request.
     */
    public boolean handleExecutionPathConnecting(DiagramNodeUi nodeUi, boolean initial, int branchIndex) {
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
            pendingBranchIndex = branchIndex;
            executionPathSourceNode.set(nodeUi);
        } else {
            final DiagramNode source = executionPathSourceNode.get().getNode();
            final DiagramNode target = nodeUi.getNode();

            boolean connected = diagramUi.getDiagram().connectExecutionPath(source, pendingBranchIndex, target);

            if(connected) {
                connectorHolderUi.rebuildConnectors();

                /* Set source variables back to null as they are no longer needed */
                executionPathSourceNode.set(null);
            }
        }

        /* Request handled */
        return true;
    }

    /** Called by DiagramUi when the cursor enters or leaves a data port during any operation. */
    public void setHoveredPort(IDataPort<?> port) {
        hoveredPort = port;
        updateLineColor();
    }

    private void updateLineColor() {
        if (dataPortSource.get() == null || hoveredPort == null) {
            dataPortConnectionLine.setStroke(DATA_PORT_LINE_COLOUR);
            return;
        }
        dataPortConnectionLine.setStroke(
                isHoverCompatible() ? DATA_PORT_LINE_COLOUR : DATA_PORT_INVALID_LINE_COLOUR);
    }

    /** Returns true if the hovered port is a type-compatible target for the current source. */
    private boolean isHoverCompatible() {
        IDataPort<?> src = dataPortSource.get();
        IDataPort<?> tgt = hoveredPort;
        if (src == null || tgt == null || src == tgt) return true;

        OutputReturn output;
        InputParameter input;
        if (src instanceof OutputReturn o && tgt instanceof InputParameter i) {
            output = o; input = i;
        } else if (src instanceof InputParameter i && tgt instanceof OutputReturn o) {
            output = o; input = i;
        } else {
            return false; // output→output or input→input
        }
        return Diagram.areDataTypesCompatible(output.getType().getDataType(), input.getType().getDataType());
    }

    public void cancelConnection() {
        hoveredPort = null;
        dataPortSource.set(null);
        dataPortConnectionLine.setStroke(DATA_PORT_LINE_COLOUR);
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
            /* Draw line between source OUT port and mouse cursor */
            ObservableBounds portBounds = executionPathNodeUi.getExecutionPathPortAreaProperty(pendingBranchIndex);
            executionPathConnectionLine.setStartX(portBounds.centerXProperty().get());
            executionPathConnectionLine.setStartY(portBounds.centerYProperty().get());
            executionPathConnectionLine.setEndX  (pos.getX());
            executionPathConnectionLine.setEndY  (pos.getY());
        }

        /* Redraw line first before setting visibility to remove visual flicker */
        dataPortConnectionLine.setVisible(dataPortSource.get() != null);
        executionPathConnectionLine.setVisible(executionPathNodeUi != null);
        updateLineColor();
    }
}
