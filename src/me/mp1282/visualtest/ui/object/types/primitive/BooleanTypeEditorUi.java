package me.mp1282.visualtest.ui.object.types.primitive;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.ui.object.types.TypeEditorUi;

public class BooleanTypeEditorUi extends TypeEditorUi<Boolean> {

    private final ToggleGroup toggleGroup;
    private final RadioButton trueButton;
    private final RadioButton falseButton;

    public BooleanTypeEditorUi() {
        this.toggleGroup = new ToggleGroup();
        this.trueButton  = new RadioButton("True");
        this.falseButton = new RadioButton("False");

        trueButton.setUserData(true);
        falseButton.setUserData(false);

        toggleGroup.getToggles().addAll(trueButton, falseButton);

        getChildren().add(new VBox(trueButton, falseButton));
    }

    @Override
    public void initialise(Object initialObject) {
        if(initialObject instanceof Boolean bool) {
            toggleGroup.selectToggle(bool ? trueButton : falseButton);
        } else {
            toggleGroup.selectToggle(falseButton);
        }
    }

    @Override
    public Boolean getObject() {
        Toggle toggle = toggleGroup.getSelectedToggle();
        if(toggle != null && toggle.getUserData() instanceof Boolean bool)
            return bool;

        return false;
    }
}
