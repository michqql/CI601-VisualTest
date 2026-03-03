package me.mp1282.visualtest.system.diagram.port;

import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.executable.data.ReturnType;

public class OutputReturn implements IDataPort<ReturnType> {

    private final DiagramNode parent;
    private final ReturnType type;

    private InputParameter to; /* The output data goes to this input parameter */

    public OutputReturn(DiagramNode parent, ReturnType type) {
        this.parent = parent;
        this.type = type;
    }

    @Override
    public DiagramNode getParentNode() {
        return parent;
    }

    @Override
    public ReturnType getType() {
        return type;
    }

    public InputParameter getTo() {
        return to;
    }

    public void setTo(InputParameter to) {
        this.to = to;
    }
}
