package me.mp1282.visualtest.ui.executable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.executable.Executable;

public class ExecutableUi extends Control {

    protected final Executable executable;

    public ExecutableUi(final Executable executable) {
        this.executable = executable;

        /* Set a default dimension of 150x150 */
        setWidth(75);
        setHeight(75);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ExecutableUiSkin(this);
    }

    public Executable getExecutable() {
        return executable;
    }
}
