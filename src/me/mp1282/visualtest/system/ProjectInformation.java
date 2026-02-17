package me.mp1282.visualtest.system;

import javafx.beans.property.*;

import java.io.File;

public class ProjectInformation implements IReset {
    private final StringProperty  name                  = new SimpleStringProperty();
    private final BooleanProperty hasWorkspace          = new SimpleBooleanProperty();
    private final ObjectProperty<File> projectDirectory = new SimpleObjectProperty<>();
    private final IntegerProperty versionMajor          = new SimpleIntegerProperty();
    private final IntegerProperty versionMinor          = new SimpleIntegerProperty();

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty hasWorkspaceProperty() {
        return hasWorkspace;
    }

    public ObjectProperty<File> projectDirectoryProperty() {
        return projectDirectory;
    }

    public ReadOnlyIntegerProperty versionMajorProperty() {
        return versionMajor;
    }

    public ReadOnlyIntegerProperty versionMinorProperty() {
        return versionMinor;
    }

    @Override
    public void reset() {
        name.set(null);
        hasWorkspace.set(false);
        projectDirectory.set(null);
        versionMajor.set(0);
        versionMinor.set(0);
    }
}
