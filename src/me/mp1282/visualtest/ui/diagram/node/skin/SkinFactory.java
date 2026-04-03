package me.mp1282.visualtest.ui.diagram.node.skin;

import javafx.scene.control.Skin;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.inbuilt.special.BranchExecutable;
import me.mp1282.visualtest.system.inbuilt.special.ConstantExecutable;
import me.mp1282.visualtest.system.inbuilt.special.RangeCheckExecutable;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class SkinFactory {

    public static Skin<?> createSkin(DiagramNodeUi ui) {
        Executable executable = ui.getNode().getExecutable();
        if(executable.getClass().equals(ConstantExecutable.class)) {
            return new ConstantExecutableSkin(ui);
        }
        if(executable.getClass().equals(RangeCheckExecutable.class)) {
            return new RangeCheckExecutableSkin(ui);
        }
        if(executable.getClass().equals(BranchExecutable.class)) {
            return new BranchExecutableSkin(ui);
        }

        return new DefaultExecutableSkin(ui);
    }
}
