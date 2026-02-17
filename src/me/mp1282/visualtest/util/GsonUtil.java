package me.mp1282.visualtest.util;

import com.google.gson.JsonElement;

import java.util.function.Function;

public class GsonUtil {

    public static <T> T getAsOrDefault(JsonElement element, Function<JsonElement, T> getAsFunction, T defaultValue) {
        if(element == null)
            return defaultValue;

        return getAsFunction.apply(element);
    }
}
