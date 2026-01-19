package me.mp1282.visualtest.system.inbuilt.category;

import me.mp1282.visualtest.system.inbuilt.InbuiltFunctionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
}
