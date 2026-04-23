package me.mp1282.visualtest.ui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.diagram.runtime.ExecuteTask;
import me.mp1282.visualtest.system.diagram.runtime.RuntimeEnvironmentService;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.system.persistence.SaveResult;
import me.mp1282.visualtest.ui.UiPreferencesService;
import me.mp1282.visualtest.ui.other.ToastUi;
import me.mp1282.visualtest.ui.other.ZoomLevelMenuItemUi;
import me.mp1282.visualtest.ui.project.ProjectWindowUi;
import me.mp1282.visualtest.util.DiagramZoomLevel;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainMenuBarUi extends MenuBar {

    private final LoadedJarRepository jarRepository;
    private final DiagramRepository diagramRepository;
    private final RuntimeEnvironmentService runtime;
    private final Window window;

    public MainMenuBarUi(Window window) {
        this.window = window;
        this.jarRepository = VisualTestSystem.getInstance().getJarRepository();
        this.diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        this.runtime = VisualTestSystem.getInstance().getRuntimeEnvironmentService();

        /* Project MenuItem */
        Menu projectMenu = new Menu("Project");
        {
            MenuItem infoItem = new MenuItem("Information");
            infoItem.setOnAction(_ -> ProjectWindowUi.showWindow(getScene().getWindow()));

            MenuItem saveItem = new MenuItem("Save Project");
            saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
            saveItem.setOnAction(this::onSave);

            MenuItem loadItem = new MenuItem("Load Project");
            loadItem.setOnAction(this::onLoad);

            MenuItem createProjectItem = new MenuItem("New Project");
//            createProjectItem.setOnAction(_ -> createProject());

            MenuItem addJarItem = new MenuItem("Add JAR");
            addJarItem.setOnAction(this::onAddJar);
            MenuItem createDiagramItem = new MenuItem("New Diagram");
            createDiagramItem.setOnAction(_ -> diagramRepository.createDiagram());

            projectMenu.getItems().addAll(
                    infoItem, new SeparatorMenuItem(),
                    saveItem, loadItem, createProjectItem, new SeparatorMenuItem(),
                    addJarItem, new SeparatorMenuItem(),
                    createDiagramItem);
        }

        Menu runMenu = new Menu("Run");
        {
            MenuItem runItem = new MenuItem("Run Diagram");
            runItem.setOnAction(_ -> handleRun());
            runItem.disableProperty().bind(
                    diagramRepository.selectedDiagramProperty().isNull()
                            .or(runtime.runningTaskProperty()));

            CheckMenuItem debugItem = new CheckMenuItem("Debug (Step Mode)");
            debugItem.selectedProperty().bindBidirectional(runtime.stepModeProperty());

            MenuItem stepItem = new MenuItem("Step");
            stepItem.setOnAction(_ -> runtime.setStepFlag());
            stepItem.disableProperty().bind(
                    debugItem.selectedProperty().not()
                            .or(runtime.runningTaskProperty().not()));

            MenuItem stopItem = new MenuItem("Stop");
            stopItem.setOnAction(_ -> handleStop());
            stopItem.disableProperty().bind(runtime.runningTaskProperty().not());

            runMenu.getItems().addAll(runItem, new SeparatorMenuItem(), debugItem, stepItem, new SeparatorMenuItem(), stopItem);
        }

        Menu viewMenu = new Menu("View");
        {
            CheckMenuItem explainItem = new CheckMenuItem("Explain Mode");
            explainItem.selectedProperty().bindBidirectional(
                    UiPreferencesService.getInstance().explainModeProperty());

            Menu zoomMenu = new Menu("Zoom");
            record ZoomEntry(DiagramZoomLevel level, String label) {}
            ZoomEntry[] zoomEntries = {
                new ZoomEntry(DiagramZoomLevel.THREE_QUARTERS_OUT, "25%"),
                new ZoomEntry(DiagramZoomLevel.HALF_OUT,           "50%"),
                new ZoomEntry(DiagramZoomLevel.QUARTER_OUT,        "75%"),
                new ZoomEntry(DiagramZoomLevel.DEFAULT,            "100%"),
                new ZoomEntry(DiagramZoomLevel.QUARTER_IN,         "125%"),
                new ZoomEntry(DiagramZoomLevel.HALF_IN,            "150%"),
                new ZoomEntry(DiagramZoomLevel.THREE_QUARTERS_IN,  "175%"),
            };
            for (ZoomEntry entry : zoomEntries) {
                ZoomLevelMenuItemUi item = new ZoomLevelMenuItemUi(entry.label());
                item.currentZoomLevelProperty().bind(
                        UiPreferencesService.getInstance().zoomLevelProperty().isEqualTo(entry.level()));
                item.setOnAction(_ ->
                        UiPreferencesService.getInstance().zoomLevelProperty().set(entry.level()));
                zoomMenu.getItems().add(item);
            }

            CheckMenuItem verticalOrientationItem = new CheckMenuItem("Vertical Orientation");
            verticalOrientationItem.selectedProperty().bindBidirectional(
                    UiPreferencesService.getInstance().verticalOrientationProperty());

            viewMenu.getItems().addAll(explainItem, zoomMenu, new SeparatorMenuItem(), verticalOrientationItem);
        }

        getMenus().addAll(projectMenu, runMenu, viewMenu);

        initAppSettings();

        if (window instanceof Stage stage)
            stage.setOnCloseRequest(event -> {
                boolean anyUnsaved = diagramRepository.getDiagrams().stream()
                        .anyMatch(d -> d.unsavedProperty().get());
                if (!anyUnsaved) return;

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(window);
                alert.setTitle("Unsaved Changes");
                alert.setHeaderText("You have unsaved changes.");
                alert.setContentText("Would you like to save before closing?");
                ButtonType saveBtn     = new ButtonType("Save",       ButtonBar.ButtonData.YES);
                ButtonType dontSaveBtn = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
                ButtonType cancelBtn   = new ButtonType("Cancel",     ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(saveBtn, dontSaveBtn, cancelBtn);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                    event.consume();
                    return;
                }

                if (result.get().getButtonData() == ButtonBar.ButtonData.YES) {
                    onSave(null);
                    /* If no project directory was chosen (user cancelled the chooser),
                     * the save didn't complete — abort the close so data isn't lost. */
                    boolean stillUnsaved = diagramRepository.getDiagrams().stream()
                            .anyMatch(d -> d.unsavedProperty().get());
                    if (stillUnsaved)
                        event.consume();
                }
            });
    }

    private void initAppSettings() {
        final AppSettingsService appSettings = AppSettingsService.getInstance();

        /* Restore UI preferences */
        UiPreferencesService.getInstance().explainModeProperty()
                .set(appSettings.getSettings().explainMode);

        try {
            DiagramZoomLevel savedZoom = DiagramZoomLevel.valueOf(appSettings.getSettings().zoomLevel);
            UiPreferencesService.getInstance().zoomLevelProperty().set(savedZoom);
        } catch (IllegalArgumentException ignored) {
            /* Unknown value in settings — leave at default */
        }

        /* Persist UI preferences whenever they change */
        UiPreferencesService.getInstance().explainModeProperty()
                .addListener((_, _, val) -> {
                    appSettings.getSettings().explainMode = val;
                    appSettings.save();
                });

        UiPreferencesService.getInstance().zoomLevelProperty()
                .addListener((_, _, val) -> {
                    appSettings.getSettings().zoomLevel = val.name();
                    appSettings.save();
                });

        UiPreferencesService.getInstance().verticalOrientationProperty()
                .set(appSettings.getSettings().verticalOrientation);

        UiPreferencesService.getInstance().verticalOrientationProperty()
                .addListener((_, _, val) -> {
                    appSettings.getSettings().verticalOrientation = val;
                    appSettings.save();
                });

        /* Auto-reopen last project after the UI is fully shown */
        File lastDir = appSettings.getLastProjectDirectory();
        if (lastDir != null) {
            Platform.runLater(() -> {
                try {
                    VisualTestSystem.getInstance().getPersistenceService().load(lastDir);
                } catch (Exception e) {
                    System.err.println("Failed to auto-load last project: " + e.getMessage());
                }
            });
        }
    }

    private void onSave(ActionEvent event) {
        final ObjectProperty<File> projectDirectory = VisualTestSystem.getInstance()
                .getProjectInformation().projectDirectoryProperty();

        /* If the project directory is null, prompt to choose a directory */
        if(projectDirectory.get() == null) {
            projectDirectory.set(promptProjectDirectoryChooser("Save Project"));
        }

        SaveResult result = SaveResult.UNKNOWN_FAILURE;
        try {
            result = VisualTestSystem.getInstance().getPersistenceService().save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == SaveResult.SUCCESS) {
            AppSettingsService.getInstance().setLastProjectDirectory(projectDirectory.get());
        } else {
            ToastUi.make(getScene().getWindow(), "Save failed: " + result.name(),
                    0, 2000, 500);
        }
    }

    private void onLoad(ActionEvent event) {
        /* Prompt the user for a directory to load */
        File dir = promptProjectDirectoryChooser("Load Project");
        if (dir == null) return;

        /* TODO: Ask if user would like to save current project first, before resetting system */
        VisualTestSystem.getInstance().resetSystem();

        try {
            VisualTestSystem.getInstance().getPersistenceService().load(dir);
            AppSettingsService.getInstance().setLastProjectDirectory(dir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private void createProject() {
//        File selectedDir = promptProjectDirectoryChooser();
//
//        /* TODO: Ask if user would like to save current project first, before resetting system */
//        VisualTestSystem.getInstance().resetSystem();
//
//        try {
//            VisualTestSystem.getInstance().getPersistenceService().load(selectedDir);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void onAddJar(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JAR");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar Files", "*.jar"),
                new FileChooser.ExtensionFilter("Zip Files", "*.zip", "*.7z"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        /* Set initial directory to current working directory */
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        final File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        final Optional<LoadedJar> jar = this.jarRepository.loadJar(selectedFile);
        final Window window = getScene().getWindow();
        jar.ifPresentOrElse(
                /* JAR loaded successfully */
                loadedJar -> ToastUi.make(window, loadedJar.getName() + " loaded",
                        500, 1000, 500),
                /* Error loading JAR */
                () -> ToastUi.make(window, "Could not load JAR",
                        500, 1000, 500));
    }

    private File promptProjectDirectoryChooser(String actionTitle) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a Directory : " + actionTitle);

        /* Set initial directory to current working directory */
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        return chooser.showDialog(getScene().getWindow());
    }

    private void handleRun() {
        Diagram diagram = diagramRepository.selectedDiagramProperty().get();
        if (diagram != null)
            runtime.queueTask(new ExecuteTask(diagram));
    }

    private void handleStop() {
        runtime.stopCurrentTask();
    }
}
