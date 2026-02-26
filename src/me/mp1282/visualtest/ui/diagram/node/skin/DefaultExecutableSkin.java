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

    private static final Color DEFAULT_COLOUR  = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.RED;

    private final Rectangle outline;

    public DefaultExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);

        VBox parent = new VBox();
        HBox inputs = new HBox();
        StackPane main = new StackPane();
        HBox outputs = new HBox();

        for(IDataPort<?> input : nodeUi.getNode().getInputs()) {
            DataPortComponent dpc = new DataPortComponent(input);
            dpc.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onDataPortClicked);

            inputs.getChildren().add(dpc);
        }

        this.outline = new Rectangle(/* Width => */ 110, /* Height => */ 90);
        this.outline.setStroke(Color.BLACK);
        this.outline.setFill(null);
        this.outline.setStrokeWidth(5);

        for(IDataPort<?> output : nodeUi.getNode().getOutputs()) {
            DataPortComponent dpc = new DataPortComponent(output);
            dpc.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onDataPortClicked);

            outputs.getChildren().add(dpc);
        }

        main.getChildren().add(outline);
        inputs.setAlignment(Pos.CENTER);
        inputs.setSpacing(5);
        outputs.setAlignment(Pos.CENTER);
        outputs.setSpacing(5);
        parent.getChildren().addAll(inputs, main, outputs);
        getChildren().add(parent);
    }

    private void onDataPortClicked(MouseEvent e) {
        System.out.println("MOUSE CLICKED");
        e.consume();
    }
}
