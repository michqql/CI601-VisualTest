package me.mp1282.visualtest;

import javafx.scene.Scene;
import javafx.stage.Stage;
import me.mp1282.visualtest.system.diagram.runtime.ObjectSnapshot;
import me.mp1282.visualtest.ui.MainUi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new MainUi(stage), 1280, 720);

        stage.setTitle("Visual Testing");
        stage.setMaximized(false);
        stage.setScene(scene);
        stage.show();
    }
}
