package me.mp1282.visualtest.ui.other;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.List;

public class ArrowLineUi extends Group {

    private final Line line;
    private Color colour = Color.BLACK;

    public ArrowLineUi() {
        this.line   = new Line();
        line.strokeProperty().set(Color.color(0, 0, 0, 0));
        line.strokeWidthProperty().set(5);
        line.hoverProperty().addListener((_, _, t1) -> onHover(t1));
        line.setOnMousePressed(this::onClick);

        getChildren().add(line);

        /* When either of the start or end positions change, rebuild the arrow */
        PropertyHelper.addListenerForEach(
                List.of(
                        line.startXProperty(), line.startYProperty(),
                        line.endXProperty(),   line.endYProperty()
                ),
                _ -> rebuildArrowLine());
    }

    public void setStartX(double startX) {
        line.startXProperty().set(startX);
    }

    public void setStartY(double startY) {
        line.startYProperty().set(startY);
    }

    public void setEndX(double endX) {
        line.endXProperty().set(endX);
    }

    public void setEndY(double endY) {
        line.endYProperty().set(endY);
    }

    public void setColour(Color colour) {
        this.colour = colour;
        recolourTriangles();
    }

    public void setWidth(double width) {
        line.strokeWidthProperty().set(width);
        resizeTriangles();
    }

    /* Functions that can be overridden to provide additional control to child classes */
    protected void onHover(boolean hovering) { /* Do nothing by default */ }
    protected void onClick(MouseEvent event) { /* Do nothing by default */ }

    protected void rebuildArrowLine() {
        getChildren().removeIf(Polygon.class::isInstance); /* Remove all the triangles */

        double dx = line.endXProperty().get() - line.startXProperty().get();
        double dy = line.endYProperty().get() - line.startYProperty().get();
        double length = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx); /* Angle in radians */

        final double arrowBaseWidth = line.strokeWidthProperty().get();
        final int arrowCount = Math.max(2, (int) length / 10);
        final double arrowSpacing = length / arrowCount;

        for (int i = 1; i < arrowCount; i++) {
            double x = line.startXProperty().get() + (i * arrowSpacing) * Math.cos(angle);
            double y = line.startYProperty().get() + (i * arrowSpacing) * Math.sin(angle);

            Polygon triangle = new Polygon(
                    /* Point A => */ arrowBaseWidth / 2, 0,
                    /* Point B => */ -arrowBaseWidth / 2,  arrowBaseWidth / 2,
                    /* Point C => */ -arrowBaseWidth / 2, -arrowBaseWidth / 2
            );
            triangle.setFill(colour);
            triangle.setMouseTransparent(true);

            triangle.setTranslateX(x);
            triangle.setTranslateY(y);
            triangle.setRotate(Math.toDegrees(angle));

            getChildren().add(triangle);
        }
    }

    private void recolourTriangles() {
        for (Node child : getChildren()) {
            if(child instanceof Polygon triangle) {
                triangle.setFill(colour);
            }
        }
    }

    private void resizeTriangles() {
        final double arrowBaseWidth = line.strokeWidthProperty().get();

        for (Node child : getChildren()) {
            if(child instanceof Polygon triangle) {
                triangle.getPoints().setAll(
                        /* Point A => */  arrowBaseWidth / 2D, 0D,
                        /* Point B => */ -arrowBaseWidth / 2D,  arrowBaseWidth / 2D,
                        /* Point C => */ -arrowBaseWidth / 2D, -arrowBaseWidth / 2D
                );
            }
        }
    }
}
