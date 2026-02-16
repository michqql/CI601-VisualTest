package me.mp1282.visualtest.system;

import javafx.beans.property.*;

public class ProjectInformation {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty versionMajor = new SimpleIntegerProperty();
    private final IntegerProperty versionMinor = new SimpleIntegerProperty();

    public StringProperty nameProperty() {
        return name;
    }

    public ReadOnlyIntegerProperty versionMajorProperty() {
        return versionMajor;
    }

    public ReadOnlyIntegerProperty versionMinorProperty() {
        return versionMinor;
    }
}
