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
    protected final DoubleProperty               width;
    protected final DoubleProperty               height;
    protected final ObjectProperty<IDataPort<?>> hoveredDataPort;

    protected final Map<IDataPort<?>, ObservableBounds> dataPortToAreaMap;

    public DiagramNodeUi(final DiagramNode node) {
        this.node                = node;
        this.selected            = new SimpleBooleanProperty();
        this.width               = new SimpleDoubleProperty();
        this.height              = new SimpleDoubleProperty();
        this.hoveredDataPort     = new SimpleObjectProperty<>();
        this.dataPortToAreaMap   = createDataPortToAreaMap(); /* Creates an unmodifiable map that is fully populated */

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

    public ObjectProperty<IDataPort<?>> hoveredDataPortProperty() {
        return hoveredDataPort;
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
