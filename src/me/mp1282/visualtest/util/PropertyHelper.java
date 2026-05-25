package me.mp1282.visualtest.util;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Consumer;

public class PropertyHelper {

    /**
     * Executes the {@link java.util.function.Consumer} action if the {@link javafx.beans.value.ObservableValue} is present.
     * @param obs    The value to check if present.
     * @param action The action to run if present.
     * @param <T>    The generic type.
     */
    public static <T> void whenPresent(ObservableValue<T> obs, Consumer<T> action) {
        T val = obs.getValue();
        if(val != null)
            action.accept(val);
    }

    /**
     * Executes the {@link java.util.function.Consumer} action for each
     * {@link javafx.beans.value.ObservableValue} if it's value is present.
     * @param obsList The list of values to check if present.
     * @param action  The action to run if present.
     * @param <T>     The generic type.
     */
    public static <T> void whenPresentForEach(List<ObservableValue<T>> obsList, Consumer<T> action) {
        for(ObservableValue<T> obs : obsList)
            whenPresent(obs, action);
    }

    public static <T> void addListenerForEach(List<ObservableValue<T>> obsList, Consumer<T> action) {
        for (ObservableValue<T> obs : obsList)
            obs.addListener((_, _, newValue) -> action.accept(newValue));
    }

    public static <T> void addListenerThreadSafe(ObservableValue<T> obs, Consumer<T> action) {
        obs.addListener((_, _, newValue) -> {
            Platform.runLater(() -> {
                action.accept(newValue);
            });
        });
    }

    public static <T> void bindList(ObservableList<T> source, ObservableList<T> target) {
        source.addListener((ListChangeListener<? super T>) change ->
                target.setAll(change.getList()));
    }

    public static <T> void bindListThreadSafe(ObservableList<T> source, ObservableList<T> target) {
        source.addListener((ListChangeListener<? super T>) change -> {
            Platform.runLater(() -> {
                target.setAll(change.getList());
            });
        });
    }
}
