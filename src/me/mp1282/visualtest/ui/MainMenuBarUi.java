package me.mp1282.visualtest.ui;

import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.ui.project.ProjectWindowUi;

import java.io.File;

public class MainMenuBarUi extends MenuBar {

    private final LoadedJarRepository jarRepository;
    private final DiagramRepository diagramRepository;

    public MainMenuBarUi() {
        this.jarRepository = VisualTestSystem.getInstance().getJarRepository();
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        /* Project MenuItem */
        Menu projectMenu = new Menu("Project");
        {
            MenuItem infoItem = new MenuItem("Information");
            infoItem.setOnAction(_ -> ProjectWindowUi.showWindow(getScene().getWindow()));

            MenuItem saveItem = new MenuItem("Save Project");
            saveItem.setOnAction(_ -> attemptToSave());

            MenuItem loadItem = new MenuItem("Load Project");

            MenuItem createProjectItem = new MenuItem("New Project");
            createProjectItem.setOnAction(_ -> createProject());

            MenuItem addJarItem = new MenuItem("Add JAR");
            MenuItem createDiagramItem = new MenuItem("New Diagram");
            createDiagramItem.setOnAction(_ -> diagramRepository.createDiagram());

            projectMenu.getItems().addAll(
                    infoItem, new SeparatorMenuItem(),
                    saveItem, loadItem, createProjectItem, new SeparatorMenuItem(),
                    addJarItem, new SeparatorMenuItem(),
                    createDiagramItem);
        }

        getMenus().add(projectMenu);
    }

    private void attemptToSave() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Save Project");
    }

    private void createProject() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a Directory for Project");

        /* Set initial directory to current working directory */
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File selectedDir = chooser.showDialog(getScene().getWindow());

        /* TODO: Ask if user would like to save current project first, before resetting system */
        VisualTestSystem.getInstance().resetSystem();

        try {
            VisualTestSystem.getInstance().getPersistenceService().load(selectedDir);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
