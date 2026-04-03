package me.mp1282.visualtest.ui.object;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.ui.object.types.ObjectTypeToEditorLUT;
import me.mp1282.visualtest.ui.object.types.TypeEditorUi;

import java.util.*;

public class ObjectEditorDialogUi extends Dialog<ObjectEditorDialogUi.Result> {

    private final Object initialObject;
    private final Map<Class<?>, TypeEditorUi<?>> typeToEditorMap;

    private final BorderPane borderPane;

    public ObjectEditorDialogUi(final Object initial) {
        this.initialObject   = initial;
        this.typeToEditorMap = new HashMap<>();
        this.borderPane      = new BorderPane();

        setTitle("Edit Object");

        ComboBox<Class<?>> typeComboBox = createObjectTypeComboBox();
        HBox top = new HBox(typeComboBox);
        HBox.setHgrow(typeComboBox, Priority.ALWAYS);
        borderPane.setTop(top);
        /* 10px gap between top and center nodes */
        BorderPane.setMargin(borderPane.getTop(), new Insets(0, 0, 10, 0));
        getDialogPane().setContent(borderPane);

        /* Action buttons */
        ButtonType okButton     = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        /* Pre-fill fields if the initial object is non-null */

        /* Convert result to an object */
        setResultConverter(buttonType -> {
            final ButtonBar.ButtonData data = buttonType.getButtonData();
            return new Result(data, data == ButtonBar.ButtonData.CANCEL_CLOSE ? initial : createObject());
        });
    }

    private Object createObject() {
        if(borderPane.getCenter() instanceof TypeEditorUi<?> editorUi) {
            return editorUi.getObject();
        }
        return null;
    }

    private ComboBox<Class<?>> createObjectTypeComboBox() {
        ComboBox<Class<?>> comboBox = new ComboBox<>();
        comboBox.setMaxWidth(Double.MAX_VALUE);

        /* Populate with a list of object types, in the order:
         * - Primitive Types
         * - Popular Java Types (Strings, ...)
         * - User Defined Types (From Loaded JARs)
         * - Collections ?
         * - Other Java Types
         */

        /* Set ensures types are unique */
        final Set<Class<?>> types = new HashSet<>();
        /* Primitive Types - But use wrapper type */
        types.add(Boolean.class);
        types.add(Byte.class);
        types.add(Short.class);
        types.add(Integer.class);
        types.add(Long.class);
        types.add(Double.class);
        types.add(Character.class);
        /* Popular complex types */
        types.add(String.class);

        comboBox.setItems(FXCollections.observableList(new ArrayList<>(types)));
        comboBox.setCellFactory(_ -> new ObjectTypeListCell());
        comboBox.setButtonCell(new ObjectTypeListCell());
        comboBox.setPromptText("Select Object Type...");

        /* Change main body upon a new selection
         * Do this before setting an initial value so it
         * automatically updates to show the correct Editor Ui
         */
        comboBox.getSelectionModel().selectedItemProperty().addListener((_, _, clazz) -> setTypeEditor(clazz));

        /* Set ComboBox initial value to the initial object's type,
         * if its present in the types list
         */
        if(initialObject != null && types.contains(initialObject.getClass())) {
            comboBox.getSelectionModel().select(initialObject.getClass());
        }

        return comboBox;
    }

    private void setTypeEditor(final Class<?> clazz) {
        TypeEditorUi<?> editorUi = typeToEditorMap.computeIfAbsent(clazz, _ -> {
            TypeEditorUi<?> ui = ObjectTypeToEditorLUT.createEditor(clazz);
            if(ui != null)
                ui.initialise(initialObject);

            return ui;
        });
        borderPane.setCenter(editorUi);

        /* Tell Dialog to resize to content */
        getDialogPane().getScene().getWindow().sizeToScene();
    }

    public record Result(ButtonBar.ButtonData buttonData, Object result) {}

    private static class ObjectTypeListCell extends ListCell<Class<?>> {
        @Override
        protected void updateItem(Class<?> clazz, boolean empty) {
            super.updateItem(clazz, empty);

            if(clazz == null || empty)
                setText(null);
            else
                setText(clazz.getSimpleName());
        }
    }
}
