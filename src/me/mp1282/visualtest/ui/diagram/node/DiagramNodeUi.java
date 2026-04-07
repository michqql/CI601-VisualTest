package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.node.skin.SkinFactory;
import me.mp1282.visualtest.ui.other.ISelectableUi;
import me.mp1282.visualtest.util.ObservableBounds;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An ExecutableBackedUi has a DiagramNode as a model.
 * The UI component reflects the Diagram node's state.
 */
public class DiagramNodeUi extends Control implements IDiagramElement, ISelectableUi {

    protected final DiagramNode node;

    /* Properties */
    protected final BooleanProperty              selected;
    protected final BooleanProperty              skipped;
    protected final DoubleProperty               width;
    protected final DoubleProperty               height;
    protected final ObjectProperty<IDataPort<?>> hoveredDataPort;

    protected final Map<IDataPort<?>, ObservableBounds> dataPortToAreaMap;
    protected final Map<Integer, ObservableBounds> executionPathPortToAreaMap;

    private Runnable onPortsChanged;

    public DiagramNodeUi(final DiagramNode node) {
        this.node                        = node;
        this.selected                    = new SimpleBooleanProperty();
        this.skipped                     = new SimpleBooleanProperty();
        this.width                       = new SimpleDoubleProperty();
        this.height                      = new SimpleDoubleProperty();
        this.hoveredDataPort             = new SimpleObjectProperty<>();
        this.dataPortToAreaMap           = createDataPortToAreaMap(); /* Creates an unmodifiable map that is fully populated */
        this.executionPathPortToAreaMap  = createExecutionPathPortAreaMap();

        /* Bind the translation and dimension properties of this UI component
         * to the model (DiagramNode) so that changes are reflected.
         *
         * The direction has to be layoutXProperty.bind(node.xProperty) because
         * sometimes the node's property will have a value first (such as when loading
         * a saved project), and reversing the statement will overwrite the node's
         * property with zero.
         */
        layoutXProperty().bindBidirectional(node.xProperty());
        layoutYProperty().bindBidirectional(node.yProperty());
        /* TODO: Find a way to flip this expression because this will overwrite the node's properties */
        node.widthProperty().bind(widthProperty());   /* node.width  = this.width  */
        node.heightProperty().bind(heightProperty()); /* node.height = this.height */
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return SkinFactory.createSkin(this);
    }

    public DiagramNode getNode() {
        return node;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public BooleanProperty skippedProperty() {
        return skipped;
    }

    public void setSkipped(boolean value) {
        skipped.set(value);
    }

    public ObjectProperty<IDataPort<?>> hoveredDataPortProperty() {
        return hoveredDataPort;
    }

    public void setExecutionPathPortArea(int branchIndex, Bounds sceneBounds) {
        if (getParent() == null)
            return;
        ObservableBounds entry = executionPathPortToAreaMap.get(branchIndex);
        if (entry == null)
            return; /* port was removed from the map before the old skin finished disposing */
        Bounds parentBounds = getParent().sceneToLocal(sceneBounds);
        entry.rawBoundsProperty().set(parentBounds);
    }

    public ObservableBounds getExecutionPathPortAreaProperty(int branchIndex) {
        return executionPathPortToAreaMap.get(branchIndex);
    }

    public void setDataPortArea(IDataPort<?> dataPort, Bounds sceneBounds) {
        /* If this DiagramNodeUi does not have a parent - ignore this call */
        if(getParent() == null)
            return;

        /* Convert bounds from scene to parent */
        Bounds parentBounds = getParent().sceneToLocal(sceneBounds);
        dataPortToAreaMap.get(dataPort).rawBoundsProperty().set(parentBounds);
    }

    public ObservableBounds getDataPortAreaProperty(IDataPort<?> dataPort) {
        return dataPortToAreaMap.get(dataPort);
    }

    /**
     * Sets a callback that is fired after {@link #refreshExecutionPaths(int)} rebuilds
     * the execution-path ports. Used by {@link me.mp1282.visualtest.ui.diagram.DiagramUi}
     * to trigger a connector-line rebuild when a node's port count changes.
     */
    public void setOnPortsChanged(Runnable callback) {
        this.onPortsChanged = callback;
    }

    /**
     * Updates the execution-path port area map to reflect a new outgoing port count and
     * re-creates the skin so the new ports are rendered immediately.
     * Existing {@link ObservableBounds} instances are reused where possible so that any
     * connector lines already bound to them keep working.
     */
    public void refreshExecutionPaths(int newOutCount) {
        /* Remove entries for ports that no longer exist */
        executionPathPortToAreaMap.keySet().removeIf(k -> k >= 0 && k >= newOutCount);
        /* Add entries for newly added ports */
        for (int i = 0; i < newOutCount; i++)
            executionPathPortToAreaMap.putIfAbsent(i, new ObservableBounds());
        /* Re-create the skin so it renders the updated port list */
        setSkin(SkinFactory.createSkin(this));
        if (onPortsChanged != null)
            onPortsChanged.run();
    }

    private Map<Integer, ObservableBounds> createExecutionPathPortAreaMap() {
        Map<Integer, ObservableBounds> map = new HashMap<>();
        map.put(-1, new ObservableBounds());
        int outCount = node.getNodeAfterPaths().size();
        for (int i = 0; i < outCount; i++)
            map.put(i, new ObservableBounds());
        return map; /* mutable — refreshExecutionPaths() may add/remove entries */
    }

    private Map<IDataPort<?>, ObservableBounds> createDataPortToAreaMap() {
        Map<IDataPort<?>, ObservableBounds> map = new HashMap<>();

        for (InputParameter input : node.getInputs()) {
            map.put(input, new ObservableBounds());
        }

        for (OutputReturn output : node.getOutputs()) {
            map.put(output, new ObservableBounds());
        }

        return Collections.unmodifiableMap(map);
    }
}
