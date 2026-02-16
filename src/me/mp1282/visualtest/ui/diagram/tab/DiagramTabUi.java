package me.mp1282.visualtest.ui.diagram.tab;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRefExecutable;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.event.EventBus;
import me.mp1282.visualtest.system.event.types.DiagramSelectedEvent;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.ui.other.ZoomLevelMenuItemUi;
import me.mp1282.visualtest.util.DiagramZoomLevel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DiagramTabUi extends Tab {

    private static final String ADD_TO_CURRENT_DIAGRAM_SECRET = "add-to-current-diagram";
    private static final String ZOOM_LEVEL_SECRET = "zoom-level";
    private static final NumberFormat DOUBLE_FORMATTER = new DecimalFormat("#0.00");

    private final DiagramRepository diagramRepository;
    private final Diagram diagram;
    private final DiagramUi ui;

    private final Label unsavedIndicator;
    private final List<ZoomLevelMenuItemUi> zoomLevelMenuItems;

    public DiagramTabUi(Diagram diagram, DiagramUi ui) {
        super(diagram.nameProperty().get(), ui);

        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        this.diagram = diagram;
        this.ui = ui;

        this.unsavedIndicator = new Label("*");
        this.zoomLevelMenuItems = new ArrayList<>();

        createContextMenu();

        /* Event handlers */
        diagram.unsavedProperty().addListener((obs, old, newValue) -> handleUnsavedChange(newValue));
        selectedProperty().addListener((obs, old, newValue) -> handleSelected(newValue));
    }

    public Diagram getDiagram() {
        return diagram;
    }

    private void createContextMenu() {
        ContextMenu tabContextMenu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename Diagram");
        renameItem.setOnAction(event -> onClickRenameDiagram());

        Menu zoomLevelMenu = new Menu("Zoom");
        zoomLevelMenu.setUserData(ZOOM_LEVEL_SECRET);
        for(DiagramZoomLevel zoom : DiagramZoomLevel.values()) {
            ZoomLevelMenuItemUi zoomLevelItem = new ZoomLevelMenuItemUi(DOUBLE_FORMATTER.format(zoom.getZoom()) + "x");
            zoomLevelItem.setUserData(zoom);
            zoomLevelItem.addEventHandler(ActionEvent.ACTION, this::handleZoomLevelSelected);

            zoomLevelMenuItems.add(zoomLevelItem);
        }
        zoomLevelMenu.getItems().addAll(zoomLevelMenuItems);

        MenuItem addToCurrentDiagramItem = new MenuItem("Add to Current Diagram");
        addToCurrentDiagramItem.setUserData(ADD_TO_CURRENT_DIAGRAM_SECRET);
        addToCurrentDiagramItem.setOnAction(_ -> onClickAddToCurrentDiagram());

        tabContextMenu.setOnShowing(_ -> updateZoomMenuItems());

        tabContextMenu.getItems().addAll(renameItem, zoomLevelMenu, addToCurrentDiagramItem);
        setContextMenu(tabContextMenu);
    }

    private void updateZoomMenuItems() {
        DiagramZoomLevel zoom = diagram.zoomLevelProperty().get();

        for(ZoomLevelMenuItemUi zoomLevelItem : zoomLevelMenuItems) {
            zoomLevelItem.currentZoomLevelProperty().set(zoom.equals(zoomLevelItem.getUserData()));
        }
    }

    private void handleZoomLevelSelected(ActionEvent e) {
        if(e.getSource() instanceof MenuItem item && item.getUserData() instanceof DiagramZoomLevel zoom) {
            diagram.zoomLevelProperty().set(zoom);
            updateZoomMenuItems();
        }
    }

    private void handleUnsavedChange(boolean unsaved) {
        setGraphic(unsaved ? unsavedIndicator : null);
    }

    private void handleSelected(boolean selected) {
        /* If tab selected -> disable,
         * If not selected -> enable.
         */
        MenuItem addToCurrentDiagramMenuItem = getAddToCurrentDiagramMenuItem();
        if(addToCurrentDiagramMenuItem != null)
            addToCurrentDiagramMenuItem.setDisable(selected);

        /* If selected, set the current diagram in the repository */
        if(selected) {
            diagramRepository.selectedDiagramProperty().set(diagram);
            EventBus.publish(new DiagramSelectedEvent(diagram));
        }
    }

    private void onClickRenameDiagram() {
        TextInputDialog dialog = new TextInputDialog(diagram.nameProperty().get());
        dialog.setTitle("Rename Diagram");
        dialog.setContentText("Name:");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        /* Show the dialog and wait for user input */
        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                diagram.nameProperty().set(newName);
                setText(newName);
            }
        });
    }

    private void onClickAddToCurrentDiagram() {
        final DiagramRefExecutable refExe = diagramRepository.getReferenceExecutable(diagram);
        diagramRepository.selectedDiagramProperty().get().placeExecutable(refExe);
    }

    private MenuItem getAddToCurrentDiagramMenuItem() {
        if(getContextMenu() == null)
            return null;

        for(MenuItem item : getContextMenu().getItems()) {
            if(ADD_TO_CURRENT_DIAGRAM_SECRET.equals(item.getUserData())) {
                return item;
            }
        }
        return null;
    }
}
