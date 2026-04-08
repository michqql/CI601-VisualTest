package me.mp1282.visualtest.ui.diagram.node;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.diagram.node.DiagramNode;
import me.mp1282.visualtest.ui.diagram.IDiagramElement;
import me.mp1282.visualtest.util.ReadOnlyMap;

import java.util.HashMap;
import java.util.function.Consumer;

/** DiagramNodeHolderUi is a dedicated UI element to only hold DiagramNodeUi elements.
 */
public class DiagramNodeHolderUi extends Pane {

    /* A lookup map to find the UI element from the DiagramNode object */
    private final DiagramNodeHashMap<DiagramNode, DiagramNodeUi> nodeToUiMap;

    private final Diagram diagram;
    private final Consumer<DiagramNodeUi> onAddConsumer;
    private final Consumer<DiagramNodeUi> onRemoveConsumer;

    public DiagramNodeHolderUi(final Diagram diagram,
                               Consumer<DiagramNodeUi> onAddConsumer, Consumer<DiagramNodeUi> onRemoveConsumer) {
        this.diagram = diagram;
        this.nodeToUiMap = new DiagramNodeHashMap<>();
        this.onAddConsumer = onAddConsumer;
        this.onRemoveConsumer = onRemoveConsumer;
        setPickOnBounds(false);

        /* Listen for future changes — initial nodes are added via populate() */
        diagram.nodesProperty().addListener((ListChangeListener<? super DiagramNode>) this::onListChange);
    }

    /**
     * Adds a {@link DiagramNodeUi} for every node already present in the diagram.
     * Must be called after all collaborators (e.g. {@link me.mp1282.visualtest.ui.diagram.port.ConnectorHolderUi})
     * have been constructed so that the {@code onAddConsumer} callback can safely reference them.
     */
    public void populate() {
        for (DiagramNode node : diagram.nodesProperty())
            add(node);
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
