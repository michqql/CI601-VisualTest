package me.mp1282.visualtest.system.diagram;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class DiagramRepository {

    private final ObservableList<Diagram> diagrams = FXCollections.observableArrayList();
    private final IntegerProperty numberOfDiagrams = new SimpleIntegerProperty();
    private final ObjectProperty<Diagram> selectedDiagram = new SimpleObjectProperty<>();
    private final HashMap<Diagram, DiagramRefExecutable> diagramToReferenceExecutableMap = new HashMap<>();

    public DiagramRepository() {
        diagrams.addListener((ListChangeListener<? super Diagram>) _ -> numberOfDiagrams.set(diagrams.size()));
    }

    public ObservableList<Diagram> getDiagrams() {
        return diagrams;
    }

    public ReadOnlyIntegerProperty numberOfDiagramsProperty() {
        return numberOfDiagrams;
    }

    public ObjectProperty<Diagram> selectedDiagramProperty() {
        return selectedDiagram;
    }

    public DiagramRefExecutable getReferenceExecutable(Diagram diagram) {
        return diagramToReferenceExecutableMap.computeIfAbsent(diagram, _ -> {
            DiagramRefExecutable exe = new DiagramRefExecutable(diagram);
            exe.init();
            return exe;
        });
    }
}
