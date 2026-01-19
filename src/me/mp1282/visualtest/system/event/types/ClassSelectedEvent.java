package me.mp1282.visualtest.system.event.types;

import me.mp1282.visualtest.system.event.IEvent;
import me.mp1282.visualtest.system.jarload.LoadedClass;

public class ClassSelectedEvent implements IEvent {

    private final LoadedClass loadedClass;

    public ClassSelectedEvent(LoadedClass loadedClass) {
        this.loadedClass = loadedClass;
    }

    public LoadedClass getLoadedClass() {
        return loadedClass;
    }
}
