package me.mp1282.visualtest.ui.other;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.function.Supplier;

public class GridPane2dInfoUi extends GridPane {

    private int rowCounter;

    public GridPane2dInfoUi(double vGap, double hGap) {
        setVgap(vGap);
        setHgap(hGap);
    }

    public final void addInfoEntry(String name, Supplier<Node> nodeSupplier) {
        final Node node = nodeSupplier.get();
        final Label label = new Label(name);
        label.setLabelFor(node);

        GridPane.setConstraints(label, /* Column => */ 0, /* Row => */ rowCounter);
        GridPane.setConstraints(node,  /* Column => */ 1, /* Row => */ rowCounter);

        getChildren().addAll(label, node);

        rowCounter++;
    }
}
