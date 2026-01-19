package me.mp1282.visualtest.system.diagram;

import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DiagramRefExecutable extends Executable {

    private final Diagram diagram;

    /* Package-private: This class should only be instantiated via the DiagramRepository */
    DiagramRefExecutable(Diagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs) {

    }

    @Override
    protected void populateInputOutputDataPorts(List<DataPort> inputs, List<DataPort> outputs) {
        List<Pair<DiagramNode, DataPort>> unconnectedPorts =
                diagram.getDataPortsWithoutConnections();

        for(Pair<DiagramNode, DataPort> pair : unconnectedPorts) {
            DataPort port = pair.value();
            /* Add the unconnected port to this executable's inputs/outputs */
            if(port.inputPort()) {
                inputs.add(port);
            } else {
                outputs.add(port);
            }
        }
    }

    @Override
    public String getName() {
        return diagram.nameProperty().get();
    }
}
