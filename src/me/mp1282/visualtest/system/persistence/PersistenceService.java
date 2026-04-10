package me.mp1282.visualtest.system.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.persistence.handlers.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PersistenceService implements IReset {

    private static final String DIAGRAMS_DIRECTORY = "diagrams";
    private static final String PROJECT_INFO_FILENAME = "project_info.json";
    private static final String DIAGRAM_FILENAME_FORMAT = "%s.json";

    private final Gson gson;

    public PersistenceService(final VisualTestSystem sys) {

        this.gson = new GsonBuilder()
                /* Persistence handlers */
                .registerTypeAdapter(ProjectInformation.class, new ProjectInformationPersistenceHandler())
                .registerTypeAdapter(Diagram.class,            new DiagramPersistenceHandler())
                .registerTypeAdapter(DiagramNode.class,        new DiagramNodePersistenceHandler())
                .registerTypeAdapter(Executable.class,         new ExecutablePersistenceHandler())
                .registerTypeAdapter(LoadedJar.class,          new LoadedJarPersistenceHandler())
                .registerTypeAdapter(NodeData.class,           new NodeDataPersistenceHandler())
                /* Other settings */
                .setPrettyPrinting()
                .create();
    }

    public void load(File directory) throws Exception {
        final ProjectInformation info = VisualTestSystem.getInstance().getProjectInformation();
        final DiagramRepository diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        /* Need to manually set project directory in the ProjectInformation class */
        info.projectDirectoryProperty().set(directory);

        try (FileReader reader = new FileReader(new File(directory, PROJECT_INFO_FILENAME))) {
            /* Result of this call is ignored because ProjectInformation
             * is a final field within the VisualTestSystem singleton.
             */
            gson.fromJson(reader, ProjectInformation.class);
        }

        File diagramDir = new File(directory, DIAGRAMS_DIRECTORY);
        File[] diagramFiles = diagramDir.listFiles();
        if(diagramFiles != null) {
            for(File diagramFile : diagramFiles) {
                try (FileReader reader = new FileReader(diagramFile)) {
                    diagramRepository.getDiagrams().add(gson.fromJson(reader, Diagram.class));
                }
            }
        }
    }

    public SaveResult save() throws Exception {
        final VisualTestSystem sys = VisualTestSystem.getInstance();
        final File dir = sys.getProjectInformation().projectDirectoryProperty().get();
        /* Check that a directory has been selected to save the project under,
         * and that the chosen location is not a file.
         */
        if(dir == null)
            return SaveResult.NO_PROJECT_DIRECTORY;

        if(dir.isFile())
            return SaveResult.INVALID_PROJECT_DIRECTORY;

        /* Create the directory if it doesn't exist */
        if(!dir.exists() && !dir.mkdirs())
            return SaveResult.FAILED_TO_MAKE_PROJECT_DIRECTORY;

        /* Set project information version to current system version */
        sys.getProjectInformation().setVersionToCurrent();

        try(FileWriter writer = new FileWriter(new File(dir, PROJECT_INFO_FILENAME))) {
            gson.toJson(sys.getProjectInformation(), writer);
        }

        /* Create directory for diagrams */
        File diagramsDir = new File(dir, DIAGRAMS_DIRECTORY);
        if(!diagramsDir.exists() && !diagramsDir.mkdirs())
            return SaveResult.FAILED_TO_MAKE_DIAGRAMS_DIRECTORY;

        /* Save each diagram */
        for(Diagram diagram : VisualTestSystem.getInstance().getDiagramRepository().getDiagrams()) {
            try(FileWriter writer = new FileWriter(new File(diagramsDir, String.format(DIAGRAM_FILENAME_FORMAT, diagram.nameProperty().get())))) {
                gson.toJson(diagram, writer);
            }
            diagram.unsavedProperty().set(false);
        }

        return SaveResult.SUCCESS;
    }

    @Override
    public void reset() {

    }
}
