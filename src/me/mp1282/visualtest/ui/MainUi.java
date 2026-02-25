package me.mp1282.visualtest.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.ui.diagram.runtime.RuntimeUi;
import me.mp1282.visualtest.ui.diagram.tab.DiagramTabPaneUi;
import me.mp1282.visualtest.ui.other.ToastUi;
import me.mp1282.visualtest.ui.sidebar.ClassHierarchyUi;
import me.mp1282.visualtest.ui.sidebar.ExecutableListUi;

import java.io.File;
import java.util.Optional;

public class MainUi extends VBox {

    private final LoadedJarRepository jarRepository;
    private final DiagramRepository diagramRepository;

    /* UI elements */
    private final DiagramTabPaneUi diagramTabPane;
    private final Stage runtimeStage;


    public MainUi(Window window) {
        this.jarRepository = VisualTestSystem.getInstance().getJarRepository();
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        this.runtimeStage = new Stage();
        runtimeStage.initOwner(window);
        runtimeStage.setTitle("Runtime / Debugger");
        runtimeStage.setScene(new Scene(new RuntimeUi()));

        setAlignment(Pos.TOP_CENTER);

        /* MenuBar UI with key elements */
        MainMenuBarUi menuBarUi = new MainMenuBarUi();

        /* ToolBar UI with key buttons */
        Button loadJar = new Button("Load JAR");
//        loadJar.setOnAction(this::onClickLoadJar);

        Button newDiagram = new Button("New Diagram");
        newDiagram.setOnAction(this::onClickNewDiagram);

        Button saveDiagram = new Button("Save Diagram");
        saveDiagram.setOnAction(this::onClickSaveDiagram);

        Button runDiagram = new Button("Run Diagram");
        runDiagram.setOnAction(this::onClickRunDiagram);

        ToolBar toolBar = new ToolBar(loadJar, newDiagram, saveDiagram, runDiagram);

        /* The sidebar split pane, containing the class hierarchy
         * above the executable list
         */
        SplitPane sidebarSplitPane = new SplitPane(
                new ClassHierarchyUi(),
                new ExecutableListUi()
        );
        sidebarSplitPane.setOrientation(Orientation.VERTICAL);

        /* Tab pane that holds each diagram */
        this.diagramTabPane = new DiagramTabPaneUi();

        /* The main split pane, containing the sidebar and tabs for the diagrams */
        SplitPane mainContentSplitPane = new SplitPane(sidebarSplitPane, diagramTabPane);
        mainContentSplitPane.setDividerPosition(0, 0.25D);

        VBox.setVgrow(mainContentSplitPane, Priority.ALWAYS);
        VBox.setVgrow(sidebarSplitPane, Priority.ALWAYS);

        getChildren().addAll(menuBarUi, mainContentSplitPane);
    }



    private void onClickNewDiagram(ActionEvent actionEvent) {
        diagramRepository.getDiagrams().add(new Diagram());
    }

    private void onClickSaveDiagram(ActionEvent actionEvent) {
        if(diagramTabPane.getSelectionModel().getSelectedItem()
                .getUserData() instanceof Diagram diagram) {

//            JsonObject json = diagram.save();
//
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save Diagram");
//            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("JSON Files", "*.json"),
//                    new FileChooser.ExtensionFilter("All Files", "*.*")
//            );
//
//            final File selectedFile = fileChooser.showSaveDialog(getScene().getWindow());
//            if(selectedFile != null) {
//                try (FileWriter writer = new FileWriter(selectedFile)) {
//                    gson.toJson(json, writer);
//                } catch (IOException e) {
//                    System.err.println("Failed to save diagram file!");
//                }
//            }
        }
    }

    private void onClickRunDiagram(ActionEvent actionEvent) {
        runtimeStage.show();
    }
}
