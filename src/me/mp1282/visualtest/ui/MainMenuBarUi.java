package me.mp1282.visualtest.ui;

import javafx.scene.control.*;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.ui.project.ProjectWindowUi;

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
            MenuItem loadItem = new MenuItem("Load Project");
            MenuItem createProjectItem = new MenuItem("New Project");
            MenuItem addJarItem = new MenuItem("Add JAR");
            MenuItem createDiagramItem = new MenuItem("New Diagram");

            projectMenu.getItems().addAll(
                    infoItem, new SeparatorMenuItem(),
                    saveItem, loadItem, createProjectItem, new SeparatorMenuItem(),
                    addJarItem, new SeparatorMenuItem(),
                    createDiagramItem);
        }

        getMenus().add(projectMenu);
    }
}
