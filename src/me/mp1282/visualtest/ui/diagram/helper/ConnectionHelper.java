package me.mp1282.visualtest.ui.diagram.helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Line;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.util.Pair;

public class ConnectionHelper {

    private final DiagramUi diagramUi;
    private final Line tempConnectionLine;

    /* Variables to handle connecting of data/flow ports together */
    private final ObjectProperty<Pair<DiagramNodeUi, DataPortArea>> sourcePortPair;
    private final ObjectProperty<DiagramNodeUi> sourceFlowPort;

    public ConnectionHelper(DiagramUi diagramUi) {
        this.diagramUi = diagramUi;
        this.tempConnectionLine = new Line();

        this.sourcePortPair = new SimpleObjectProperty<>();
        this.sourceFlowPort = new SimpleObjectProperty<>();

        tempConnectionLine.setVisible(false);
        sourcePortPair.addListener((_, _, _) -> redrawConnectingLine());
    }

    public Line getTempConnectionLine() {
        return tempConnectionLine;
    }

    /**
     * Tries to handle connecting of data ports.
     *
     * @param clickedNodeUi The {@code me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi} that was clicked by the user.
     * @return {@code true} if this function handled the request.
     */
    public boolean handleConnecting(DiagramNodeUi clickedNodeUi) {
        /* Check that a data port is being hovered */
        final DataPortArea hoveredPort = clickedNodeUi.hoveredDataPortProperty().get();
        if(hoveredPort != null) {
            /* If no current source data port is set, set this data port as the source.
             * Otherwise, handle 'connecting' the two data ports together.
             */
            final Pair<DiagramNodeUi, DataPortArea> sourcePair = sourcePortPair.get();
            if (sourcePair == null) {
                sourcePortPair.set(new Pair<>(clickedNodeUi, clickedNodeUi.hoveredDataPortProperty().get()));
            } else {
                boolean connectionValid = diagramUi.getDiagram().connectDataPorts(
                        /* Source Node => */ sourcePair.key().getNode(),
                        /* Source Port => */ sourcePair.value().getDataPort(),
                        /* Target Node => */ clickedNodeUi.getNode(),
                        /* Target Port => */ hoveredPort.getDataPort());

                if (connectionValid) {
                    diagramUi.rebuildDataPortConnections();

                    /* Set source variables back to null as they are no longer needed */
                    sourcePortPair.set(null);
                }
            }

            /* Handled this event */
            return true;
        }

        /* Not handled - nothing changed */
        return false;
    }

    public void cancelConnection() {
        sourcePortPair.set(null);
    }

    public void redrawConnectingLine() {
        final Pair<DiagramNodeUi, DataPortArea> sourcePair = sourcePortPair.get();

        if(sourcePair != null) {
            /* Draw line between source data port and mouse cursor */
            tempConnectionLine.setStartX(sourcePair.key().getLayoutX() +
                    sourcePair.key().getTranslateX() + sourcePair.value().getMidX());
            tempConnectionLine.setStartY(sourcePair.key().getLayoutY() +
                    sourcePair.key().getTranslateY() + sourcePair.value().getMidY());
            tempConnectionLine.setEndX(diagramUi.lastMouseX.get());
            tempConnectionLine.setEndY(diagramUi.lastMouseY.get());
        }

        /* Redraw line first before setting visibility to remove visual flicker */
        tempConnectionLine.setVisible(sourcePair != null);
    }
}
