package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

@InbuiltFunctionProvider(providerName = "Collections")
public class CollectionsInbuiltMethods {

    public static ArrayList<Object> newArrayList(Object... arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

    public static ArrayList<Object> newEmptyArrayList() {
        return new ArrayList<>();
    }

    public static void add(Collection<Object> collection, Object object) {
        collection.add(object);
    }

    public static int size(Collection<Object> c) {
        return c.size();
    }

    public static Object get(ArrayList<Object> l, int index) {
        return l.get(index);
    }

    public static Object removeAt(ArrayList<Object> l, int index) {
        return l.remove(index);
    }

    public static boolean removeObject(Collection<Object> c, Object o) {
        return c.remove(o);
    }

    public static boolean contains(Collection<Object> c, Object o) {
        return c.contains(o);
    }

    public static void clear(Collection<Object> c) {
        c.clear();
    }

    public static boolean isEmpty(Collection<Object> c) {
        return c.isEmpty();
    }

    public static Object set(ArrayList<Object> l, int index, Object o) {
        return l.set(index, o);
    }

    public static int indexOf(ArrayList<Object> l, Object o) {
        return l.indexOf(o);
    }

    public static void addAll(Collection<Object> c, Collection<Object> other) {
        c.addAll(other);
    }

    public static ArrayList<Object> copy(ArrayList<Object> l) {
        return new ArrayList<>(l);
    }

    public static void reverse(ArrayList<Object> l) {
        Collections.reverse(l);
    }

    public static void swap(ArrayList<Object> l, int i, int j) {
        Collections.swap(l, i, j);
    }

    public static ArrayList<Object> subList(ArrayList<Object> l, int from, int to) {
        return new ArrayList<>(l.subList(from, to));
    }

    public static String join(ArrayList<Object> l, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object o : l) joiner.add(String.valueOf(o));
        return joiner.toString();
    }

    public static int frequency(Collection<Object> c, Object o) {
        return Collections.frequency(c, o);
    }

    public static int lastIndexOf(ArrayList<Object> l, Object o) {
        return l.lastIndexOf(o);
    }
}
