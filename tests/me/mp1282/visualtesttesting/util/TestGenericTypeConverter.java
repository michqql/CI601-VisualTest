package me.mp1282.visualtesttesting.util;

import me.mp1282.visualtest.util.GenericTypeConverter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class TestGenericTypeConverter {

    /* Helper class with fields whose generic types can be retrieved via reflection */
    @SuppressWarnings("unused")
    private static class TypeHolder<T> {
        List<String>         listString;
        Map<String, Integer> mapStringInt;
        T[]                  genericArray;
    }

    /* Plain Class<?> -> simple name only */
    @Test
    public void testSimpleClass() {
        Assert.assertEquals("String", GenericTypeConverter.typeToString(String.class));
    }

    /* Primitive class -> primitive name */
    @Test
    public void testPrimitiveClass() {
        Assert.assertEquals("int", GenericTypeConverter.typeToString(int.class));
    }

    /* ParameterizedType: List<String> -> "List<String>" */
    @Test
    public void testParameterizedType() throws Exception {
        Type type = TypeHolder.class.getDeclaredField("listString").getGenericType();
        Assert.assertEquals("List<String>", GenericTypeConverter.typeToString(type));
    }

    /* ParameterizedType with two args: Map<String, Integer> -> "Map<String, Integer>" */
    @Test
    public void testNestedParameterizedType() throws Exception {
        Type type = TypeHolder.class.getDeclaredField("mapStringInt").getGenericType();
        Assert.assertEquals("Map<String, Integer>", GenericTypeConverter.typeToString(type));
    }

    /* GenericArrayType: T[] -> result ends with "[]" */
    @Test
    public void testGenericArrayType() throws Exception {
        Type type = TypeHolder.class.getDeclaredField("genericArray").getGenericType();
        String result = GenericTypeConverter.typeToString(type);
        Assert.assertTrue("Expected result to end with '[]', got: " + result, result.endsWith("[]"));
    }
}
