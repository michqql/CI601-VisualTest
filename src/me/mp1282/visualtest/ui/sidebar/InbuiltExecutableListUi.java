package me.mp1282.visualtest.ui.sidebar;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.inbuilt.InbuiltMethodExecutable;
import me.mp1282.visualtest.ui.other.ExecutableCellUi;

public class InbuiltExecutableListUi extends TreeView<Object> {

    public InbuiltExecutableListUi() {
        this.setShowRoot(false);

        TreeItem<Object> root = new TreeItem<>(null);
        root.setExpanded(true);

        VisualTestSystem.getInstance().getInbuiltMethodRepository().getRepository().forEach(pair -> {
            final TreeItem<Object> item = new TreeItem<>(pair.key().providerName());
            item.setExpanded(false);

            pair.value().forEach(method -> item.getChildren().add(new TreeItem<>(method)));

            root.getChildren().add(item);
        });

        this.setRoot(root);
        this.setCellFactory(objectTreeView -> new TreeCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (item instanceof String s) {
                    setText(s);
                    setGraphic(null);
                } else if (item instanceof InbuiltMethodExecutable inbuiltMethodExecutable) {
                    ExecutableCellUi.handle(this, inbuiltMethodExecutable);
                }
            }
        });
    }
}
