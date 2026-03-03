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
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.node.skin.component.DataPortComponent;
import me.mp1282.visualtest.ui.event.DataPortMouseEvent;
import me.mp1282.visualtest.ui.event.ExecutableBodyMouseEvent;

public class DefaultExecutableSkin extends SkinBase<DiagramNodeUi> {

    public DefaultExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
        getChildren().add(createRoot());
    }

    protected Node createRoot() {
        final DiagramNodeUi nodeUi = getSkinnable();
        final VBox root = new VBox(); /* The root container */

        final HBox inputs = new HBox(); /* Container for data port inputs */
        inputs.setAlignment(Pos.CENTER);
        inputs.setSpacing(5);
        /* Populate input data ports */
        for(IDataPort<?> input : nodeUi.getNode().getInputs()) {
            inputs.getChildren().add(new DataPortComponent(this, input));
        }

        final HBox outputs = new HBox(); /* Container for data port outputs */
        outputs.setAlignment(Pos.CENTER);
        outputs.setSpacing(5);
        /* Populate output data ports */
        for(IDataPort<?> output : nodeUi.getNode().getOutputs()) {
            outputs.getChildren().add(new DataPortComponent(this, output));
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
        return root;
    }

    protected void createMainBody(StackPane parent) {
        final Text exeName = new Text(getSkinnable().getNode().getExecutable().getName());
        parent.getChildren().add(exeName);
    }
}
