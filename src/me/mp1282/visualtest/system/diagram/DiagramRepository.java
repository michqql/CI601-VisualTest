package me.mp1282.visualtest.system.diagram;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class DiagramRepository {

    private final ObservableList<Diagram> diagrams = FXCollections.observableArrayList();
    private final ObjectProperty<Diagram> selectedDiagram = new SimpleObjectProperty<>();
    private final HashMap<Diagram, DiagramRefExecutable> diagramToReferenceExecutableMap = new HashMap<>();

    public ObservableList<Diagram> getDiagrams() {
        return diagrams;
    }

    public ObjectProperty<Diagram> selectedDiagramProperty() {
        return selectedDiagram;
    }

    public DiagramRefExecutable getReferenceExecutable(Diagram diagram) {
        return diagramToReferenceExecutableMap.computeIfAbsent(diagram, diagram1 -> {
            DiagramRefExecutable exe = new DiagramRefExecutable(diagram);
            exe.init();
            return exe;
        });
    }
}
