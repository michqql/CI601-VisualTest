package me.mp1282.visualtest.ui.diagram.runtime;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.system.diagram.runtime.RunStep;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironmentService;
import me.mp1282.visualtest.ui.other.DiagramListCell;
import me.mp1282.visualtest.util.PropertyHelper;

public class RuntimeUi extends VBox {

    private final DiagramRepository diagramRepository;
    private final RuntimeEnvironmentService runtime;

    private final ComboBox<Diagram> diagramSelectComboBox;
    private final CheckBox stepModeCheckBox;
    private final Button stepButton;
    private final Button runDiagramButton;

    private final ListView<RunStep> runStepList;
    private final RunStepInfoUi runStepInfo;

    public RuntimeUi() {
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        this.runtime = VisualTestSystem.getInstance().getRuntimeEnvironmentService();

        this.diagramSelectComboBox = new ComboBox<>(diagramRepository.getDiagrams());
        this.stepModeCheckBox = new CheckBox("Step Mode");
        this.stepButton = new Button("Step");
        this.runDiagramButton = new Button("Run Diagram");

        this.runStepList = new ListView<>();
        this.runStepInfo = new RunStepInfoUi();

        diagramSelectComboBox.valueProperty().addListener((_, _, diagram) -> handleDiagramSelect(diagram));
        diagramSelectComboBox.setCellFactory(_ -> new DiagramListCell());
        diagramSelectComboBox.setButtonCell(new DiagramListCell());
        diagramSelectComboBox.setMaxWidth(150);

        runtime.stepModeProperty().bind(stepModeCheckBox.selectedProperty());

        /* Only enable step button if step mode is enabled (checked / selected) */
        stepButton.disableProperty().bind(stepModeCheckBox.selectedProperty().not());
        stepButton.setOnMouseClicked(e -> runtime.setStepFlag());

        /* Only enable run diagram button if a diagram is selected and nothing is currently running */
        runDiagramButton.setOnMouseClicked(e -> handleRunDiagram(diagramSelectComboBox.getValue()));
        runDiagramButton.disableProperty().bind(
                diagramSelectComboBox.valueProperty().isNotNull().and(runtime.runningTaskProperty().not()).not());

        runStepList.setCellFactory(_ -> new RunStepCell());
        runStepList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        runStepList.getSelectionModel().selectedItemProperty().addListener((_, _, runStep) -> handleRunStepSelect(runStep));
        PropertyHelper.bindList(runtime.getRunStepList(), runStepList.getItems());

        HBox group1 = new HBox(10, diagramSelectComboBox, stepModeCheckBox, stepButton, runDiagramButton);
        group1.setPadding(new Insets(10));
        SplitPane group2 = new SplitPane(runStepList, runStepInfo);
        getChildren().addAll(group1, group2);
    }

    private void handleDiagramSelect(Diagram diagram) {

    }

    private void handleRunStepSelect(RunStep step) {
        if (step != null) runStepInfo.setRunStep(step);
    }

    private void handleRunDiagram(Diagram diagram) {
        ExecuteTask task = new ExecuteTask(diagram);
        runtime.queueTask(task);
    }

    private static class RunStepCell extends ListCell<RunStep> {
        @Override
        protected void updateItem(RunStep step, boolean empty) {
            super.updateItem(step, empty);

            if(step == null || empty) {
                setText(null);
            } else {
                setText((getIndex() + 1) + ". " + step.getNode().getExecutable().getName());
            }

        }
    }
}
