package me.mp1282.visualtest.ui.diagram.node;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.ui.diagram.helper.ConnectionHelper;
import me.mp1282.visualtest.ui.diagram.helper.SelectionHelper;
import me.mp1282.visualtest.ui.diagram.port.DataPortArea;
import me.mp1282.visualtest.util.MouseDelta;
import me.mp1282.visualtest.util.ReadOnlyMap;

import java.util.HashMap;
import java.util.function.Consumer;

/** DiagramNodeHolderUi is a dedicated UI element to only hold DiagramNodeUi elements.
 */
public class DiagramNodeHolderUi extends Pane {

    private final ConnectionHelper connectionHelper;
    private final SelectionHelper selectionHelper;

    /* A lookup map to find the UI element from the DiagramNode object */
    private final DiagramNodeHashMap<DiagramNode, DiagramNodeUi> nodeToUiMap;

    private Consumer<DiagramNodeUi> onAddConsumer;
    private Consumer<DiagramNodeUi> onRemoveConsumer;

    public DiagramNodeHolderUi(final Diagram diagram,
                               ConnectionHelper connectionHelper, SelectionHelper selectionHelper,
                               Consumer<DiagramNodeUi> onAddConsumer, Consumer<DiagramNodeUi> onRemoveConsumer) {
        this.connectionHelper = connectionHelper;
        this.selectionHelper = selectionHelper;
        this.nodeToUiMap = new DiagramNodeHashMap<>();
        this.onAddConsumer = onAddConsumer;
        this.onRemoveConsumer = onRemoveConsumer;

        /* For each node currently in the diagram, add a UI element */
        for(DiagramNode node : diagram.nodesProperty())
            add(node);

        /* Listen for changes in the diagram node's map and add/remove children UI as appropriate */
        diagram.nodesProperty().addListener((ListChangeListener<? super DiagramNode>) this::onListChange);

    }

    public ReadOnlyMap<DiagramNode, DiagramNodeUi> getNodeToUiMap() {
        return nodeToUiMap;
    }

    public void translate(double deltaX, double deltaY) {
        for (Node node : getChildren()) {
            if (node instanceof IDiagramElement) {
                node.setTranslateX(node.getTranslateX() + deltaX);
                node.setTranslateY(node.getTranslateY() + deltaY);
            }
        }
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

        if(onAddConsumer != null)
            onAddConsumer.accept(ui);
    }

    private void remove(DiagramNode node) {
        /* Loop over children and remove a single element */
        ObservableList<Node> children = getChildren();
        for(int i = 0; i < children.size(); ++i) {
            Node child = children.get(i);
            if(child instanceof DiagramNodeUi ui && ui.getNode().equals(node)) {
                children.remove(i);
                break;
            }
        }

        /* Remove from the lookup map */
        DiagramNodeUi ui = nodeToUiMap.remove(node);

        if(onRemoveConsumer != null)
            onRemoveConsumer.accept(ui);
    }

    private static class DiagramNodeHashMap<K, V> extends HashMap<K, V> implements ReadOnlyMap<K, V> {
        /* Empty class. ReadOnlyMap::get points to HashMap::get */
    }
}
