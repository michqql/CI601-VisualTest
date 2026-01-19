package me.mp1282.visualtest.ui.diagram.tab;

import javafx.scene.control.*;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRefExecutable;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.ui.diagram.DiagramUi;

public class DiagramTabUi extends Tab {

    private static final String ADD_TO_CURRENT_DIAGRAM_SECRET = "add-to-current-diagram";

    private final DiagramRepository diagramRepository;
    private final Diagram diagram;
    private final DiagramUi ui;

    private final Label unsavedIndicator;

    public DiagramTabUi(Diagram diagram, DiagramUi ui) {
        super(diagram.nameProperty().get(), ui);

        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        this.diagram = diagram;
        this.ui = ui;
        this.unsavedIndicator = new Label("*");

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

        MenuItem addToCurrentDiagramItem = new MenuItem("Add to Current Diagram");
        addToCurrentDiagramItem.setUserData(ADD_TO_CURRENT_DIAGRAM_SECRET);
        addToCurrentDiagramItem.setOnAction(event -> onClickAddToCurrentDiagram());

        tabContextMenu.getItems().addAll(renameItem, addToCurrentDiagramItem);
        setContextMenu(tabContextMenu);
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
