package me.mp1282.visualtest.system.diagram;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.IReset;

import java.util.HashMap;

public class DiagramRepository implements IReset {

    private final ObservableList<Diagram> diagrams = FXCollections.observableArrayList();
    private final ObjectProperty<Diagram> selectedDiagram = new SimpleObjectProperty<>();
    private final HashMap<Diagram, DiagramRefExecutable> diagramToReferenceExecutableMap = new HashMap<>();

    public ObservableList<Diagram> getDiagrams() {
        return diagrams;
    }

    public void createDiagram() {
        diagrams.add(new Diagram());
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

    @Override
    public void reset() {
        diagrams.clear();
        selectedDiagram.set(null);
        diagramToReferenceExecutableMap.clear();
    }
}
