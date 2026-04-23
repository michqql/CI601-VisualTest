package me.mp1282.visualtest.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import me.mp1282.visualtest.util.DiagramZoomLevel;

public class UiPreferencesService {

    private static final UiPreferencesService INSTANCE = new UiPreferencesService();
    private final BooleanProperty explainMode = new SimpleBooleanProperty(false);
    private final ObjectProperty<DiagramZoomLevel> zoomLevel =
            new SimpleObjectProperty<>(DiagramZoomLevel.DEFAULT);
    private final BooleanProperty verticalOrientation = new SimpleBooleanProperty(false);

    private UiPreferencesService() {}

    public static UiPreferencesService getInstance() { return INSTANCE; }
    public BooleanProperty explainModeProperty() { return explainMode; }
    public ObjectProperty<DiagramZoomLevel> zoomLevelProperty() { return zoomLevel; }
    public BooleanProperty verticalOrientationProperty() { return verticalOrientation; }
}
