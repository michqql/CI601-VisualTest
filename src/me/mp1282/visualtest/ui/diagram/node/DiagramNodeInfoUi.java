package me.mp1282.visualtest.ui.diagram.node;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.ui.other.ISelectableUi;

public class DiagramNodeInfoUi extends GridPane {

    private final ObservableList<ISelectableUi> list;

    public DiagramNodeInfoUi(ObservableList<ISelectableUi> selectedElements) {
        this.list = selectedElements;

        this.list.addListener((ListChangeListener<? super ISelectableUi>) _ -> handleSelectedListChange());

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

    private void handleSelectedListChange() {
        if(list.isEmpty()) {
            setVisible(false);
            return;
        }

        getChildren().clear();

        /* Add information */

        setVisible(true);
    }
}
