package me.mp1282.visualtest.system.diagram.node.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RangeCheckNodeData extends NodeData {

    private final ObjectProperty<Class<? extends Number>> type = new SimpleObjectProperty<>();
    private final ObjectProperty<Number>                  min  = new SimpleObjectProperty<>();
    private final ObjectProperty<Number>                  max  = new SimpleObjectProperty<>();

    public ObjectProperty<Class<? extends Number>> typeProperty() {
        return type;
    }

    public ObjectProperty<Number> minProperty() {
        return min;
    }

    public ObjectProperty<Number> maxProperty() {
        return max;
    }
}