package me.mp1282.visualtest.system.diagram.node.data;

import javafx.beans.InvalidationListener;

public class NodeData {

    /**
     * Attaches the given listener to all observable properties in this node data so that
     * any change marks the owning diagram as unsaved. The base implementation is a no-op;
     * subclasses override this to wire their own properties.
     */
    public void addInvalidationListener(InvalidationListener listener) { /* no-op */ }
}
