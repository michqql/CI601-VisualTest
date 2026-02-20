package me.mp1282.visualtest.ui;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.system.persistence.SaveResult;
import me.mp1282.visualtest.ui.other.ToastUi;
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
            saveItem.setOnAction(this::onSave);

            MenuItem loadItem = new MenuItem("Load Project");
            loadItem.setOnAction(this::onLoad);

            MenuItem createProjectItem = new MenuItem("New Project");
//            createProjectItem.setOnAction(_ -> createProject());

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

    private void onSave(ActionEvent event) {
        final ObjectProperty<File> projectDirectory = VisualTestSystem.getInstance()
                .getProjectInformation().projectDirectoryProperty();

        /* If the project directory is null, prompt to choose a directory */
        if(projectDirectory.get() == null) {
            projectDirectory.set(promptProjectDirectoryChooser("Save Project"));
        }

        SaveResult result = SaveResult.UNKNOWN_FAILURE;
        try {
            result = VisualTestSystem.getInstance().getPersistenceService().save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result != SaveResult.SUCCESS) {
            ToastUi.make(getScene().getWindow(), "Save failed: " + result.name(),
                    0, 2000, 500);
        }
    }

    private void onLoad(ActionEvent event) {
        /* Prompt the user for a directory to load */
        File dir = promptProjectDirectoryChooser("Load Project");

        /* TODO: Ask if user would like to save current project first, before resetting system */
        VisualTestSystem.getInstance().resetSystem();

        try {
            VisualTestSystem.getInstance().getPersistenceService().load(dir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private void createProject() {
//        File selectedDir = promptProjectDirectoryChooser();
//
//        /* TODO: Ask if user would like to save current project first, before resetting system */
//        VisualTestSystem.getInstance().resetSystem();
//
//        try {
//            VisualTestSystem.getInstance().getPersistenceService().load(selectedDir);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

    private File promptProjectDirectoryChooser(String actionTitle) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a Directory : " + actionTitle);

        /* Set initial directory to current working directory */
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        return chooser.showDialog(getScene().getWindow());
    }
}
