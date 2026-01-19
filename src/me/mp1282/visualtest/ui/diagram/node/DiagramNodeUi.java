package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.ui.executable.ExecutableUi;

/* An ExecutableBackedUi has a DiagramNode as a model.
 * The UI component reflects the Diagram node's state.
 */
public class DiagramNodeUi extends ExecutableUi {

    private final DiagramNode node;

    protected final ObjectProperty<DataPortArea> hoveredDataPortProperty;

    private double lastMouseX, lastMouseY; /* Scene coordinates */

    public DiagramNodeUi(final DiagramNode node) {
        super(node.getExecutable());
        this.node = node;

        this.hoveredDataPortProperty = new SimpleObjectProperty<>();

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
        hoveredDataPortProperty     .addListener((_, _, _) -> requestRedraw());
        hoverProperty             ().addListener((_, _, _) -> requestRedraw());
        node.executionCostProperty().addListener((_, _, _) -> requestRedraw());

        /* Set a default dimension of 150x150 */
        setWidth(150);
        setHeight(150);

        /* Add event handlers/filters */
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        addEventHandler(MouseEvent.MOUSE_MOVED,   this::handleMouseMove   );
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DiagramNodeUiSkin(this);
    }

    public DiagramNode getNode() {
        return node;
    }

    public DataPortArea getHoveredPort() {
        return hoveredDataPortProperty.get();
    }

    private void handleMousePressed(MouseEvent e) {
        /* Set an initial position for the drag handling */
        lastMouseX = e.getSceneX();
        lastMouseY = e.getSceneY();
        e.consume();
    }

    private void handleMouseMove(MouseEvent e) {
        /* For each data port, check if the user is currently hovering,
         * if so set the hovered property.
         */
        for(DataPortArea area : cachedPortAreas) {
            if(area.isInside(e.getX(), e.getY())) {
                hoveredDataPortProperty.set(area);
                return;
            }
        }

        /* No data port is being hovered if the code has reached here,
         * thus set the hovered property to null
         */
        hoveredDataPortProperty.set(null);
    }

    private void handleMouseDragged(MouseEvent e) {
        /* Translation requires primary mouse button */
        if(e.isPrimaryButtonDown()) {
            /* Only allow translation if the user is not hovering over a data port */
            if(hoveredDataPortProperty.get() == null) {
                final double deltaX = e.getSceneX() - lastMouseX;
                final double deltaY = e.getSceneY() - lastMouseY;

                layoutXProperty().set(layoutXProperty().get() + deltaX);
                layoutYProperty().set(layoutYProperty().get() + deltaY);

                lastMouseX = e.getSceneX();
                lastMouseY = e.getSceneY();
                e.consume();
            }
        }
    }
}
