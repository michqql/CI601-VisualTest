package me.mp1282.visualtest.ui.other;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.List;

public class ArrowLineUi extends Group {

    /* Line variables */
    protected final DoubleProperty startX;
    protected final DoubleProperty startY;
    protected final DoubleProperty endX;
    protected final DoubleProperty endY;
    protected final ObjectProperty<Color> colour;

    public ArrowLineUi() {
        this.startX = new SimpleDoubleProperty();
        this.startY = new SimpleDoubleProperty();
        this.endX   = new SimpleDoubleProperty();
        this.endY   = new SimpleDoubleProperty();
        this.colour = new SimpleObjectProperty<>(Color.BLACK);

        /* When either of the start or end positions change, rebuild the arrow */
        PropertyHelper.addListenerForEach(
                List.of(startX, startY, endX, endY),
                _ -> rebuildArrowLine());
    }

    public DoubleProperty startXProperty() {
        return startX;
    }

    public DoubleProperty startYProperty() {
        return startY;
    }

    public DoubleProperty endXProperty() {
        return endX;
    }

    public DoubleProperty endYProperty() {
        return endY;
    }

    public ObjectProperty<Color> colourProperty() {
        return colour;
    }

    private void rebuildArrowLine() {
        getChildren().clear();

        double dx = endX.get() - startX.get();
        double dy = endY.get() - startY.get();
        double length = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx); /* Angle in radians */

        final double arrowSize = 5;
        final int arrowCount = Math.max(2, (int) length / 10);
        final double arrowSpacing = length / arrowCount;

        for (int i = 1; i < arrowCount; i++) {
            double x = startX.get() + (i * arrowSpacing) * Math.cos(angle);
            double y = startY.get() + (i * arrowSpacing) * Math.sin(angle);

            Polygon triangle = new Polygon(
                    /* Point A => */ 0, 0,
                    /* Point B => */ -arrowSize,  arrowSize / 2,
                    /* Point C => */ -arrowSize, -arrowSize / 2
            );
            triangle.setFill(colour.get());

            triangle.setTranslateX(x);
            triangle.setTranslateY(y);
            triangle.setRotate(Math.toDegrees(angle));

            getChildren().add(triangle);
        }
    }
}
