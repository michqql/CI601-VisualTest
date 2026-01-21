package me.mp1282.visualtest.ui.diagram.helper;

import javafx.scene.input.KeyEvent;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class KeyboardHelper {


    private final List<Pair<Predicate<KeyEvent>, Runnable>> actionableKeyEvents;

    public KeyboardHelper(DiagramUi diagramUi) {
        this.actionableKeyEvents = new ArrayList<>();

        diagramUi.sceneProperty().addListener((_, old, scene) -> {
            if(old   != null) old  .setOnKeyPressed(null);
            if(scene != null) scene.setOnKeyPressed(this::handleKeyPress);
        });
    }

    public void addKeyHandler(Predicate<KeyEvent> predicate, Runnable action) {
        actionableKeyEvents.add(new Pair<>(predicate, action));
    }

    private void handleKeyPress(KeyEvent e) {
        actionableKeyEvents.forEach(pair -> {
            if(pair.key().test(e))
                pair.value().run();
        });
    }
}
