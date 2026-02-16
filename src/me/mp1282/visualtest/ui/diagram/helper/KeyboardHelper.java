package me.mp1282.visualtest.ui.diagram.helper;

import javafx.scene.input.KeyEvent;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class KeyboardHelper {

    private final Diagram diagram;
    private final DiagramRepository repository;
    private final List<Pair<Predicate<KeyEvent>, Runnable>> actionableKeyEvents;

    public KeyboardHelper(DiagramUi diagramUi) {
        this.diagram = diagramUi.getDiagram();
        this.repository = VisualTestSystem.getInstance().getDiagramRepository();
        this.actionableKeyEvents = new ArrayList<>();

        diagramUi.sceneProperty().addListener((_, old, scene) -> {
            if(old   != null) old  .removeEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
            if(scene != null) scene.addEventHandler   (KeyEvent.KEY_PRESSED, this::handleKeyPress);
        });
    }

    public void addKeyHandler(Predicate<KeyEvent> predicate, Runnable action) {
        actionableKeyEvents.add(new Pair<>(predicate, action));
    }

    private void handleKeyPress(KeyEvent e) {
        /* Only handle the key press if this KeyboardHelper is for the currently selected diagram */
        if(diagram.equals(repository.selectedDiagramProperty().get())) {
            actionableKeyEvents.forEach(pair -> {
                if (pair.key().test(e))
                    pair.value().run();
            });
        }
    }
}
