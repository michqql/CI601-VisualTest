package me.mp1282.visualtest.ui.diagram.tab;

import javafx.scene.control.TabPane;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.ui.diagram.DiagramUi;
import me.mp1282.visualtest.util.ObservableListBinder;

public class DiagramTabPaneUi extends TabPane {

    public DiagramTabPaneUi() {
        final DiagramRepository diagramRepository =
                VisualTestSystem.getInstance().getDiagramRepository();

        /* Bind the list of diagrams to the tabs, ensuring that when a new diagram
         * is created, a new tab is also created for it.
         */
        new ObservableListBinder<>(diagramRepository.getDiagrams(), getTabs(),
                (diagram) -> new DiagramTabUi(diagram, new DiagramUi(diagram)),
                (removedDiagram, tab) -> tab.getUserData() == removedDiagram);
    }
}
