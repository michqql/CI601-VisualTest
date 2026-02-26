package me.mp1282.visualtest.system.jarload;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.IReset;
import me.mp1282.visualtest.system.executable.IExecutableTypeHolder;
import me.mp1282.visualtest.util.Preconditions;

import java.io.File;
import java.util.Optional;

public class LoadedJarRepository implements IReset, IExecutableTypeHolder<LoadedMethod> {

    private final ObservableList<LoadedJar> repository = FXCollections.observableArrayList();

    public Optional<LoadedJar> loadJar(File file) {
        Preconditions.fileExists(file);

        try {
            LoadedJar jar = new LoadedJar(file);
            /* Initialize all the LoadedMethod's */
            jar.getLoadedClassMap().values().forEach(clazz ->
                    clazz.getMethods().forEach(method ->
                            method.init(this)));

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

    @Override
    public Optional<LoadedMethod> findExecutableByPersistenceId(String persistenceId) {
        return Optional.empty();
    }

    @Override
    public String getType() {
        return "loaded";
    }
}
