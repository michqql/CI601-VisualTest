package me.mp1282.visualtest.ui.other;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ZoomLevelMenuItemUi extends CustomMenuItem {

    private final BooleanProperty currentZoomLevel;

    public ZoomLevelMenuItemUi(String text) {
        this.currentZoomLevel = new SimpleBooleanProperty();

        Circle selectedCircle = new Circle(5, Color.BLACK);
        Label label = new Label(text);
        label.setTextFill(Color.BLACK);

        HBox hbox = new HBox(5, selectedCircle, label);
        hbox.setAlignment(Pos.CENTER_LEFT);

        setContent(hbox);
        setHideOnClick(false);

        selectedCircle.visibleProperty().bind(currentZoomLevel);
    }

    public BooleanProperty currentZoomLevelProperty() {
        return currentZoomLevel;
    }
}
