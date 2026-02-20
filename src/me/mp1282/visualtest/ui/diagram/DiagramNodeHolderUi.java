package me.mp1282.visualtest.ui.diagram;

import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

import java.util.HashMap;
import java.util.Map;

/** DiagramNodeHolderUi is a dedicated UI element to only hold DiagramNodeUi elements.
 */
public class DiagramNodeHolderUi extends Pane {

    private final Diagram diagram;

    /* A lookup map to find the UI element from the DiagramNode object */
    private final Map<DiagramNode, DiagramNodeUi> nodeToUiMap;

    public DiagramNodeHolderUi(Diagram diagram) {
        this.diagram = diagram;
        this.nodeToUiMap = new HashMap<>();

        /* Listen for changes in the diagram node's map and add/remove children UI as appropriate */
        diagram.nodesProperty().addListener((ListChangeListener<? super DiagramNode>) this::onListChange);
    }

    private void onListChange(ListChangeListener.Change<? extends DiagramNode> change) {
        while(change.next()) {
            if(change.wasAdded()) {
                for(DiagramNode node : change.getAddedSubList())
                    add(node);
            }

            if(change.wasRemoved()) {
                for(DiagramNode node : change.getRemoved())
                    remove(node);
            }
        }
    }

    private void add(DiagramNode node) {
        /* Check if there is already a UI element for this node */
        DiagramNodeUi ui = nodeToUiMap.get(node);
        if(ui != null && ui.getParent().equals(this))
            return;

        /* Add new node */
        getChildren().add(ui = new DiagramNodeUi(node));
        nodeToUiMap.put(node, ui);
    }

    private void remove(DiagramNode node) {
        getChildren().removeIf(child -> (child instanceof DiagramNodeUi ui) && ui.getNode().equals(node));
    }
}
