package me.mp1282.visualtest.ui.diagram.port;

import javafx.beans.property.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.other.ArrowLineUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.ObservableBounds;

import java.util.function.Consumer;

public class ExecutionPathConnectorLineUi extends ArrowLineUi implements IConnectorUi, ISelectableUi, IDiagramElement {

    private static final Color DEFAULT_COLOUR  = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.RED;
    private static final int DEFAULT_WIDTH = 5;
    private static final int HOVERED_WIDTH = 10;

    private final DiagramNodeUi sourceNodeUi;
    private final DiagramNodeUi targetNodeUi;
    private final int branchIndex;

    private final BooleanProperty selected;

    private Consumer<MouseEvent> mouseClickConsumer;

    public ExecutionPathConnectorLineUi(DiagramNodeUi sourceNodeUi, int branchIndex, DiagramNodeUi targetNodeUi) {
        this.sourceNodeUi = sourceNodeUi;
        this.targetNodeUi = targetNodeUi;
        this.branchIndex  = branchIndex;

        this.selected = new SimpleBooleanProperty();

        /* Bind start/end positions to the execution path port areas on each node */
        final ObservableBounds src = sourceNodeUi.getExecutionPathPortAreaProperty(branchIndex);
        final ObservableBounds tgt = targetNodeUi.getExecutionPathPortAreaProperty(-1);
        startXProperty().bind(src.centerXProperty());
        startYProperty().bind(src.centerYProperty());
        endXProperty()  .bind(tgt.centerXProperty());
        endYProperty()  .bind(tgt.centerYProperty());

//        hoverProperty().addListener((_, _, hovered) -> {
//            setStrokeWidth(hovered ? HOVERED_WIDTH : DEFAULT_WIDTH);
//
//            sourceNodeUi.hoveredDataPortProperty().set(hovered ? sourcePort : null);
//            targetNodeUi.hoveredDataPortProperty().set(hovered ? targetPort : null);
//        });

        selected.addListener((_, _, selected) -> setColour(selected ? SELECTED_COLOUR : DEFAULT_COLOUR));
    }

    public int getBranchIndex() {
        return branchIndex;
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

    @Override
    public final void onHover(boolean hovering) {
        setWidth(hovering ? HOVERED_WIDTH : DEFAULT_WIDTH);
    }

    @Override
    protected void onClick(MouseEvent e) {
        /* Create a new mouse event with the same properties to set the source
         * to this object instead of the underlying Line that is used to capture
         * the mouse click.
         */
        MouseEvent newEvent = new MouseEvent(
                this, e.getTarget(), MouseEvent.MOUSE_PRESSED,
                e.getX(), e.getY(), e.getScreenX(), e.getScreenY(),
                e.getButton(), e.getClickCount(),
                e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown(),
                e.isPrimaryButtonDown(), e.isMiddleButtonDown(), e.isSecondaryButtonDown(),
                e.isBackButtonDown(), e.isForwardButtonDown(), e.isSynthesized(), e.isPopupTrigger(),
                e.isStillSincePress(), e.getPickResult()
        );

        if(mouseClickConsumer != null)
            mouseClickConsumer.accept(newEvent);

        /* Consume the original event if the new event was consumed */
        if(newEvent.isConsumed())
            e.consume();
    }

    public void setMouseClickConsumer(Consumer<MouseEvent> mouseClickConsumer) {
        this.mouseClickConsumer = mouseClickConsumer;
    }
}