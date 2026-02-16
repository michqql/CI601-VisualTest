package me.mp1282.visualtest.ui.other;

import javafx.scene.control.ListCell;
import me.mp1282.visualtest.system.diagram.Diagram;

public class DiagramListCell extends ListCell<Diagram> {

    private String emptyText;

    public DiagramListCell() {
    }

    public DiagramListCell(String emptyText) {
        this.emptyText = emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    @Override
    protected void updateItem(Diagram diagram, boolean empty) {
        super.updateItem(diagram, empty);

        if(diagram == null || empty) {
            setText(emptyText);
        } else {
            setText(diagram.nameProperty().get());
        }
    }
}
