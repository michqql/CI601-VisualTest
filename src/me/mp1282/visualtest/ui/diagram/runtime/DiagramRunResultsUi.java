package me.mp1282.visualtest.ui.diagram.runtime;

import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.diagram.runtime.RunStep;
import me.mp1282.visualtest.util.PropertyHelper;

/**
 * Embedded execution results panel shown below the diagram canvas in each tab.
 * Shows the list of executed steps and details about the selected step.
 */
public class DiagramRunResultsUi extends VBox {

    private final ListView<RunStep> runStepList;
    private final RunStepInfoUi runStepInfo;

    public DiagramRunResultsUi(DiagramExecutionContext context) {
        this.runStepList = new ListView<>();
        this.runStepInfo = new RunStepInfoUi();

        runStepList.setCellFactory(_ -> new RunStepCell());
        runStepList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        runStepList.getSelectionModel().selectedItemProperty()
                .addListener((_, _, step) -> { if (step != null) runStepInfo.setRunStep(step); });

        PropertyHelper.bindList(context.getRunStepList(), runStepList.getItems());

        SplitPane content = new SplitPane(runStepList, runStepInfo);
        content.setDividerPositions(0.3);
        VBox.setVgrow(content, Priority.ALWAYS);
        getChildren().add(content);
    }

    private static class RunStepCell extends ListCell<RunStep> {
        @Override
        protected void updateItem(RunStep step, boolean empty) {
            super.updateItem(step, empty);
            if (step == null || empty) {
                setText(null);
            } else {
                setText((getIndex() + 1) + ". " + step.getNode().getExecutable().getName());
            }
        }
    }
}
