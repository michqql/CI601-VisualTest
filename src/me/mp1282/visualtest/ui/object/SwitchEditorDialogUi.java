package me.mp1282.visualtest.ui.object;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import me.mp1282.visualtest.system.diagram.node.data.SwitchCase;
import me.mp1282.visualtest.system.diagram.node.data.SwitchNodeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for configuring the cases of a Switch node.
 * Rows can be added and removed freely; the number of rows determines how many
 * case execution path ports the node will have after confirmation.
 */
public class SwitchEditorDialogUi extends Dialog<List<SwitchEditorDialogUi.Result>> {

    public record Result(String label, String value) {}

    private final VBox rowsBox = new VBox(6);
    private final List<TextField> valueFields = new ArrayList<>();
    private final List<TextField> labelFields = new ArrayList<>();

    public SwitchEditorDialogUi(SwitchNodeData data) {
        setTitle("Edit Switch Cases");

        /* Header row */
        GridPane header = new GridPane();
        header.setHgap(10);
        ColumnConstraints col0 = new ColumnConstraints(60);
        ColumnConstraints col1 = new ColumnConstraints(140);
        ColumnConstraints col2 = new ColumnConstraints(120);
        ColumnConstraints col3 = new ColumnConstraints(30);
        header.getColumnConstraints().addAll(col0, col1, col2, col3);
        header.add(new Label("#"),           0, 0);
        header.add(new Label("Match Value"), 1, 0);
        header.add(new Label("Port Label"),  2, 0);

        /* Populate existing cases */
        for (SwitchCase sc : data.getCases()) {
            addRow(sc.getValue(), sc.getLabel());
        }

        Button addButton = new Button("+ Add Case");
        addButton.setOnAction(_ -> addRow("", ""));

        VBox content = new VBox(8, header, rowsBox, addButton);
        content.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        getDialogPane().setContent(scroll);

        ButtonType okButton     = new ButtonType("OK",     ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
                return null;
            List<Result> results = new ArrayList<>();
            for (int i = 0; i < valueFields.size(); i++)
                results.add(new Result(labelFields.get(i).getText(), valueFields.get(i).getText()));
            return results;
        });
    }

    private void addRow(String value, String label) {
        int index = valueFields.size();

        TextField valueField = new TextField(value);
        TextField labelField = new TextField(label);
        valueField.setPromptText("value to match");
        labelField.setPromptText("label (optional)");
        HBox.setHgrow(valueField, Priority.ALWAYS);
        HBox.setHgrow(labelField, Priority.ALWAYS);

        valueFields.add(valueField);
        labelFields.add(labelField);

        Button removeButton = new Button("✕");
        removeButton.setOnAction(_ -> removeRow(valueField, labelField));

        Label indexLabel = new Label("Case " + index + ":");
        indexLabel.setMinWidth(55);

        HBox row = new HBox(6, indexLabel, valueField, labelField, removeButton);
        row.setUserData(new Object[]{valueField, labelField}); /* for lookup on remove */
        rowsBox.getChildren().add(row);
    }

    private void removeRow(TextField valueField, TextField labelField) {
        int idx = valueFields.indexOf(valueField);
        if (idx < 0) return;
        valueFields.remove(idx);
        labelFields.remove(idx);
        /* Remove the corresponding HBox row */
        rowsBox.getChildren().removeIf(node -> {
            if (node.getUserData() instanceof Object[] pair)
                return pair[0] == valueField;
            return false;
        });
        /* Re-label remaining rows */
        for (int i = 0; i < rowsBox.getChildren().size(); i++) {
            if (rowsBox.getChildren().get(i) instanceof HBox hbox
                    && hbox.getChildren().get(0) instanceof Label lbl) {
                lbl.setText("Case " + i + ":");
            }
        }
    }
}
