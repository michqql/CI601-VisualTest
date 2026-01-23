package me.mp1282.visualtest.system.diagram.runtime;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;

import java.util.ArrayList;
import java.util.List;

public class RunStep {
    private final Diagram diagram;
    private final DiagramNode node;

    private final List<ObjectSnapshot> inputSnapshots;
    private final List<ObjectSnapshot> outputSnapshots;

    public RunStep(Diagram diagram, DiagramNode node) {
        this.diagram = diagram;
        this.node = node;

        this.inputSnapshots = new ArrayList<>();
        this.outputSnapshots = new ArrayList<>();
    }

    public void setInputs(Object[] inputs) {
        for (Object input : inputs) {
            inputSnapshots.add(new ObjectSnapshot(input));
        }
    }

    public void setOutputs(Object[] outputs) {
        for (Object output : outputs) {
            outputSnapshots.add(new ObjectSnapshot(output));
        }
    }
}
