package me.mp1282.visualtest.system;

import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironmentService;
import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodRepository;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutableRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.system.persistence.PersistenceService;

import java.util.Map;

/**
 * VisualTestSystem is a singleton class that holds information about the project currently open
 * in the program. It is a singleton as only a single project can be open in a single program.
 * <br><br>
 * It also holds services used by the system.
 */
public class VisualTestSystem {

    public static final int SYSTEM_VERSION_MAJOR = 0;
    public static final int SYSTEM_VERSION_MINOR = 3;

    /* Singleton class */
    private static VisualTestSystem INSTANCE;
    public static synchronized VisualTestSystem getInstance() {
        if(INSTANCE == null)
            INSTANCE = new VisualTestSystem();

        return INSTANCE;
    }

    public static boolean isVersionSupported(int major, int minor) {
        return major == SYSTEM_VERSION_MAJOR && minor == SYSTEM_VERSION_MINOR;
    }

    /* Basic project information */
    private final ProjectInformation projectInformation;

    /* Data repositories */
    private final InbuiltMethodRepository inbuiltMethodRepository;
    private final SpecialExecutableRepository specialExecutableRepository;
    private final LoadedJarRepository jarRepository;
    private final DiagramRepository diagramRepository;
    private final Map<String, IExecutableTypeHolder<?>> dataHolderRegistry;

    /* Services */
    private final RuntimeEnvironmentService runtimeEnvironmentService;
    private final PersistenceService persistenceService;

    /* Enforce singleton design pattern */
    private VisualTestSystem() {
        this.projectInformation = new ProjectInformation();

        /* Data repositories */
        this.inbuiltMethodRepository = new InbuiltMethodRepository();
        this.specialExecutableRepository = new SpecialExecutableRepository();
        this.jarRepository = new LoadedJarRepository();
        this.diagramRepository = new DiagramRepository();

        this.dataHolderRegistry = Map.of(
            inbuiltMethodRepository    .getType(), inbuiltMethodRepository,
            specialExecutableRepository.getType(), specialExecutableRepository,
            jarRepository              .getType(), jarRepository,
            diagramRepository          .getType(), diagramRepository
        );

        /* Services */
        this.runtimeEnvironmentService = new RuntimeEnvironmentService();
        this.persistenceService = new PersistenceService(this);
    }

    public ProjectInformation getProjectInformation() {
        return projectInformation;
    }

    public InbuiltMethodRepository getInbuiltMethodRepository() {
        return inbuiltMethodRepository;
    }

    public SpecialExecutableRepository getSpecialExecutableRepository() {
        return specialExecutableRepository;
    }

    public LoadedJarRepository getJarRepository() {
        return jarRepository;
    }

    public DiagramRepository getDiagramRepository() {
        return diagramRepository;
    }

    public RuntimeEnvironmentService getRuntimeEnvironmentService() {
        return runtimeEnvironmentService;
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public void resetSystem() {
        /* Reset any services */
        runtimeEnvironmentService.reset();
        persistenceService.reset();

        /* Reset project data */
        projectInformation.reset();
        inbuiltMethodRepository.reset();
        jarRepository.reset();
        diagramRepository.reset();
    }

    public IExecutableTypeHolder<?> findDataHolder(String type) {
        return dataHolderRegistry.get(type);
    }
}
