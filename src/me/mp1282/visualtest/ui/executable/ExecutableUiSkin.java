package me.mp1282.visualtest.ui.executable;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;

import java.util.List;

public class ExecutableUiSkin extends SkinBase<ExecutableUi> {

    public static final int TOP_BOTTOM_BOX_PADDING = 20;
    public static final int BOX_SIDE_LENGTH = 20;
    public static final int DATA_PORT_LENGTH = 10;

    protected final Canvas canvas;

    public ExecutableUiSkin(ExecutableUi executableUi) {
        super(executableUi);

        this.canvas = new Canvas(executableUi.getWidth(), executableUi.getHeight());

        /* Ensure the canvas cannot capture mouse events */
        canvas.setMouseTransparent(true);
        /* Ensure the canvas is always the same size as the control we are skinning */
        canvas.widthProperty().bind(executableUi.widthProperty()); /* canvas.width = ui.width */
        canvas.heightProperty().bind(executableUi.heightProperty());

        getChildren().add(canvas);

        /* Draw the skin */
        redraw();
        /* Redraw the skin when requested */
        executableUi.requestRedrawProperty.addListener((_, _, _) -> redraw());

    }

    private void redraw() {
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth() + 1, canvas.getHeight() + 1);
        draw(gc);
    }

    protected void draw(final GraphicsContext gc) {
        final ExecutableUi ui = getSkinnable();
        final Executable exe = ui.getExecutable();

        /* Draw executable rectangle outline */
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(0);
        gc.setLineWidth(2);
        drawOutline(gc);

        /* Draw the name of the executable centered */
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(exe.getName(), canvas.getWidth() / 2, canvas.getHeight() / 2);

        /* Draw the input/output DataPort lines - equally spaced and centered */
        final List<DataPortArea> areas = ui.getCachedPortAreas();
        for(DataPortArea area : areas) {
            gc.setFill(Color.BLACK);
            gc.fillRect(
                    /* x      => */ area.getLineStartX(),
                    /* y      => */ area.getLineStartY(),
                    /* width  => */ 1,
                    /* height => */ DATA_PORT_LENGTH);
        }
    }

    protected void drawOutline(final GraphicsContext gc) {
        /* Draw executable rectangle outline */
        gc.strokeRect(
                /* x      => */ 0,
                /* y      => */ TOP_BOTTOM_BOX_PADDING,
                /* width  => */ canvas.getWidth(),
                /* height => */ canvas.getHeight() - TOP_BOTTOM_BOX_PADDING * 2
        );
    }
}
