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

    private final OutputReturn output;
    private final BooleanProperty selected;

    public DataPortConnectorLineUi(final DiagramNodeUi outputUi, final OutputReturn   output,
                                   final DiagramNodeUi inputUi,  final InputParameter input) {
        this.output = output;
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

        hoverProperty().addListener((_, _, hovered) -> {
            setStrokeWidth(hovered ? HOVERED_WIDTH : DEFAULT_WIDTH);

            /* When hovering over this line, inform the UI elements that
             * the data port is also being hovered, to give a nice
             * visual indication of which data ports this line is connecting
             */
            outputUi.hoveredDataPortProperty().set(hovered ? output : null);
            inputUi .hoveredDataPortProperty().set(hovered ? input  : null);
        });

        selected.addListener((_, _, selected) -> setStroke(selected ? SELECTED_COLOUR : DEFAULT_COLOUR));
    }

    public OutputReturn getOutput() {
        return output;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }
}
