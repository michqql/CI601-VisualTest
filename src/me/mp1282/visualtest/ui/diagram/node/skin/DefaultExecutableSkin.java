package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.UiPreferencesService;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.node.skin.component.DataPortComponent;
import me.mp1282.visualtest.ui.diagram.node.skin.component.ExecutionPathPortComponent;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;
import me.mp1282.visualtest.ui.event.ExecutableBodyMouseEvent;

public class DefaultExecutableSkin extends SkinBase<DiagramNodeUi> {

    private Node skinRoot;

    public DefaultExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
        skinRoot = createRoot();
        getChildren().add(skinRoot);
    }

    /**
     * Only removes the root node that THIS skin instance added.
     * Using getChildren().clear() would also wipe any children already added by the
     * incoming skin when refreshExecutionPaths() pre-constructs it before calling setSkin().
     */
    @Override
    public void dispose() {
        getChildren().remove(skinRoot);
        skinRoot = null;
        super.dispose();
    }

    protected Node createRoot() {
        final DiagramNodeUi nodeUi = getSkinnable();
        final boolean vertical = UiPreferencesService.getInstance().verticalOrientationProperty().get();
        final Executable exe = nodeUi.getNode().getExecutable();

        /* Inputs container: left column (horizontal) or top row (vertical) */
        final Pane inputs;
        if (vertical) {
            HBox hb = new HBox(5);
            hb.setAlignment(Pos.BOTTOM_CENTER);
            inputs = hb;
        } else {
            VBox vb = new VBox(5);
            vb.setAlignment(Pos.CENTER_RIGHT);
            inputs = vb;
        }
        inputs.getChildren().add(execPathPortNode(exe, -1));
        for (IDataPort<?> input : nodeUi.getNode().getInputs())
            inputs.getChildren().add(dataPortNode(input, true));

        /* Outputs container: right column (horizontal) or bottom row (vertical).
         * Use nodeAfterPaths.size() rather than getExecutionPathOutputCount() so that
         * nodes whose port list was resized after construction render correctly. */
        final Pane outputs;
        if (vertical) {
            HBox hb = new HBox(5);
            hb.setAlignment(Pos.TOP_CENTER);
            outputs = hb;
        } else {
            VBox vb = new VBox(5);
            vb.setAlignment(Pos.CENTER_LEFT);
            outputs = vb;
        }
        for (int i = 0; i < nodeUi.getNode().getNodeAfterPaths().size(); i++)
            outputs.getChildren().add(execPathPortNode(exe, i));
        for (IDataPort<?> output : nodeUi.getNode().getOutputs())
            outputs.getChildren().add(dataPortNode(output, false));

        /* Main content */
        final StackPane main = new StackPane();
        main.setPadding(new Insets(5));
        final Border normalBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3)));
        final Border selectedBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3)));
        main.borderProperty().bind(nodeUi.selectedProperty().map(sel -> sel ? selectedBorder : normalBorder));
        createMainBody(main);

        /* Root container: HBox for horizontal flow, VBox for vertical flow */
        final Pane root = vertical ? new VBox() : new HBox();
        root.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        /* Capture all mouse events on this pane, and fire them on the DiagramNodeUi */
        root.addEventHandler(MouseEvent.ANY, event -> {
            final DiagramNodeUi ui = getSkinnable();
            ui.fireEvent(new ExecutableBodyMouseEvent(ExecutableBodyMouseEvent.ANY_MOUSE, ui, event));
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
                ui.fireEvent(new ExecutableBodyMouseEvent(ExecutableBodyMouseEvent.CLICK, ui, event));
            else if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
                ui.fireEvent(new ExecutableBodyMouseEvent(ExecutableBodyMouseEvent.PRESSED, ui, event));
        });

        root.getChildren().addAll(inputs, main, outputs);
        root.opacityProperty().bind(nodeUi.skippedProperty().map(s -> s ? 0.35 : 1.0));
        return root;
    }

    /**
     * Creates the UI node for an execution path port. If the executable provides a custom label
     * for this port, it is shown as a persistent text label. Otherwise, a hover-visible text
     * label showing "IN" or "OUT" is displayed when the node is hovered.
     *
     * Port placement follows the current orientation:
     *   Horizontal — port at the outer edge, label toward the node centre (HBox wrapper).
     *   Vertical   — port at the outer edge (top for IN, bottom for OUT), label toward centre (VBox wrapper).
     */
    protected Node execPathPortNode(Executable exe, int branchIndex) {
        final boolean vertical = UiPreferencesService.getInstance().verticalOrientationProperty().get();
        final ExecutionPathPortComponent port = new ExecutionPathPortComponent(this, branchIndex);
        final String customLabel = exe.getExecutionPathLabel(branchIndex);

        final Text label = new Text(customLabel != null ? customLabel : (branchIndex < 0 ? "IN" : "OUT"));
        label.setStyle("-fx-font-size: 10;");
        label.setMouseTransparent(true);
        if (customLabel == null)
            label.visibleProperty().bind(getSkinnable().hoverProperty());

        if (vertical) {
            final VBox wrapper = new VBox(2);
            if (branchIndex < 0) /* Input: port at top edge, label toward centre */
                wrapper.getChildren().addAll(port, label);
            else                 /* Output: label toward centre, port at bottom edge */
                wrapper.getChildren().addAll(label, port);
            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        } else {
            final HBox wrapper = new HBox(2);
            if (branchIndex < 0) /* Input: port at left edge, label toward centre */
                wrapper.getChildren().addAll(port, label);
            else                 /* Output: label toward centre, port at right edge */
                wrapper.getChildren().addAll(label, port);
            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        }
    }

    private Node dataPortNode(IDataPort<?> port, boolean isInput) {
        final boolean vertical = UiPreferencesService.getInstance().verticalOrientationProperty().get();
        final DataPortComponent portComp = new DataPortComponent(this, port);
        final Text label = new Text(port.getType().getDataType().getSimpleName());
        label.setStyle("-fx-font-size: 10;");
        label.setMouseTransparent(true);
        label.visibleProperty().bind(getSkinnable().hoverProperty());

        if (vertical) {
            final VBox wrapper = new VBox(2);
            if (isInput) /* Port at top edge, label toward centre */
                wrapper.getChildren().addAll(portComp, label);
            else         /* Label toward centre, port at bottom edge */
                wrapper.getChildren().addAll(label, portComp);
            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        } else {
            final HBox wrapper = new HBox(2);
            if (isInput) /* Port at left edge, label toward centre */
                wrapper.getChildren().addAll(portComp, label);
            else         /* Label toward centre, port at right edge */
                wrapper.getChildren().addAll(label, portComp);
            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        }
    }

    protected void createMainBody(StackPane parent) {
        final Text exeName = new Text(getSkinnable().getNode().getExecutable().getName());
        parent.getChildren().add(exeName);
    }
}
