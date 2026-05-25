package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.diagram.node.data.SwitchCase;
import me.mp1282.visualtest.system.diagram.node.data.SwitchNodeData;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;
import me.mp1282.visualtest.ui.diagram.node.skin.component.ExecutionPathPortComponent;
import me.mp1282.visualtest.ui.object.SwitchEditorDialogUi;

import java.util.List;
import java.util.Optional;

public class SwitchExecutableSkin extends DefaultExecutableSkin {

    public SwitchExecutableSkin(DiagramNodeUi nodeUi) {
        super(nodeUi);
    }

    @Override
    protected void createMainBody(StackPane parent) {
        if (!(getSkinnable().getNode().getData() instanceof SwitchNodeData data))
            throw new RuntimeException("NodeData not of type SwitchNodeData");

        BorderPane borderPane = new BorderPane();
        parent.getChildren().add(borderPane);

        StackPane top = new StackPane();
        super.createMainBody(top);
        borderPane.setTop(top);

        Button editButton = new Button("Edit Cases…");
        editButton.setOnAction(_ -> {
            SwitchEditorDialogUi dialog = new SwitchEditorDialogUi(data);
            Optional<List<SwitchEditorDialogUi.Result>> result = dialog.showAndWait();
            result.ifPresent(results -> {
                /* Update NodeData cases to match the new list */
                data.getCases().clear();
                for (SwitchEditorDialogUi.Result r : results) {
                    SwitchCase sc = new SwitchCase();
                    sc.labelProperty().set(r.label());
                    sc.valueProperty().set(r.value());
                    data.getCases().add(sc);
                }

                /* Resize the model's execution paths:
                 * cases.size() case paths + 1 DEFAULT = total path count */
                int newPathCount = results.size() + 1;
                getSkinnable().getNode().setExecutionPathCount(newPathCount);

                /* Update the UI port area map and re-create the skin */
                getSkinnable().refreshExecutionPaths(newPathCount);
            });
        });

        borderPane.setCenter(editButton);
    }

    /**
     * Overrides port rendering for case output ports so their label reflects
     * the user-configured case label from NodeData (reactive binding).
     * The IN port (branchIndex == -1) and the DEFAULT port
     * (branchIndex == cases.size()) fall back to the default behaviour.
     */
    @Override
    protected Node execPathPortNode(Executable exe, int branchIndex) {
        if (!(getSkinnable().getNode().getData() instanceof SwitchNodeData data))
            return super.execPathPortNode(exe, branchIndex);

        /* IN port or DEFAULT port -> default rendering */
        if (branchIndex < 0 || branchIndex >= data.getCases().size())
            return super.execPathPortNode(exe, branchIndex);

        SwitchCase sc = data.getCases().get(branchIndex);
        final ExecutionPathPortComponent port = new ExecutionPathPortComponent(this, branchIndex);

        final Text label = new Text();
        label.textProperty().bind(sc.labelProperty().map(lbl ->
                lbl.isBlank() ? "CASE " + branchIndex : lbl));
        label.setStyle("-fx-font-size: 10;");
        label.setMouseTransparent(true);

        final VBox wrapper = new VBox(2, label, port); /* label above port (output side) */
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }
}
