package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import me.mp1282.visualtest.system.diagram.node.data.ConstantNodeData;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.object.ObjectEditorDialogUi;

import java.util.Optional;

public class ConstantExecutableSkin extends DefaultExecutableSkin {

    public ConstantExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
    }

    @Override
    protected void createMainBody(StackPane parent) {
        /* Get the ConstantNodeData from the node */
        if(!(getSkinnable().getNode().getData() instanceof ConstantNodeData data))
            throw new RuntimeException("NodeData not of type ConstantNodeData");

        BorderPane borderPane = new BorderPane();
        parent.getChildren().add(borderPane);

        StackPane top = new StackPane();
        super.createMainBody(top);
        borderPane.setTop(top);

        /* Button that renders the constant node data as its text */
        Button editButton = new Button();
        editButton.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(data.constantProperty().get()), data.constantProperty()
        ));
        editButton.setOnAction(e -> {
            ObjectEditorDialogUi dialog = new ObjectEditorDialogUi(data.constantProperty().get());
            Optional<ObjectEditorDialogUi.Result> optional = dialog.showAndWait();

            optional.ifPresent(result -> data.constantProperty().set(result.result()));
        });
        borderPane.setCenter(editButton);
    }
}
