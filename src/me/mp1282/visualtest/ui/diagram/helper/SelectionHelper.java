package me.mp1282.visualtest.ui.diagram.helper;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.ui.other.ISelectableUi;

public class SelectionHelper {

    private final ObservableList<ISelectableUi> list;

    public SelectionHelper() {
        this.list = FXCollections.observableArrayList();

        /* When the selection list changes, fire selection updates and inform info UI */
        list.addListener((ListChangeListener<? super ISelectableUi>) change -> {
            while(change.next()) {
                for (ISelectableUi ui : change.getAddedSubList())
                    ui.selectedProperty().set(true);

                for(ISelectableUi ui : change.getRemoved())
                    ui.selectedProperty().set(false);
            }
        });
    }

    public ObservableList<ISelectableUi> getList() {
        return list;
    }

    public void clear() {
        list.clear();
    }

    public void handleSelection(Object object, boolean shiftHeld) {
        if(object instanceof ISelectableUi ui) {
            /* If shift was held, the item is appended to the end of the list.
             * Otherwise, the list is cleared and the item will be the only item in the list.
             *
             * However, if the list already contains the item, the item is removed from the list.
             */

            /* The list already contains the item: remove it and return early. */
            if(list.remove(ui))
                return;

            /* If shift is NOT held, clear the list */
            if(!shiftHeld)
                list.clear();

            list.add(ui);
        }
    }
}
