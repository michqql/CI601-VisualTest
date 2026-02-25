package me.mp1282.visualtest.ui.project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.ui.other.DiagramListCell;
import me.mp1282.visualtest.ui.other.GridPane2dInfoUi;

public class DiagramInfoUi extends VBox {

    private final ObjectProperty<Diagram> selectedDiagram;

    public DiagramInfoUi() {
        this.selectedDiagram = new SimpleObjectProperty<>();

        setPadding(new Insets(20));

        final DiagramRepository diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        final ComboBox<Diagram> diagramSelectComboBox = new ComboBox<>(diagramRepository.getDiagrams());
        diagramSelectComboBox.setCellFactory(_ -> new DiagramListCell());
        diagramSelectComboBox.setButtonCell(new DiagramListCell());
        diagramSelectComboBox.setMaxWidth(Double.MAX_VALUE);
        diagramSelectComboBox.setPromptText("Select a diagram...");
        selectedDiagram.bind(diagramSelectComboBox.valueProperty());
        setMargin(diagramSelectComboBox,new Insets(
                /* Top    => */ 0,
                /* Right  => */ 0,
                /* Bottom => */ 5,
                /* Left   => */ 0
        ));

        getChildren().addAll(diagramSelectComboBox, createGridPane());
    }

    private GridPane2dInfoUi createGridPane() {
        final GridPane2dInfoUi gridPane = new GridPane2dInfoUi(5, 10);

        gridPane.addInfoEntry("No. Nodes", () -> {
            final Text numNodesText = new Text();
            numNodesText.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                        Diagram diagram = selectedDiagram.get();
                        if(diagram == null) {
                            return "0";
                        }
                        return String.valueOf(diagram.nodesProperty().size());
                    }, selectedDiagram)
            );
            return numNodesText;
        });
        return gridPane;
    }
}
