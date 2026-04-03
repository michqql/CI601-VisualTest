package me.mp1282.visualtest.ui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import me.mp1282.visualtest.ui.diagram.tab.DiagramTabPaneUi;
import me.mp1282.visualtest.ui.sidebar.ClassHierarchyUi;
import me.mp1282.visualtest.ui.sidebar.ExecutableListUi;

public class MainUi extends VBox {


    public MainUi(final Window window) {
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
        /* UI elements */
        final DiagramTabPaneUi diagramTabPane = new DiagramTabPaneUi();

        /* The main split pane, containing the sidebar and tabs for the diagrams */
        SplitPane mainContentSplitPane = new SplitPane(sidebarSplitPane, diagramTabPane);
        mainContentSplitPane.setDividerPosition(0, 0.25D);

        VBox.setVgrow(mainContentSplitPane, Priority.ALWAYS);
        VBox.setVgrow(sidebarSplitPane, Priority.ALWAYS);

        getChildren().addAll(new MainMenuBarUi(window), mainContentSplitPane);
    }
}
