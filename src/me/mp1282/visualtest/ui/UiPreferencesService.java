package me.mp1282.visualtest.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UiPreferencesService {

    private static final UiPreferencesService INSTANCE = new UiPreferencesService();
    private final BooleanProperty explainMode = new SimpleBooleanProperty(false);

    private UiPreferencesService() {}

    public static UiPreferencesService getInstance() { return INSTANCE; }
    public BooleanProperty explainModeProperty() { return explainMode; }
}
