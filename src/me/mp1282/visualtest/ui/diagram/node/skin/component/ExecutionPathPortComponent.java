package me.mp1282.visualtest.ui.diagram.node.skin.component;

import javafx.geometry.Bounds;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.event.ExecutionPathPortMouseEvent;

public class ExecutionPathPortComponent extends StackPane {

    private static final double SIDE_LENGTH    = 20;
    private static final double PAD            = 3;
    private static final double LINE_WIDTH     = 3;

    private static final Color  STROKE_HIDDEN  = Color.TRANSPARENT;
    private static final Color  STROKE_VISIBLE = Color.BLACK;

    private final SkinBase<DiagramNodeUi> skinParent;
    private final int branchIndex;

    public ExecutionPathPortComponent(final SkinBase<DiagramNodeUi> skinParent, final int branchIndex) {
        this.skinParent  = skinParent;
        this.branchIndex = branchIndex;

        /* The background rectangle
         * Invisible unless hovered; adds consistent width for spacing.
         */
        final Rectangle bg = new Rectangle(SIDE_LENGTH, SIDE_LENGTH);
        bg.setFill(STROKE_HIDDEN);
        bg.setStroke(STROKE_HIDDEN);
        bg.setStrokeWidth(LINE_WIDTH);
        bg.setArcWidth(5);
        bg.setArcHeight(5);
        bg.hoverProperty().addListener((_, _, hover) ->
                bg.setStroke(hover ? STROKE_VISIBLE : STROKE_HIDDEN));

        /* Triangle indicator: pointing UP for IN ports, pointing DOWN for OUT ports */
        final Polygon fg = new Polygon(
                PAD, PAD,
                SIDE_LENGTH - PAD, PAD,
                SIDE_LENGTH / 2, SIDE_LENGTH - PAD
        );
        fg.setFill(Color.BLACK);
        fg.setMouseTransparent(true);

        /* Capture all mouse events on this pane, and fire them on the DiagramNodeUi */
        addEventHandler(MouseEvent.ANY, event -> {
            final DiagramNodeUi ui = skinParent.getSkinnable();
            ui.fireEvent(new ExecutionPathPortMouseEvent(ExecutionPathPortMouseEvent.ANY_MOUSE, ui, branchIndex, event));

            if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
                ui.fireEvent(new ExecutionPathPortMouseEvent(ExecutionPathPortMouseEvent.CLICK, ui, branchIndex, event));
        });

        /* When position changes, push new position to DiagramNodeUi */
        layoutBoundsProperty         ().addListener((_, _, _) -> pushPositionToParent());
        localToSceneTransformProperty().addListener((_, _, _) -> pushPositionToParent());

        getChildren().addAll(bg, fg);
    }

    private void pushPositionToParent() {
        Bounds b = localToScene(getBoundsInLocal());
        skinParent.getSkinnable().setExecutionPathPortArea(branchIndex, b);
    }
}
