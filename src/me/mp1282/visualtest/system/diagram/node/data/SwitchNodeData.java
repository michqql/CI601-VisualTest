package me.mp1282.visualtest.system.diagram.node.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Per-node state for {@link me.mp1282.visualtest.system.inbuilt.special.SwitchExecutable}.
 * Holds one {@link SwitchCase} per user-configured case. The number of cases is variable
 * and matches the case count baked into the owning {@code SwitchExecutable} instance.
 */
public class SwitchNodeData extends NodeData {

    private final ObservableList<SwitchCase> cases;

    public SwitchNodeData(int initialCaseCount) {
        cases = FXCollections.observableArrayList();
        for (int i = 0; i < initialCaseCount; i++) {
            cases.add(new SwitchCase());
        }
    }

    /** Returns the observable list of cases. Length matches the node's execution path count. */
    public ObservableList<SwitchCase> getCases() {
        return cases;
    }
}
