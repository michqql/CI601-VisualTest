package me.mp1282.visualtest.util;

import javafx.beans.property.*;
import javafx.geometry.Bounds;

public class ObservableBounds {

    private final ObjectProperty<Bounds> rawBounds =
            new SimpleObjectProperty<>(this, "rawBounds");

    private final DoubleProperty minX = new SimpleDoubleProperty(this, "minX");
    private final DoubleProperty minY = new SimpleDoubleProperty(this, "minY");
    private final DoubleProperty maxX = new SimpleDoubleProperty(this, "maxX");
    private final DoubleProperty maxY = new SimpleDoubleProperty(this, "maxY");
    private final DoubleProperty centerX = new SimpleDoubleProperty(this, "centerX");
    private final DoubleProperty centerY = new SimpleDoubleProperty(this, "centerY");

    public ObservableBounds() {
        rawBounds.addListener((_, _, b) -> update(b));
    }

    private void update(Bounds b) {
        if (b == null) return;
        minX.set(b.getMinX());
        minY.set(b.getMinY());
        maxX.set(b.getMaxX());
        maxY.set(b.getMaxY());
        centerX.set(b.getCenterX());
        centerY.set(b.getCenterY());
    }

    public ObjectProperty<Bounds> rawBoundsProperty() { return rawBounds; }
    public ReadOnlyDoubleProperty minXProperty() { return minX; }
    public ReadOnlyDoubleProperty minYProperty() { return minY; }
    public ReadOnlyDoubleProperty maxXProperty() { return maxX; }
    public ReadOnlyDoubleProperty maxYProperty() { return maxY; }
    public ReadOnlyDoubleProperty centerXProperty() { return centerX; }
    public ReadOnlyDoubleProperty centerYProperty() { return centerY; }
}

