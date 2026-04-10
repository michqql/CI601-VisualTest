package me.mp1282.visualtest.ui.object.types;

import javafx.scene.control.TextField;

public class StringTypeEditorUi extends TypeEditorUi<String> {

    private final TextField textField;

    public StringTypeEditorUi() {
        this.textField = new TextField();

        getChildren().add(textField);
    }

    @Override
    public void initialise(Object initialObject) {
        if(initialObject instanceof String str)
            this.textField.setText(str);
    }

    @Override
    public String getObject() throws RuntimeException {
        return textField.getText();
    }
}
