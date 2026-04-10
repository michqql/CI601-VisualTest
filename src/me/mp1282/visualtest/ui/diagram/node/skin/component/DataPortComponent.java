package me.mp1282.visualtest.ui.diagram.node.skin.component;

import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.event.EventBus;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;

public class DataPortComponent extends StackPane {

    private static final double SIDE_LENGTH     = 20;
    private static final double LINE_WIDTH      = 3;

    private static final Color  STROKE_HIDDEN   = Color.TRANSPARENT;
    private static final Color  STROKE_VISIBLE  = Color.BLACK;

    private final SkinBase<DiagramNodeUi> skinParent; /* The control that this UI skin component belongs to */
    private final IDataPort<?> dataPort;

    public DataPortComponent(final SkinBase<DiagramNodeUi> skinParent, final IDataPort<?> dataPort) {
        this.skinParent = skinParent;
        this.dataPort = dataPort;

        /* The background rectangle
         * This isn't visible unless being hovered over, but adds width to the data port component
         * even when not visible. This spaces the data ports out more.
         */
        final Rectangle bg = new Rectangle(SIDE_LENGTH, SIDE_LENGTH);
        bg.setFill(STROKE_HIDDEN);
        bg.setStroke(STROKE_HIDDEN);
        bg.setStrokeWidth(LINE_WIDTH);
        bg.setArcWidth(5);
        bg.setArcHeight(5);
        bg.hoverProperty().addListener((_, _, hover) ->
                bg.setStroke(hover ? STROKE_VISIBLE : STROKE_HIDDEN));

        /* The foreground line that is representing the data port itself */
        final Line fg = new Line(/* X => */ 0, /* Y => */ SIDE_LENGTH / 2, /* End X => */ SIDE_LENGTH, /* End Y => */ SIDE_LENGTH / 2);
        fg.setStrokeWidth(LINE_WIDTH);
        fg.setStroke(Color.RED);
        fg.setMouseTransparent(true);

        /* Capture all mouse events on this pane, and fire them on the DiagramNodeUi */
        addEventHandler(MouseEvent.ANY, event -> {
            final DiagramNodeUi ui = skinParent.getSkinnable();
            if (ui == null) return; /* skin disposed */
            /* Fire generic mouse event */
            ui.fireEvent(new DataPortMouseEvent(DataPortMouseEvent.ANY_MOUSE, ui, dataPort, event));

            /* If was mouse click event, fire mouse click specific event */
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
                ui.fireEvent(new DataPortMouseEvent(DataPortMouseEvent.CLICK, ui, dataPort, event));
        });
        /* When position changes, push new position to the DiagramNodeUi
         * so that the position of this DataPortComponent is known outside
         * the Skin.
         */
        layoutBoundsProperty         ().addListener((_, _, _) -> pushPositionToParent());
        localToSceneTransformProperty().addListener((_, _, _) -> pushPositionToParent());

        getChildren().addAll(bg, fg);
    }

    public IDataPort<?> getDataPort() {
        return dataPort;
    }

    private void pushPositionToParent() {
        DiagramNodeUi ui = skinParent.getSkinnable();
        if (ui == null) return; /* skin is being disposed; ignore stale bounds change */
        Bounds b = localToScene(getBoundsInLocal());
        ui.setDataPortArea(dataPort, b);
    }
}
