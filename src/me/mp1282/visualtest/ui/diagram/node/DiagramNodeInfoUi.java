package me.mp1282.visualtest.ui.diagram.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.diagram.DiagramNode;

import java.util.Map;

public class DiagramNodeInfoUi extends GridPane {

    private final ObjectProperty<DiagramNode> nodeProperty; /* The node to show information for */

    /* The labels for displaying information */
    private final Label nameLabel = new Label("Name");
    private final Label nameVarLabel;
    private final Label typeLabel = new Label("Type");
    private final Label typeVarLabel;

    public DiagramNodeInfoUi() {
        this.nodeProperty = new SimpleObjectProperty<>();
        this.nameVarLabel = new Label();
        this.typeVarLabel = new Label();

        this.nodeProperty.addListener((obs, old, newValue) ->
                handleNodeChange(newValue));

        setVisible(false);
        setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))
        ));
        setBackground(new Background(
                new BackgroundFill(Color.GHOSTWHITE, new CornerRadii(5), Insets.EMPTY)
        ));
        setPadding(new Insets(10));
        setMinHeight(150);
        setMinWidth(150);
        setHgap(13);
        setVgap(5);
    }

    public ObjectProperty<DiagramNode> nodeProperty() {
        return this.nodeProperty;
    }

    private void handleNodeChange(DiagramNode node) {
        if(node == null) {
            setVisible(false);
            return;
        }

        getChildren().clear();
        add(nameLabel, 0, 0);
        add(nameVarLabel, 1, 0);
        add(typeLabel, 0, 1);
        add(typeVarLabel, 1, 1);

        setVisible(true);

        nameVarLabel.setText(node.getExecutable().getName());
        typeVarLabel.setText(node.getExecutable().getClass().getSimpleName());

        /* Add executable information */
        Map<String, String> infoMap = node.getExecutable().getInformationMap();
        if(infoMap != null) {
            infoMap.forEach((label, info) -> {
                addRow(getRowCount(), new Label(label), new Label(info));
            });
        }
    }
}
