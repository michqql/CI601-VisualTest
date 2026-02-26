package me.mp1282.visualtest.ui.sidebar;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.event.EventBus;
import me.mp1282.visualtest.system.event.types.ClassSelectedEvent;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.ui.other.ExecutableCellUi;

import java.util.Collection;

public class ExecutableListUi extends VBox {

    private final TabPane tabPane;

    public ExecutableListUi() {
        Tab inbuiltTab = new Tab("Inbuilt");
        inbuiltTab.setClosable(false);
        inbuiltTab.setContent(new InbuiltExecutableListUi());

        this.tabPane = new TabPane(inbuiltTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        getChildren().add(tabPane);

        EventBus.subscribe(ClassSelectedEvent.class, this::handleClassSelectedEvent);
    }

    private void handleClassSelectedEvent(ClassSelectedEvent classSelectedEvent) {
        Class<?> clazz = classSelectedEvent.getLoadedClass().getWrappedClass();

        /* Does a tab holding this class already exist? */
        for(Tab tab : tabPane.getTabs()) {
            if(clazz.equals(tab.getUserData())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        /* Otherwise, create a new tab for this class */
        Tab classTab = new Tab(clazz.getSimpleName());
        classTab.setUserData(clazz);
        classTab.setContent(createList(classSelectedEvent.getLoadedClass().getMethods()));

        tabPane.getTabs().add(classTab);
        tabPane.getSelectionModel().select(classTab);
    }

    protected static ListView<Executable> createList(Collection<? extends Executable> executables) {
        ListView<Executable> list = new ListView<>();

        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Executable executable, boolean empty) {
                super.updateItem(executable, empty);

                if(empty || executable == null) {
                    setGraphic(null);
                } else {
                    ExecutableCellUi.handle(this, executable);
                }
            }
        });

        list.getItems().addAll(executables);

        return list;
    }
}
