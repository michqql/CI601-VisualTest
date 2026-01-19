package me.mp1282.visualtest.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GenericTypeConverter {
    public static String typeToString(Type type) {
        if (type instanceof Class<?> cls) {
            return cls.getSimpleName();
        }

        if (type instanceof ParameterizedType p) {
            String raw = ((Class<?>) p.getRawType()).getSimpleName();
            String args = Arrays.stream(p.getActualTypeArguments())
                    .map(Type::getTypeName)
                    .map(GenericTypeConverter::shortName) // helper below
                    .collect(Collectors.joining(", "));
            return raw + "<" + args + ">";
        }

        if (type instanceof GenericArrayType g) {
            return typeToString(g.getGenericComponentType()) + "[]";
        }

        return type.getTypeName();
    }

    private static String shortName(String full) {
        int dot = full.lastIndexOf('.');
        return dot >= 0 ? full.substring(dot + 1) : full;
    }
}
