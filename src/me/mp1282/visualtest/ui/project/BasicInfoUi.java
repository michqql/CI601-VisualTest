package me.mp1282.visualtest.ui.project;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import me.mp1282.visualtest.system.ProjectInformation;
import me.mp1282.visualtest.system.VisualTestSystem;
import me.mp1282.visualtest.system.diagram.DiagramRepository;
import me.mp1282.visualtest.system.jarload.LoadedClass;
import me.mp1282.visualtest.system.jarload.LoadedJar;
import me.mp1282.visualtest.system.jarload.LoadedJarRepository;
import me.mp1282.visualtest.ui.other.GridPane2dInfoUi;

import java.io.File;

public class BasicInfoUi extends GridPane2dInfoUi {

    private int rowCounter;

    public BasicInfoUi() {
        super(5, 10);
        setPadding(new Insets(20));
        
        final ProjectInformation info             = VisualTestSystem.getInstance().getProjectInformation();
        final DiagramRepository diagramRepository = VisualTestSystem.getInstance().getDiagramRepository();
        final LoadedJarRepository jarRepository   = VisualTestSystem.getInstance().getJarRepository();

        addInfoEntry("Project Name", () -> {
            final TextField nameTextField = new TextField();
            nameTextField.setPromptText("Name");
            nameTextField.textProperty().bindBidirectional(info.nameProperty());
            return nameTextField;
        });

        addInfoEntry("Project Directory", () -> {
            final Text filePathText = new Text();
            filePathText.textProperty().bind(Bindings.createStringBinding(() -> {
                    File file = info.projectDirectoryProperty().get();
                    return file == null ? "Project directory not set" : file.getAbsolutePath();
                }, info.projectDirectoryProperty()));
            return filePathText;
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
            numDiagramsText.textProperty().bind(Bindings.size(diagramRepository.getDiagrams()).asString());
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
}
