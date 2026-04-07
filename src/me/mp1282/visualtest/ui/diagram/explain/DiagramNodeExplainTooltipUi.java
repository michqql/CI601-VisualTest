package me.mp1282.visualtest.ui.diagram.explain;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class DiagramNodeExplainTooltipUi extends VBox {

    public DiagramNodeExplainTooltipUi() {
        setMouseTransparent(true);
        setPickOnBounds(false);
        setVisible(false);
        setStyle("-fx-background-color: rgba(255,255,220,0.95); -fx-padding: 8; "
               + "-fx-border-color: #888; -fx-border-width: 1; -fx-border-radius: 4; "
               + "-fx-background-radius: 4;");
        setMaxWidth(260);
        setSpacing(4);
    }

    public void show(DiagramNodeUi nodeUi) {
        getChildren().clear();
        DiagramNode node = nodeUi.getNode();

        Text title = new Text(node.getExecutable().getName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        getChildren().add(title);

        String nodeDescription = NodeDescriptionProvider.describe(node.getExecutable());
        if(nodeDescription != null) {
            Label desc = new Label(nodeDescription);
            desc.setWrapText(true);
            desc.setMaxWidth(244);
            getChildren().add(desc);
        }

        if (!node.getNodeAfterPaths().isEmpty()) {
            getChildren().add(sectionHeader("Execution paths:"));
            for (int i = 0; i < node.getNodeAfterPaths().size(); i++) {
                String label = node.getExecutable().getExecutionPathLabel(i);
                if (label == null)
                    label = "OUT " + i;

                /* getNodesAfterPath.get(index) should not be null here */
                DiagramNode target = node.getNodeAfterPaths().get(i).getOther();
                String targetName = (target != null) ? target.getExecutable().getName() : "not connected";
                getChildren().add(new Label("  " + label + " -> " + targetName));
            }
        }

        if (!node.getInputs().isEmpty()) {
            getChildren().add(sectionHeader("Inputs:"));
            for (InputParameter input : node.getInputs()) {
                String typeName = input.getType().getDataType().getSimpleName();
                String from = (input.getFrom() != null)
                        ? input.getFrom().getParentNode().getExecutable().getName()
                        : "not connected";
                getChildren().add(new Label("  " + typeName + " <- " + from));
            }
        }

        if (!node.getOutputs().isEmpty()) {
            getChildren().add(sectionHeader("Outputs:"));
            for (OutputReturn output : node.getOutputs()) {
                String typeName = output.getType().getDataType().getSimpleName();
                getChildren().add(new Label("  " + typeName));
            }
        }

        layoutXProperty().bind(nodeUi.layoutXProperty().add(nodeUi.widthProperty()).add(8));
        layoutYProperty().bind(nodeUi.layoutYProperty());
        setVisible(true);
    }

    public void hide() {
        layoutXProperty().unbind();
        layoutYProperty().unbind();
        setVisible(false);
        getChildren().clear();
    }

    private static Text sectionHeader(String text) {
        Text t = new Text(text);
        t.setStyle("-fx-font-weight: bold;");
        return t;
    }
}
