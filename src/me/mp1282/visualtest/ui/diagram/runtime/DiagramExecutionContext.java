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

import java.util.ArrayList;
import java.util.List;

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

        /* Filter incoming run steps to only those belonging to this diagram.
         *
         * bindListThreadSafe uses setAll(), which JavaFX batches into a single REPLACE
         * change (wasAdded && wasRemoved both true). Handling wasAdded then wasRemoved
         * in the same iteration would add duplicates and then wipe the list. Instead:
         *   - pure ADD  → append only the new steps
         *   - anything with a REMOVE (replace or clear) → rebuild from the full source
         */
        runtime.getRunStepList().addListener((ListChangeListener<RunStep>) change -> {
            while (change.next()) {
                if (change.wasAdded() && !change.wasRemoved()) {
                    for (RunStep step : change.getAddedSubList()) {
                        if (step.getDiagram() == diagram)
                            runStepList.add(step);
                    }
                } else if (change.wasRemoved()) {
                    List<RunStep> filtered = new ArrayList<>();
                    for (RunStep step : change.getList()) {
                        if (step.getDiagram() == diagram)
                            filtered.add(step);
                    }
                    runStepList.setAll(filtered);
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
