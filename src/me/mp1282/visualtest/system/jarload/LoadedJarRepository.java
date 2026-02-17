package me.mp1282.visualtest.system.jarload;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.IReset;

import java.io.File;
import java.util.Optional;

public class LoadedJarRepository implements IReset {

    private final ObservableList<LoadedJar> repository = FXCollections.observableArrayList();

    public Optional<LoadedJar> loadJar(File file) {
        try {
            LoadedJar jar = new LoadedJar(file);
            repository.add(jar);
            return Optional.of(jar);
        } catch(Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public ObservableList<LoadedJar> getLoadedJars() {
        /* Can't return an unmodifiable copy here because this
         * breaks the observable part.
         */
        return repository;
    }

    @Override
    public void reset() {
        repository.clear();
    }
}
