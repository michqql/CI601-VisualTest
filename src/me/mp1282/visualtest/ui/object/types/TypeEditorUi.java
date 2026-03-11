package me.mp1282.visualtest.ui.object.types;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public abstract class TypeEditorUi<T> extends StackPane {

    public TypeEditorUi() {
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2)
        )));
    }

    public abstract void initialise(Object initialObject);
    public abstract T getObject() throws RuntimeException;
}
