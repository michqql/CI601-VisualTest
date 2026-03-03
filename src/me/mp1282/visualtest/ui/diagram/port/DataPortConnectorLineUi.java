package me.mp1282.visualtest.ui.diagram.port;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.ObservableBounds;

public class DataPortConnectorLineUi extends Line implements IConnectorUi, ISelectableUi, IDiagramElement {

    private static final Color DEFAULT_COLOUR  = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.RED;
    private static final int DEFAULT_WIDTH = 2;
    private static final int HOVERED_WIDTH = 7;

    private final DiagramNodeUi outputUi;
    private final DiagramNodeUi inputUi;
    private final OutputReturn   output;
    private final InputParameter input;

    private final BooleanProperty selected;

    public DataPortConnectorLineUi(DiagramNodeUi outputUi, OutputReturn   output,
                                   DiagramNodeUi inputUi,  InputParameter input) {

        this.outputUi = outputUi;
        this.inputUi  = inputUi;
        this.output   = output;
        this.input    = input;

        this.selected = new SimpleBooleanProperty();

        setStroke(DEFAULT_COLOUR);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setStrokeWidth(DEFAULT_WIDTH);

        /* Bind the start and end positions of the line to the
         * positions of the source and target ports
         */
        final ObservableBounds outputBounds = outputUi.getDataPortAreaProperty(output);
        final ObservableBounds inputBounds  = inputUi .getDataPortAreaProperty(input);

        startXProperty().bind(outputBounds.centerXProperty());
        startYProperty().bind(outputBounds.centerYProperty());
        endXProperty  ().bind(inputBounds .centerXProperty ());
        endYProperty  ().bind(inputBounds .centerYProperty());

//        sourceNodeUi.getDataPortAreaProperty(sourcePort).addListener((_, _, bounds) -> {
//            startXProperty().set(bounds.getCenterX());
//            startYProperty().set(bounds.getCenterY());
//        });
//        targetNodeUi.getDataPortAreaProperty(targetPort).addListener((_, _, bounds) -> {
//            endXProperty().set(bounds.getCenterX());
//            endYProperty().set(bounds.getCenterY());
//        });

        hoverProperty().addListener((_, _, hovered) -> {
            setStrokeWidth(hovered ? HOVERED_WIDTH : DEFAULT_WIDTH);

//            outputUi.hoveredDataPortProperty().set(hovered ? sourcePort : null);
//            targetNodeUi.hoveredDataPortProperty().set(hovered ? targetPort : null);
        });

        selected.addListener((_, _, selected) -> setStroke(selected ? SELECTED_COLOUR : DEFAULT_COLOUR));
    }

    public DiagramNodeUi getOutputUi() {
        return outputUi;
    }

    public DiagramNodeUi getInputUi() {
        return inputUi;
    }

    public IDataPort<ReturnType> getOutputPort() {
        return output;
    }

    public IDataPort<ParameterType> getInputPort() {
        return input;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

//    public boolean equals(DiagramNodeUi nodeA, DataPortArea areaA,
//                          DiagramNodeUi nodeB, DataPortArea areaB) {
//        boolean nodesEqual =
//                (outputUi.equals(nodeA) && inputUi.equals(nodeB)) ||
//                (outputUi.equals(nodeB) && inputUi.equals(nodeA));
//
//        boolean portsEqual =
//                (sourcePort.equals(areaA) && targetPort.equals(areaB)) ||
//                (sourcePort.equals(areaB) && targetPort.equals(areaA));
//
//        return nodesEqual && portsEqual;
//    }

    @Override
    public int getZOrder() {
        return 1;
    }
}
