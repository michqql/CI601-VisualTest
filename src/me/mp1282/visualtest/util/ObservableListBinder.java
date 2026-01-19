package me.mp1282.visualtest.util;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class ObservableListBinder<S, T> {

    private final ObservableList<S> source;
    private final ObservableList<T> target;
    private final Function<S, T> factory;
    private BiPredicate<S, T> removalPredicate;

    public ObservableListBinder(ObservableList<S> source, ObservableList<T> target,
                                Function<S, T> factory, BiPredicate<S, T> removalPredicate) {
        this.source = source;
        this.target = target;
        this.factory = factory;
        this.removalPredicate = removalPredicate;
        bind();
    }

    private void bind() {
        source.addListener((ListChangeListener<? super S>) e -> {
            while(e.next()) {
                if(e.wasAdded()) {
                    for(S item : e.getAddedSubList()) {
                        target.add(factory.apply(item));
                    }
                }

                if(e.wasRemoved()) {
                    for(S removedItem : e.getRemoved()) {
                        target.removeIf(item -> removalPredicate.test(removedItem, item));
                    }
                }
            }
        });
    }
}
