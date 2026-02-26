package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.scene.control.Skin;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class SkinFactory {

    public static Skin<?> createSkin(DiagramNodeUi ui) {
        return new DefaultExecutableSkin(ui);
    }
}
