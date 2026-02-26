package me.mp1282.visualtest.ui.diagram.node.skin.component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import me.mp1282.visualtest.system.diagram.node.IDataPort;

public class DataPortComponent extends StackPane {

    private static final double SIDE_LENGTH     = 20;
    private static final double LINE_WIDTH      = 3;

    private static final Color  STROKE_HIDDEN   = Color.TRANSPARENT;
    private static final Color  STROKE_VISIBLE  = Color.BLACK;

    private final IDataPort<?> dataPort;

    public DataPortComponent(IDataPort<?> dataPort) {
        this.dataPort = dataPort;

        final Rectangle bg = new Rectangle(SIDE_LENGTH, SIDE_LENGTH);
        bg.setFill(STROKE_HIDDEN);
        bg.setStroke(STROKE_HIDDEN);
        bg.setStrokeWidth(LINE_WIDTH);
        bg.hoverProperty().addListener((_, _, hover) -> bg.setStroke(hover ? STROKE_VISIBLE : STROKE_HIDDEN));

        final Line fg = new Line(/* X => */ 0, /* Y => */ 0, /* End X => */ 0, /* End Y => */ SIDE_LENGTH);
        fg.setStrokeWidth(LINE_WIDTH);
        fg.setStroke(Color.RED);
        fg.setMouseTransparent(true);

        getChildren().addAll(bg, fg);
    }

    public IDataPort<?> getDataPort() {
        return dataPort;
    }
}
