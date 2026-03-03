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

    private final DiagramRepository diagramRepository;

    /* UI elements */
    private final DiagramTabPaneUi diagramTabPane;
    private final Stage runtimeStage;


    public MainUi(Window window) {
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        this.runtimeStage = new Stage();
        runtimeStage.initOwner(window);
        runtimeStage.setTitle("Runtime / Debugger");
        runtimeStage.setScene(new Scene(new RuntimeUi()));

        setAlignment(Pos.TOP_CENTER);

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

        getChildren().addAll(new MainMenuBarUi(), mainContentSplitPane);
    }
}
