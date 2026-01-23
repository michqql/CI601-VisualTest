package me.mp1282.visualtest.ui.other;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ToastUi {

    private ToastUi() throws IllegalAccessException {
        throw new IllegalAccessException("ToastUi cannot be instantiated");
    }

    public static void make(Stage owner, String message,
                            int fadeInMs, int displayMs, int fadeOutMs) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(message);

        StackPane root = new StackPane(text);
        root.setStyle(
                "-fx-background-radius: 20; " +
                        "-fx-background-color: rgba(0, 0, 0, 0.2); " +
                        "-fx-padding: 50px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();

        FadeTransition inTransition = new FadeTransition(Duration.millis(fadeInMs),
                toastStage.getScene().getRoot());
        inTransition.setFromValue(0.0);
        inTransition.setToValue(1.0);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(displayMs));

        FadeTransition outTransition = new FadeTransition(Duration.millis(fadeOutMs),
                toastStage.getScene().getRoot());
        outTransition.setFromValue(1.0);
        outTransition.setToValue(0.0);

        SequentialTransition mainTransition = new SequentialTransition(
                inTransition, pauseTransition, outTransition);
        mainTransition.setOnFinished(_ -> toastStage.close());
        mainTransition.play();
    }
}
