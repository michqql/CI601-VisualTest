package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class BranchExecutableSkin extends DefaultExecutableSkin {

    public BranchExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
    }

    @Override
    protected void createMainBody(StackPane parent) {
        VBox body = new VBox(4);
        body.setPadding(new Insets(4));
        body.setAlignment(Pos.CENTER);

        /* Node name */
        StackPane namePane = new StackPane();
        super.createMainBody(namePane);
        body.getChildren().add(namePane);

        /* Execution path output labels */
        HBox pathLabels = new HBox(16);
        pathLabels.setAlignment(Pos.CENTER);

        Text trueLabel  = new Text("→ " + BranchExecutable.TRUE_BRANCH  + ": true");
        Text falseLabel = new Text("→ " + BranchExecutable.FALSE_BRANCH + ": false");

        HBox.setHgrow(trueLabel,  Priority.ALWAYS);
        HBox.setHgrow(falseLabel, Priority.ALWAYS);
        trueLabel .setStyle("-fx-font-size: 10;");
        falseLabel.setStyle("-fx-font-size: 10;");

        pathLabels.getChildren().addAll(trueLabel, falseLabel);
        body.getChildren().add(pathLabels);

        parent.getChildren().add(body);
    }
}
