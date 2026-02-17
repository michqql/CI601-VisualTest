package me.mp1282.visualtest.ui.project;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ProjectWindowUi extends Stage {

    private static ProjectWindowUi instance;

    public ProjectWindowUi(Window owner) {
        initOwner(owner);
        setTitle("Project Information");

        TabPane tabPane = new TabPane(
                new Tab("Basic Information", new BasicInfoUi()),
                new Tab("Diagrams", new DiagramInfoUi())
        );
        /* Ensure tab's cannot be closed */
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        setScene(new Scene(tabPane));

        /* Remove the static instance when the window is closed, as some resources
         * will no longer be valid so cannot reuse the window.
         */
        setOnCloseRequest(_ -> instance = null);
    }

    public static void showWindow(Window owner) {
        if(instance != null && instance.isShowing()) {
            instance.toFront();
            return;
        }

        instance = new ProjectWindowUi(owner);
        instance.show();
    }
}
