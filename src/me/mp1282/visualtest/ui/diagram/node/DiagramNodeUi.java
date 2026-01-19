package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.executable.ExecutableUi;

/* An ExecutableBackedUi has a DiagramNode as a model.
 * The UI component reflects the Diagram node's state.
 */
public class DiagramNodeUi extends ExecutableUi {

    private final DiagramNode node;
    /* Properties */
    protected final ObjectProperty<DataPortArea> hoveredDataPort;

    public DiagramNodeUi(final DiagramNode node) {
        super(node.getExecutable());
        this.node = node;
        this.hoveredDataPort = new SimpleObjectProperty<>();

        /* Bind the translation and dimension properties of this UI component
         * to the model (DiagramNode) so that changes are reflected.
         */
        node.xProperty().bindBidirectional(layoutXProperty());
        node.yProperty().bindBidirectional(layoutYProperty());
        node.widthProperty().bind(widthProperty());   /* node.width  = this.width  */
        node.heightProperty().bind(heightProperty()); /* node.height = this.height */

        /* Tell the UI to redraw when:
         * - the hovered data port changes
         * - the Ui is hovered
         * - the execution cost changes
         */
        hoveredDataPort             .addListener((_, _, _) -> requestRedraw());
        hoverProperty             ().addListener((_, _, _) -> requestRedraw());
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
}
