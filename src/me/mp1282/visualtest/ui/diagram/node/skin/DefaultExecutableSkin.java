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
        final VBox root = new VBox(); /* The root container */
        root.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        final HBox inputs = new HBox(); /* Container for data port inputs and execution path IN port */
        inputs.setAlignment(Pos.BOTTOM_CENTER);
        inputs.setSpacing(5);
        /* Execution path IN port at the start of the inputs row */
        inputs.getChildren().add(execPathPortNode(nodeUi.getNode().getExecutable(), -1));
        /* Populate input data ports */
        for(IDataPort<?> input : nodeUi.getNode().getInputs()) {
            inputs.getChildren().add(dataPortNode(input, true));
        }

        final HBox outputs = new HBox(); /* Container for data port outputs and execution path OUT port(s) */
        outputs.setAlignment(Pos.TOP_CENTER);
        outputs.setSpacing(5);
        /* Execution path OUT port(s) at the end of the outputs row.
         * Use nodeAfterPaths.size() rather than getExecutionPathOutputCount() so that
         * nodes whose port list was resized after construction render correctly. */
        final Executable exe = nodeUi.getNode().getExecutable();
        for (int i = 0; i < nodeUi.getNode().getNodeAfterPaths().size(); i++)
            outputs.getChildren().add(execPathPortNode(exe, i));

        /* Populate output data ports */
        for(IDataPort<?> output : nodeUi.getNode().getOutputs()) {
            outputs.getChildren().add(dataPortNode(output, false));
        }

        /* Construct the main content */
        final StackPane main = new StackPane(); /* Container for main content */
        main.setPadding(new Insets(5));
        final Border normalBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3)));
        final Border selectedBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3)));
        main.borderProperty().bind(nodeUi.selectedProperty().map(sel -> sel ? selectedBorder : normalBorder));
        createMainBody(main);

        /* Capture all mouse events on this pane, and fire them on the DiagramNodeUi */
        root.addEventHandler(MouseEvent.ANY, event -> {
            final DiagramNodeUi ui = getSkinnable();
            /* Fire generic mouse event */
            ui.fireEvent(new ExecutableBodyMouseEvent(ExecutableBodyMouseEvent.ANY_MOUSE, ui, event));

            /* If was mouse click event, fire mouse click specific event */
            if(event.getEventType() == MouseEvent.MOUSE_CLICKED)
                ui.fireEvent(new ExecutableBodyMouseEvent(ExecutableBodyMouseEvent.CLICK, ui, event));
            else if(event.getEventType() == MouseEvent.MOUSE_PRESSED)
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
     */
    protected Node execPathPortNode(Executable exe, int branchIndex) {
        final ExecutionPathPortComponent port = new ExecutionPathPortComponent(this, branchIndex);
        final String customLabel = exe.getExecutionPathLabel(branchIndex);

        if (customLabel != null) {
            final Text label = new Text(customLabel);
            label.setStyle("-fx-font-size: 10;");
            label.setMouseTransparent(true);
            final VBox wrapper = new VBox(2);
            if(branchIndex >= 0) { /* Output port */
                wrapper.getChildren().addAll(label, port);
            } else { /* Input port */
                wrapper.getChildren().addAll(port, label);
            }

            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        } else {
            final String defaultLabel = branchIndex < 0 ? "IN" : "OUT";
            final Text label = new Text(defaultLabel);
            label.setStyle("-fx-font-size: 10;");
            label.setMouseTransparent(true);
            label.visibleProperty().bind(getSkinnable().hoverProperty());
            final VBox wrapper = new VBox(2);
            if (branchIndex >= 0) {
                wrapper.getChildren().addAll(label, port);
            } else {
                wrapper.getChildren().addAll(port, label);
            }
            wrapper.setAlignment(Pos.CENTER);
            return wrapper;
        }
    }

    private Node dataPortNode(IDataPort<?> port, boolean isInput) {
        final DataPortComponent portComp = new DataPortComponent(this, port);
        final Text label = new Text(port.getType().getDataType().getSimpleName());
        label.setStyle("-fx-font-size: 10;");
        label.setMouseTransparent(true);
        label.visibleProperty().bind(getSkinnable().hoverProperty());
        final VBox wrapper = new VBox(2);
        if (isInput) {
            wrapper.getChildren().addAll(portComp, label);
        } else {
            wrapper.getChildren().addAll(label, portComp);
        }
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    protected void createMainBody(StackPane parent) {
        final Text exeName = new Text(getSkinnable().getNode().getExecutable().getName());
        parent.getChildren().add(exeName);
    }
}
