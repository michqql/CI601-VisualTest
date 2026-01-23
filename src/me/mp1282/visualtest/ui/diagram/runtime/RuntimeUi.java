package me.mp1282.visualtest.ui.diagram.runtime;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironment;

public class RuntimeUi extends VBox {

    private final DiagramRepository diagramRepository;
    private final RuntimeEnvironment runtime;

    private final ComboBox<Diagram> diagramSelectComboBox;
    private final CheckBox stepModeCheckBox;
    private final Button stepButton;
    private final Button runDiagramButton;

    private final ListView<DiagramNode> nodeList;

    public RuntimeUi() {
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        this.runtime = VisualTestSystem.getInstance().getRuntimeEnvironment();

        this.diagramSelectComboBox = new ComboBox<>(diagramRepository.getDiagrams());
        this.stepModeCheckBox = new CheckBox("Step Mode");
        this.stepButton = new Button("Step");
        this.runDiagramButton = new Button("Run Diagram");

        this.nodeList = new ListView<>();

        diagramSelectComboBox.valueProperty().addListener((_, _, diagram) -> handleDiagramSelect(diagram));
        diagramSelectComboBox.setCellFactory(_ -> new DiagramSelectComboBoxCell());
        diagramSelectComboBox.setButtonCell(new DiagramSelectComboBoxCell());
        diagramSelectComboBox.setMaxWidth(150);

        runtime.stepModeProperty().bind(stepModeCheckBox.selectedProperty());

        /* Only enable step button if step mode is enabled (checked / selected) */
        stepButton.disableProperty().bind(stepModeCheckBox.selectedProperty().not());
        stepButton.setOnMouseClicked(e -> runtime.setStepFlag());

        /* Only enable run diagram button if a diagram is selected and nothing is currently running */
        runDiagramButton.disableProperty().bind(
                diagramSelectComboBox.valueProperty().isNotNull().and(runtime.runningTaskProperty().not()).not());
        runDiagramButton.setOnMouseClicked(e -> handleRunDiagram(diagramSelectComboBox.getValue()));

        nodeList.setCellFactory(_ -> new DiagramNodeListCell());

        HBox group1 = new HBox(10, diagramSelectComboBox, stepModeCheckBox, stepButton, runDiagramButton);
        group1.setPadding(new Insets(10));
        VBox group2 = new VBox(10, nodeList);
        getChildren().addAll(group1, group2);
    }

    private void handleDiagramSelect(Diagram diagram) {

    }

    private void handleRunDiagram(Diagram diagram) {
        ExecuteTask task = new ExecuteTask(diagram);
        runtime.queueTask(task);
    }

    private static class DiagramSelectComboBoxCell extends ListCell<Diagram> {
        @Override
        protected void updateItem(Diagram diagram, boolean empty) {
            super.updateItem(diagram, empty);

            if(diagram == null || empty) {
                setText(null);
            } else {
                setText(diagram.nameProperty().get());
            }
        }
    }

    private static class DiagramNodeListCell extends ListCell<DiagramNode> {
        @Override
        protected void updateItem(DiagramNode node, boolean empty) {
            super.updateItem(node, empty);

            if(node == null || empty) {
                setText(null);
            } else {
                setText((getIndex() + 1) + ". " + node.getExecutable().getName());
            }

        }
    }
}
