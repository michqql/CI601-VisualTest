package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.*;
import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.executable.ExecutableUi;
import me.mp1282.visualtest.ui.other.ISelectableUi;

/* An ExecutableBackedUi has a DiagramNode as a model.
 * The UI component reflects the Diagram node's state.
 */
public class DiagramNodeUi extends ExecutableUi implements ISelectableUi, IDiagramElement {

    private final DiagramNode node;
    /* Properties */
    protected final ObjectProperty<DataPortArea> hoveredDataPort;
    protected final BooleanProperty selected;
    protected final DoubleProperty width;
    protected final DoubleProperty height;

    public DiagramNodeUi(final DiagramNode node) {
        super(node.getExecutable());
        this.node = node;
        this.hoveredDataPort = new SimpleObjectProperty<>();
        this.selected = new SimpleBooleanProperty();
        this.width = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();

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

        /* Tell the UI to redraw when:
         * - the hovered data port changes
         * - the UI is hovered
         * - the UI is selected
         * - the execution cost changes
         */
        hoveredDataPort             .addListener((_, _, _) -> requestRedraw());
        hoverProperty()             .addListener((_, _, _) -> requestRedraw());
        selected                    .addListener((_, _, _) -> requestRedraw());
        node.executionCostProperty().addListener((_, _, _) -> requestRedraw());

        /* Set a default dimension of 150x150 */
        setWidth(110);
        setHeight(110);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DiagramNodeUiSkin(this);
    }

    public DiagramNode getNode() {
        return node;
    }

    public ObjectProperty<DataPortArea> hoveredDataPortProperty() {
        return hoveredDataPort;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    public int getZOrder() {
        return 0;
    }

    public void setWidth(double width) {
        super.setWidth(width);
    }

    public void setHeight(double height) {
        super.setHeight(height);
    }
}
