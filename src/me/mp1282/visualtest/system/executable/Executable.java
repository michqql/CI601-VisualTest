package me.mp1282.visualtest.system.executable;

import javafx.scene.input.DataFormat;
import me.mp1282.visualtest.util.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Executable extends Identifiable {

    public static final DataFormat DTO_DATA_FORMAT = new DataFormat(
            Executable.class.getPackageName() + "." +
                    Executable.class.getSimpleName()
    );

    private List<DataPort> inputs;
    private List<DataPort> outputs;

    public final void init() {
        List<DataPort> localInputs  = new ArrayList<>();
        List<DataPort> localOutputs = new ArrayList<>();

        /* Inputs and outputs must be populated by the child classes */
        populateInputOutputDataPorts(localInputs, localOutputs);

        /* Set the inputs and outputs as unmodifiable collections */
        this.inputs  = Collections.unmodifiableList(localInputs);
        this.outputs = Collections.unmodifiableList(localOutputs);

        /* Allow subclasses to set up other things */
        setup();
    }

    /* Methods for subclasses to override */
    public abstract void execute(Object[] inputs, Object[] outputs) throws Exception;
    public abstract String getName();
    protected abstract void populateInputOutputDataPorts(List<DataPort> inputs, List<DataPort> outputs);
    protected void setup() {}
    public Map<String, String> getInformationMap() { return null; }

    /* Getters */
    public int getNumberOfInputs() {
        return this.inputs.size();
    }

    public List<DataPort> getInputs() {
        return this.inputs;
    }

    public int getNumberOfOutputs() {
        return this.outputs.size();
    }

    public List<DataPort> getOutputs() {
        return this.outputs;
    }
}
