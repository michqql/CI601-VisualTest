package me.mp1282.visualtest.ui.executable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;

import java.util.ArrayList;
import java.util.List;

public class ExecutableUi extends Control {

    protected final Executable executable;
    protected final List<DataPortArea> cachedPortAreas;

    protected final BooleanProperty requestRedrawProperty;

    public ExecutableUi(final Executable executable) {
        this.executable = executable;
        this.cachedPortAreas = new ArrayList<>();

        this.requestRedrawProperty = new SimpleBooleanProperty(false);

        /* Set a default dimension of 150x150 */
        setWidth(150);
        setHeight(150);

        /* Re-cache the data port areas when resizing
         * (which will change when the width and height of this UI component changes)
         */
        widthProperty().addListener((obs, old, newValue) -> {
            cacheDataPortAreas();
            requestRedraw();
        });
        heightProperty().addListener((obs, old, newValue) -> {
            cacheDataPortAreas();
            requestRedraw();
        });
        cacheDataPortAreas();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ExecutableUiSkin(this);
    }

    public Executable getExecutable() {
        return executable;
    }

    public void requestRedraw() {
        /* Toggle the redraw property value, as the actual value doesn't matter, it's only about generating
         * a change/invalidation on the property that can be listened to.
         */
        requestRedrawProperty.set(!requestRedrawProperty.get());
    }

    public List<DataPortArea> getCachedPortAreas() {
        return cachedPortAreas;
    }

    protected void cacheDataPortAreas() {
        cachedPortAreas.clear();

        for (DataPort input : executable.getInputs())
            cachedPortAreas.add(new DataPortArea(this, input));

        for(DataPort output : executable.getOutputs())
            cachedPortAreas.add(new DataPortArea(this, output));
    }
}
