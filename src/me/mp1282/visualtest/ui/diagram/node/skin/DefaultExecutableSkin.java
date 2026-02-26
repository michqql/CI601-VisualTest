package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.mp1282.visualtest.system.diagram.node.IDataPort;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.node.skin.component.DataPortComponent;

public class DefaultExecutableSkin extends SkinBase<DiagramNodeUi> {

    public DefaultExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);

        final VBox parent    = new VBox(); /* The parent container */

        final HBox inputs    = new HBox(); /* Container for data port inputs */
        inputs.setAlignment(Pos.CENTER);
        inputs.setSpacing(5);

        final HBox outputs   = new HBox(); /* Container for data port outputs */
        outputs.setAlignment(Pos.CENTER);
        outputs.setSpacing(5);

        final StackPane main = new StackPane(); /* Container for main content */

        /* Populate input data ports */
        for(IDataPort<?> input : nodeUi.getNode().getInputs()) {
            DataPortComponent dpc = new DataPortComponent(input);
            dpc.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onDataPortClicked);

            inputs.getChildren().add(dpc);
        }

        /* Populate output data ports */
        for(IDataPort<?> output : nodeUi.getNode().getOutputs()) {
            DataPortComponent dpc = new DataPortComponent(output);
            dpc.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onDataPortClicked);

            outputs.getChildren().add(dpc);
        }

        /* Construct the main content */
        final Rectangle outline = new Rectangle(/* Width => */ 110, /* Height => */ 90);
        outline.setStroke(Color.BLACK);
        outline.setFill(null);
        outline.setStrokeWidth(3);

        /* Construct node graph */
        main.getChildren().add(outline);
        parent.getChildren().addAll(inputs, main, outputs);
        getChildren().add(parent);
    }

    private void onDataPortClicked(MouseEvent e) {
        System.out.println("MOUSE CLICKED");
        e.consume();
    }
}
