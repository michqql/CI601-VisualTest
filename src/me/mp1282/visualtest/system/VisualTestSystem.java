package me.mp1282.visualtest.system;

import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironment;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;

public class VisualTestSystem {

    /* Singleton class */
    private static VisualTestSystem INSTANCE;
    public static synchronized VisualTestSystem getInstance() {
        if(INSTANCE == null)
            INSTANCE = new VisualTestSystem();

        return INSTANCE;
    }

    private final InbuiltMethodRepository inbuiltMethodRepository = new InbuiltMethodRepository();
    private final LoadedJarRepository jarRepository = new LoadedJarRepository();
    private final DiagramRepository diagramRepository = new DiagramRepository();

    private final RuntimeEnvironment runtimeEnvironment = new RuntimeEnvironment();

    /* Enforce singleton design pattern */
    private VisualTestSystem() {
    }

    public InbuiltMethodRepository getInbuiltMethodRepository() {
        return inbuiltMethodRepository;
    }

    public LoadedJarRepository getJarRepository() {
        return jarRepository;
    }

    public DiagramRepository getDiagramRepository() {
        return diagramRepository;
    }

    public RuntimeEnvironment getRuntimeEnvironment() {
        return runtimeEnvironment;
    }
}
