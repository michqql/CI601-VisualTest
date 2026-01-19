package me.mp1282.visualtest.ui.diagram.node;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.executable.ExecutableUiSkin;

import java.util.List;

public class DiagramNodeUiSkin extends ExecutableUiSkin {

    public DiagramNodeUiSkin(DiagramNodeUi exe) {
        super(exe);
    }

    @Override
    protected void draw(GraphicsContext gc) {
        super.draw(gc);

        final DiagramNodeUi ui = (DiagramNodeUi) getSkinnable();
        final DiagramNode node = ui.getNode();
        final boolean hovered = ui.hoverProperty().get();

        final List<DataPortArea> areas = ui.getCachedPortAreas();
        final DataPortArea hoveredDataPort = ui.hoveredDataPortProperty.get();
        /* Draw the data port line and type information */
        for(DataPortArea area : areas) {
            /* Show type information if the diagram node is hovered,
             * but not if a specific data port is being hovered,
             * unless this is the data port being hovered
             */
            if((hovered && hoveredDataPort == null) || area.equals(hoveredDataPort)) {
                gc.setFill(Color.BLACK);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.fillText(
                        /* Text => */ area.getDataPort().dataTypeDescriptor(),
                        /* X    => */ area.getMidX(),
                        /* Y    => */ area.getMidY() - (area.getFlow().getDirection() * 15));
                gc.fillText(
                        /* Text => */ area.getDataPort().name(),
                        /* X    => */ area.getMidX(),
                        /* Y    => */ area.getMidY() - (area.getFlow().getDirection() * 25));
            }
        }

        /* Draw a box around the hovered data port */
        if(hoveredDataPort != null) {
            gc.setStroke(Color.RED);
            gc.setLineDashes(1, 2);
            gc.setLineWidth(1);
            gc.strokeRect(
                    /* x      => */ hoveredDataPort.getLeftX(),
                    /* y      => */ hoveredDataPort.getTopY(),
                    /* width  => */ BOX_SIDE_LENGTH,
                    /* height => */ BOX_SIDE_LENGTH);
        }

        /* Draw execution cost */
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
                /* Text => */ String.valueOf(node.executionCostProperty().get()),
                /* X    => */ canvas.getWidth() - TOP_BOTTOM_BOX_PADDING,
                /* Y    => */ canvas.getHeight() / 2);
    }
}
