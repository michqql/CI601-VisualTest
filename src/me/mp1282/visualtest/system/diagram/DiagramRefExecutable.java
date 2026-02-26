package me.mp1282.visualtest.system.diagram;

import me.mp1282.visualtest.system.diagram.node.IDataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.data.IDataType;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;

import java.util.List;

public class DiagramRefExecutable extends Executable {

    private final Diagram diagram;

    /* Package-private: This class should only be instantiated via the DiagramRepository */
    DiagramRefExecutable(Diagram diagram) {
        this.diagram = diagram;
    }

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        List<IDataPort<?>> unconnectedPorts = diagram.getDataPortsWithoutConnections();

        for (IDataPort<? extends IDataType> port : unconnectedPorts) {
            /* Add the unconnected port to this executable's inputs/outputs */
            if(port.getType().getType() == IDataType.Type.PARAMETER) {
                parameterTypes.add((ParameterType) port.getType());
            } else {
                returnTypes.add((ReturnType) port.getType());
            }
        }
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs) {

    }

    @Override
    public String getName() {
        return diagram.nameProperty().get();
    }

    @Override
    protected String getNamespace() {
        return "";
    }
}
