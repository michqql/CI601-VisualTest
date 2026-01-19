package me.mp1282.visualtest.ui.sidebar;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.event.EventBus;
import me.mp1282.visualtest.system.event.types.ClassSelectedEvent;
import me.mp1282.visualtest.system.jarload.LoadedClass;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ClassHierarchyUi extends VBox {

    private interface TreeNode {}
    private final class PackageNode implements TreeNode {
        private final String name;

        public PackageNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final class ClassNode implements TreeNode {
        private final LoadedClass loadedClass;

        public ClassNode(LoadedClass loadedClass) {
            this.loadedClass = loadedClass;
        }

        public LoadedClass getLoadedClass() {
            return loadedClass;
        }

        @Override
        public String toString() {
            return loadedClass.getWrappedClass().getSimpleName();
        }
    }

    private final LoadedJarRepository repository;
    private final TreeView<TreeNode> treeView;
    private final Comparator<TreeItem<TreeNode>> comparator;

    public ClassHierarchyUi() {
        this.repository = VisualTestSystem.getInstance().getJarRepository();
        this.treeView = new TreeView<>();
        this.comparator = (a, b) -> {
            if( a.isLeaf() && !b.isLeaf()) return 1;  /* Leaf nodes   (classes)  go after  */
            if(!a.isLeaf() &&  b.isLeaf()) return -1; /* Branch nodes (packages) go before */

            /* Alphabetical sort */
            return a.getValue().toString().compareToIgnoreCase(b.getValue().toString());
        };

        /* Hide root node of tree view */
        this.treeView.setShowRoot(false);

        /* Add a listener that fires the Class Selected event when the user clicks on a new item in the treeView */
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if(newItem != null && newItem.isLeaf() && newItem.getValue() instanceof ClassNode classNode)
                EventBus.publish(new ClassSelectedEvent(classNode.getLoadedClass()));
        });

        /* Add a listener that rebuilds the tree when the list of loaded JARs changes */
        repository.getLoadedJars().addListener((ListChangeListener<? super LoadedJar>) e -> rebuildTree());

        getChildren().add(treeView);
    }

    private void rebuildTree() {
        TreeItem<TreeNode> root = new TreeItem<>(new PackageNode("root"));
        root.setExpanded(true);

        Map<String, TreeItem<TreeNode>> packageMap = new HashMap<>();
        packageMap.put("", root);

        repository.getLoadedJars().forEach(loadedJar -> {
            StringBuilder path = new StringBuilder();
            loadedJar.getLoadedClassMap().forEach((s, loadedClass) -> {
                String[] parts = s.split("\\.");

                TreeItem<TreeNode> parent = root;
                path.setLength(0); /* Clear any previous text */

                for(int i = 0; i < parts.length; ++i) {
                    /* Append a '.' in between package/class names */
                    if(!path.isEmpty()) path.append('.');
                    path.append(parts[i]);

                    String key = path.toString();
                    TreeItem<TreeNode> node = packageMap.get(key);

                    if(node == null) {
                        /* If this is the last part of the class name split (the class itself),
                         * then make the node a class node, otherwise it will be a package node.
                         */
                        node = new TreeItem<>(i == parts.length - 1 ?
                                new ClassNode(loadedClass) : new PackageNode(parts[i]));
                        node.setExpanded(true);
                        parent.getChildren().add(node);
                        packageMap.put(key, node);
                    }

                    parent = node;
                }
            });
        });

        treeView.setRoot(root);
        sortTree(root);
    }

    private void sortTree(TreeItem<TreeNode> root) {
        if(root == null)
            return;

        FXCollections.sort(root.getChildren(), comparator);

        /* Recursively sort children */
        for(TreeItem<TreeNode> child : root.getChildren())
            sortTree(child);
    }
}
