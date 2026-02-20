package me.mp1282.visualtest.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;

public class MouseDelta {

    /* Mouse positions */
    public static final DoubleProperty lastSceneMouseX = new SimpleDoubleProperty();
    public static final DoubleProperty lastSceneMouseY = new SimpleDoubleProperty();

    public static void updatePosition(MouseEvent e) {
        lastSceneMouseX.set(e.getSceneX());
        lastSceneMouseY.set(e.getSceneY());
    }
}
