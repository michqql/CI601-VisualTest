package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    public DiagramNodeUi(final DiagramNode node) {
        super(node.getExecutable());
        this.node = node;
        this.hoveredDataPort = new SimpleObjectProperty<>();
        this.selected = new SimpleBooleanProperty();

        /* Bind the translation and dimension properties of this UI component
         * to the model (DiagramNode) so that changes are reflected.
         */
        node.xProperty().bindBidirectional(layoutXProperty());
        node.yProperty().bindBidirectional(layoutYProperty());
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
        setWidth(150);
        setHeight(150);
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
}
