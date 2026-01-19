package me.mp1282.visualtest.ui.other;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.Cell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.executable.ExecutableUi;
import me.mp1282.visualtest.util.DragContext;

public class ExecutableCellUi {

    public static void handle(Cell<?> cell, Executable executable) {
        final ExecutableUi ui = new ExecutableUi(executable);
        final StringBuilder nameBuilder = new StringBuilder(executable.getName());
        nameBuilder.append('(');
        for(DataPort inputDataPort : executable.getInputs()) {
            nameBuilder
                    .append(inputDataPort.dataTypeDescriptor())
                    .append(' ')
                    .append(inputDataPort.name())
                    .append(", ");
        }

        if(executable.getNumberOfInputs() > 0)
            nameBuilder.setLength(nameBuilder.length() - 2);
        nameBuilder.append(") => ");

        for(DataPort outputDataPort : executable.getOutputs()) {
            nameBuilder
                    .append(outputDataPort.dataTypeDescriptor())
                    .append(' ')
                    .append(outputDataPort.name())
                    .append(", ");
        }

        if(executable.getNumberOfOutputs() > 0)
            nameBuilder.setLength(nameBuilder.length() - 2);

        cell.setGraphic(ui);
        cell.setText(nameBuilder.toString());

        cell.setMinWidth(ui.getWidth());
        cell.setMaxWidth(ui.getWidth() * 5);
        cell.setWrapText(true);

        /* Add drag drop listener */
        ui.setOnDragDetected(e -> {
            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
            /* Add a blank content to the drag-board just to signify
             * that there is data being dragged.
             */
            ClipboardContent cc = new ClipboardContent();
            cc.put(Executable.DTO_DATA_FORMAT, "");
            db.setContent(cc);
            /* Store the executable being dragged in the DragContext */
            DragContext.setObject(executable);

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            db.setDragView(ui.snapshot(snapshotParameters, null));

            e.consume();
        });
    }
}
