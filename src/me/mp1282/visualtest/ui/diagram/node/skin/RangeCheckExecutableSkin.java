package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import me.mp1282.visualtest.system.diagram.node.data.RangeCheckNodeData;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.object.RangeCheckEditorDialogUi;

import java.util.Optional;

public class RangeCheckExecutableSkin extends DefaultExecutableSkin {

    public RangeCheckExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
    }

    @Override
    protected void createMainBody(StackPane parent) {
        if (!(getSkinnable().getNode().getData() instanceof RangeCheckNodeData data))
            throw new RuntimeException("NodeData not of type RangeCheckNodeData");

        BorderPane borderPane = new BorderPane();
        parent.getChildren().add(borderPane);

        StackPane top = new StackPane();
        super.createMainBody(top);
        borderPane.setTop(top);

        Button editButton = new Button();
        editButton.textProperty().bind(Bindings.createStringBinding(() -> {
            Class<? extends Number> type = data.typeProperty().get();
            Number min = data.minProperty().get();
            Number max = data.maxProperty().get();
            if (type == null || min == null || max == null)
                return "Not configured";
            return type.getSimpleName() + " [" + min + ", " + max + "]";
        }, data.typeProperty(), data.minProperty(), data.maxProperty()));

        editButton.setOnAction(_ -> {
            RangeCheckEditorDialogUi dialog = new RangeCheckEditorDialogUi(
                    data.typeProperty().get(),
                    data.minProperty().get(),
                    data.maxProperty().get()
            );
            Optional<RangeCheckEditorDialogUi.Result> result = dialog.showAndWait();
            result.ifPresent(r -> {
                data.typeProperty().set(r.type());
                data.minProperty().set(r.min());
                data.maxProperty().set(r.max());
            });
        });

        borderPane.setCenter(editButton);
    }
}