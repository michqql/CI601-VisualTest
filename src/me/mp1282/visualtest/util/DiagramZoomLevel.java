package me.mp1282.visualtest.util;

public enum DiagramZoomLevel {

    THREE_QUARTERS_OUT(0.25),
    HALF_OUT(0.5),
    QUARTER_OUT(0.75),
    DEFAULT(1.0),
    QUARTER_IN(1.25),
    HALF_IN(1.5),
    THREE_QUARTERS_IN(1.75)

    ;

    private final double zoom;

    DiagramZoomLevel(double zoom) {
        this.zoom = zoom;
    }

    DiagramZoomLevel(DiagramZoomLevel other) {
        this(other.zoom);
    }

    public double getZoom() {
        return zoom;
    }
}
