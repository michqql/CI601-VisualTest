package me.mp1282.visualtest.ui.object;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import me.mp1282.visualtest.ui.other.NumericFieldUi;

public class RangeCheckEditorDialogUi extends Dialog<RangeCheckEditorDialogUi.Result> {

    public record Result(Class<? extends Number> type, Number min, Number max) {}

    private NumericFieldUi<?> minField;
    private NumericFieldUi<?> maxField;
    private Class<? extends Number> selectedType;
    private boolean fieldsBuilt = false;

    private final GridPane fieldsPane;
    private final Number initialMin;
    private final Number initialMax;

    public RangeCheckEditorDialogUi(Class<? extends Number> initialType, Number initialMin, Number initialMax) {
        this.initialMin  = initialMin;
        this.initialMax  = initialMax;
        this.fieldsPane  = new GridPane();

        setTitle("Edit Range Check");

        fieldsPane.setHgap(10);
        fieldsPane.setVgap(8);
        fieldsPane.add(new Label("Min:"), 0, 0);
        fieldsPane.add(new Label("Max:"), 0, 1);

        ComboBox<Class<? extends Number>> typeComboBox = new ComboBox<>();
        typeComboBox.setMaxWidth(Double.MAX_VALUE);
        typeComboBox.getItems().addAll(
                Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class
        );
        typeComboBox.setCellFactory(_ -> new NumericTypeListCell());
        typeComboBox.setButtonCell(new NumericTypeListCell());
        typeComboBox.setPromptText("Select Numeric Type...");

        typeComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, clazz) -> {
            if (clazz == null) return;
            selectedType = clazz;
            rebuildFields(clazz);
        });

        if (initialType != null) {
            typeComboBox.getSelectionModel().select(initialType);
        } else {
            typeComboBox.getSelectionModel().select(Integer.class);
        }

        VBox content = new VBox(10, typeComboBox, fieldsPane);
        content.setPadding(new Insets(10));
        getDialogPane().setContent(content);

        ButtonType okButton     = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
                return null;
            if (selectedType == null || minField == null || maxField == null)
                return null;
            return new Result(selectedType, minField.getValue(), maxField.getValue());
        });
    }

    private void rebuildFields(Class<? extends Number> clazz) {
        fieldsPane.getChildren().removeIf(n -> {
            Integer col = GridPane.getColumnIndex(n);
            return col != null && col == 1;
        });

        /* Each branch passes a concrete typed constant so T can be inferred */
        if      (clazz == Byte.class)   createFields(NumericFieldUi.BYTE_TYPE);
        else if (clazz == Short.class)  createFields(NumericFieldUi.SHORT_TYPE);
        else if (clazz == Long.class)   createFields(NumericFieldUi.LONG_TYPE);
        else if (clazz == Float.class)  createFields(NumericFieldUi.FLOAT_TYPE);
        else if (clazz == Double.class) createFields(NumericFieldUi.DOUBLE_TYPE);
        else                            createFields(NumericFieldUi.INTEGER_TYPE);
    }

    /* Separate method to capture T so NumericFieldUi<T> can be constructed */
    private <T extends Number> void createFields(NumericFieldUi.Type<T> fieldType) {
        minField = new NumericFieldUi<>(fieldType);
        maxField = new NumericFieldUi<>(fieldType);

        /* Only pre-fill with initial values the first time fields are built */
        if (!fieldsBuilt) {
            if (initialMin != null) minField.setValue(initialMin);
            if (initialMax != null) maxField.setValue(initialMax);
            fieldsBuilt = true;
        }

        GridPane.setHgrow(minField, Priority.ALWAYS);
        GridPane.setHgrow(maxField, Priority.ALWAYS);
        fieldsPane.add(minField, 1, 0);
        fieldsPane.add(maxField, 1, 1);

        if (getDialogPane().getScene() != null)
            getDialogPane().getScene().getWindow().sizeToScene();
    }

    private static class NumericTypeListCell extends ListCell<Class<? extends Number>> {
        @Override
        protected void updateItem(Class<? extends Number> clazz, boolean empty) {
            super.updateItem(clazz, empty);
            setText(clazz == null || empty ? null : clazz.getSimpleName());
        }
    }
}