package me.mp1282.visualtest.ui.project;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.ui.other.DiagramListCell;

public class DiagramInfoUi extends VBox {

    public DiagramInfoUi() {
        setPadding(new Insets(20));

        final DiagramRepository diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();

        final ComboBox<Diagram> diagramSelectComboBox = new ComboBox<>(diagramRepository.getDiagrams());
        diagramSelectComboBox.setCellFactory(_ -> new DiagramListCell());
        diagramSelectComboBox.setButtonCell(new DiagramListCell());
        diagramSelectComboBox.setMaxWidth(Double.MAX_VALUE);
        diagramSelectComboBox.setPromptText("Select a diagram...");

        getChildren().add(diagramSelectComboBox);
    }
}
