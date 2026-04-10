package me.mp1282.visualtest.ui.diagram.runtime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.system.diagram.runtime.RunStep;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironmentService;

/**
 * Bridges the shared {@link RuntimeEnvironmentService} to a single diagram's UI layer.
 * Filters the global run-step list to only steps for this diagram, and tracks when
 * the diagram's task completes so the canvas overlay can be updated.
 *
 * Must be used on the JavaFX thread only.
 */
public class DiagramExecutionContext {

    private final Diagram diagram;
    private final ObservableList<RunStep> runStepList;
    private final ObjectProperty<ExecuteTask> lastCompletedTask;

    public DiagramExecutionContext(Diagram diagram, RuntimeEnvironmentService runtime) {
        this.diagram = diagram;
        this.runStepList = FXCollections.observableArrayList();
        this.lastCompletedTask = new SimpleObjectProperty<>();

        /* Filter incoming run steps to only those belonging to this diagram */
        runtime.getRunStepList().addListener((ListChangeListener<RunStep>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (RunStep step : change.getAddedSubList()) {
                        if (step.getDiagram() == diagram)
                            runStepList.add(step);
                    }
                }
                if (change.wasRemoved()) {
                    runStepList.clear();
                }
            }
        });

        /* Track task completion for this diagram */
        runtime.lastCompletedTaskProperty().addListener((_, _, completedTask) -> {
            if (completedTask != null && completedTask.getDiagram() == diagram)
                lastCompletedTask.set(completedTask);
        });
    }

    public ObservableList<RunStep> getRunStepList() {
        return runStepList;
    }

    public ObjectProperty<ExecuteTask> lastCompletedTaskProperty() {
        return lastCompletedTask;
    }

    public void clearResults() {
        runStepList.clear();
        lastCompletedTask.set(null);
    }
}
