package me.mp1282.visualtest.system.diagram.node.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents one case in a {@link SwitchNodeData}. Stores the user-visible
 * label shown on the execution path port, and the string value that is compared
 * against the Switch node's input at runtime.
 */
public class SwitchCase {

    private final StringProperty label = new SimpleStringProperty("");
    private final StringProperty value = new SimpleStringProperty("");

    public StringProperty labelProperty() { return label; }
    public StringProperty valueProperty() { return value; }

    public String getLabel() { return label.get(); }
    public String getValue() { return value.get(); }
}
