package me.mp1282.visualtest.ui.other;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import me.mp1282.visualtest.ui.diagram.DiagramUi;

public class SelectionBoxRectUi extends Rectangle {

    private static final Color COLOR_START = Color.color(0.0f, 0.0f, 0.0f, 1.0f);
    private static final Color COLOR_END   = Color.color(0.0f, 0.0f, 0.0f, 0.3f);

    private final DiagramUi ui;

    public SelectionBoxRectUi(DiagramUi ui) {
        this.ui = ui;

        getStrokeDashArray().setAll(10.0D, 10.0D);
        setStrokeWidth(1);
        setStroke(COLOR_START);
        setStrokeType(StrokeType.OUTSIDE);

        setFill(Color.TRANSPARENT); /* No fill */

        Timeline animationTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(strokeDashOffsetProperty(), 0),
                        new KeyValue(strokeProperty(), COLOR_START)
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(strokeDashOffsetProperty(), 50, Interpolator.LINEAR),
                        new KeyValue(strokeProperty(), COLOR_END))
        );
        animationTimeline.setAutoReverse(true);
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();

        /* Add event handlers to X, Y, width and height properties */
        xProperty().addListener((obs, old, newValue) -> findSelectedNodes());
        yProperty().addListener((obs, old, newValue) -> findSelectedNodes());
        widthProperty().addListener((obs, old, newValue) -> findSelectedNodes());
        heightProperty().addListener((obs, old, newValue) -> findSelectedNodes());
    }

    private void findSelectedNodes() {
//        for(Node node : ui.getChildren()) {
//            if(node instanceof ISelectableUi selectableUi) {
//                double worldToScreenX = node.getLayoutX() - node.getTranslateX();
//                double worldToScreenY = node.getLayoutY() - node.getTranslateY();
//
//                boolean selected = intersects(worldToScreenX, worldToScreenY,
//                        node.getBoundsInParent().getWidth(),
//                        node.getBoundsInParent().getHeight());
//
//                selectableUi.selectedProperty().set(selected);
//            }
//        }
    }
}
