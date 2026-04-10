package me.mp1282.visualtest.ui.object.types.primitive.number;

import me.mp1282.visualtest.ui.object.types.TypeEditorUi;
import me.mp1282.visualtest.ui.other.NumericFieldUi;

public class NumberTypeEditorUi<T extends Number> extends TypeEditorUi<T> {

    private final NumericFieldUi<T> numericFieldUi;

    public NumberTypeEditorUi(NumericFieldUi.Type<T> type) {
        this.numericFieldUi = new NumericFieldUi<>(type);

        getChildren().add(numericFieldUi);
    }

    @Override
    public void initialise(Object initialObject) {
        if(initialObject instanceof Number number)
            numericFieldUi.setValue(number);
    }

    @Override
    public T getObject() throws RuntimeException {
        return numericFieldUi.valueProperty().getValue();
    }
}
