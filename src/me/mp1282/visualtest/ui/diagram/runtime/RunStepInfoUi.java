package me.mp1282.visualtest.ui.diagram.runtime;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.diagram.runtime.ObjectSnapshot;
import me.mp1282.visualtest.system.diagram.runtime.RunStep;

public class RunStepInfoUi extends VBox {

    private final Label nodeLabel;
    private final ListView<ObjectSnapshot> inputList;
    private final ListView<ObjectSnapshot> outputList;

    public RunStepInfoUi() {
        super(/* Spacing => */ 10);
        this.nodeLabel  = new Label();
        this.inputList  = new ListView<>();
        this.outputList = new ListView<>();

        final Border border = new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT
        ));

        Label inputLabel = new Label("Inputs");
        inputLabel.setLabelFor(inputList);
        this.inputList.setCellFactory(_ -> new ObjectSnapshotCell());
        this.inputList.setBorder(border);

        Label outputLabel = new Label("Outputs");
        outputLabel.setLabelFor(outputList);
        this.outputList.setCellFactory(_ -> new ObjectSnapshotCell());
        this.outputList.setBorder(border);

        getChildren().addAll(nodeLabel, inputLabel, inputList, outputLabel, outputList);
    }

    public void setRunStep(RunStep runStep) {
        nodeLabel .setText(runStep.getNode().getExecutable().getName());
        inputList .setItems(FXCollections.observableList(runStep.getInputSnapshots()));
        outputList.setItems(FXCollections.observableList(runStep.getOutputSnapshots()));
    }

    private static class ObjectSnapshotCell extends ListCell<ObjectSnapshot> {
        @Override
        protected void updateItem(ObjectSnapshot snapshot, boolean empty) {
            super.updateItem(snapshot, empty);

            if(snapshot == null || empty) {
                setText(null);
            } else {
                setText(snapshot.getObject().toString());
            }
        }
    }
}
