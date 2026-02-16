package me.mp1282.visualtest.system;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironmentService;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;

/**
 * VisualTestSystem is a singleton class that holds information about the project currently open
 * in the program. It is a singleton as only a single project can be open in a single program.
 * <br><br>
 * It also holds services used by the system.
 */
public class VisualTestSystem {

    public static final int SYSTEM_VERSION_MAJOR = 0;
    public static final int SYSTEM_VERSION_MINOR = 1;

    /* Singleton class */
    private static VisualTestSystem INSTANCE;
    public static synchronized VisualTestSystem getInstance() {
        if(INSTANCE == null)
            INSTANCE = new VisualTestSystem();

        return INSTANCE;
    }

    /* Basic project information */
        private final ProjectInformation projectInformation = new ProjectInformation();

    /* Data repositories */
    private final InbuiltMethodRepository inbuiltMethodRepository = new InbuiltMethodRepository();
    private final LoadedJarRepository jarRepository = new LoadedJarRepository();
    private final DiagramRepository diagramRepository = new DiagramRepository();

    /* Services */
    private final RuntimeEnvironmentService runtimeEnvironmentService = new RuntimeEnvironmentService();

    /* Enforce singleton design pattern */
    private VisualTestSystem() {
    }

    public ProjectInformation getProjectInformation() {
        return projectInformation;
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

    public RuntimeEnvironmentService getRuntimeEnvironment() {
        return runtimeEnvironmentService;
    }
}
