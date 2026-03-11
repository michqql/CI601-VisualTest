package me.mp1282.visualtest.system.diagram.node.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ConstantNodeData extends NodeData {

    private final ObjectProperty<Object> constant = new SimpleObjectProperty<>();

    public ObjectProperty<Object> constantProperty() {
        return constant;
    }
}
