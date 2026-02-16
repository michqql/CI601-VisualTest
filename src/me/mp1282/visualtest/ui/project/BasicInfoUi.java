package me.mp1282.visualtest.ui.project;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedClass;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;

import java.util.function.Supplier;

public class BasicInfoUi extends GridPane {

    private int rowCounter;

    public BasicInfoUi() {
        setPadding(new Insets(20));
        setHgap(10);
        setVgap(5);
        
        final ProjectInformation info             = VisualTestSystem.getInstance().getProjectInformation();
        final DiagramRepository diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        final LoadedJarRepository jarRepository   = VisualTestSystem.getInstance().getJarRepository();

        addInfoEntry("Project Name", () -> {
            final TextField nameTextField = new TextField();
            nameTextField.setPromptText("Name");
            nameTextField.textProperty().bindBidirectional(info.nameProperty());
            return nameTextField;
        });

        addInfoEntry("System Version", () -> {
            final Text versionText = new Text();
            final StringExpression versionBinding = Bindings.format(
                    "%d.%d", info.versionMajorProperty(), info.versionMinorProperty());

            versionText.textProperty().bind(versionBinding); /* versionText.text = versionBinding */
            return versionText;
        });

        addInfoEntry("No. Diagrams", () -> {
            final Text numDiagramsText = new Text();
            numDiagramsText.textProperty().bind(diagramRepository.numberOfDiagramsProperty().asString());
            return numDiagramsText;
        });

        addInfoEntry("No. JAR/Class/Methods", () -> {
            /* Count number of JARs, classes and methods */
            int jarCount    = 0;
            int classCount  = 0;
            int methodCount = 0;

            for (LoadedJar jar : jarRepository.getLoadedJars()) {
                jarCount++;
                for (LoadedClass loadedClass : jar.getLoadedClassMap().values()) {
                    classCount++;
                    methodCount += loadedClass.getMethodMap().size();
                }
            }

            return new Text(String.format(
                    "%d / %d / %d", jarCount, classCount, methodCount
            ));
        });
    }

    private void addInfoEntry(String name, Supplier<Node> nodeSupplier) {
        final Node node = nodeSupplier.get();
        final Label label = new Label(name);
        label.setLabelFor(node);

        GridPane.setConstraints(label, /* Column => */ 0, /* Row => */ rowCounter);
        GridPane.setConstraints(node,  /* Column => */ 1, /* Row => */ rowCounter);

        getChildren().addAll(label, node);

        rowCounter++;
    }
}
