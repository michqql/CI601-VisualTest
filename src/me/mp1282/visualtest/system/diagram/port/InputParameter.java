package me.mp1282.visualtest.system.diagram.port;

import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.executable.data.ParameterType;

public class InputParameter implements IDataPort<ParameterType> {

    private final DiagramNode parent;
    private final ParameterType type;

    private OutputReturn from; /* The input parameter data comes from this output data */

    public InputParameter(DiagramNode parent, ParameterType type) {
        this.parent = parent;
        this.type = type;
    }

    @Override
    public DiagramNode getParentNode() {
        return parent;
    }

    @Override
    public ParameterType getType() {
        return type;
    }

    public OutputReturn getFrom() {
        return from;
    }

    public void setFrom(OutputReturn from) {
        this.from = from;
    }

    @Override
    public IDataPort<?> getOther() {
        return getFrom();
    }

    @Override
    public void disconnect() {
        setFrom(null);
    }
}
