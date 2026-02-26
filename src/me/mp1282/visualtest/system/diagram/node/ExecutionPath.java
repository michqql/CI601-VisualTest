package me.mp1282.visualtest.system.diagram.node;

public class ExecutionPath implements IPort {

    private final DiagramNode parent;
    private DiagramNode other;

    public ExecutionPath(DiagramNode parent) {
        this.parent = parent;
    }

    @Override
    public DiagramNode getParentNode() {
        return parent;
    }

    public DiagramNode getOther() {
        return other;
    }

    public void setOther(DiagramNode other) {
        this.other = other;
    }
}
