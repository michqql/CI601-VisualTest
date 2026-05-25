package me.mp1282.visualtesttesting.util;

import com.google.gson.JsonPrimitive;
import me.mp1282.visualtest.util.GsonUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestGsonUtil {

    /* null element must return the supplied default value */
    @Test
    public void testNullElementReturnsDefault() {
        String result = GsonUtil.getAsOrDefault(null, e -> e.getAsString(), "default");
        Assert.assertEquals("default", result);
    }

    /* valid JsonPrimitive(42) -> extraction function applied, returns 42 */
    @Test
    public void testValidIntElementExtracted() {
        int result = GsonUtil.getAsOrDefault(new JsonPrimitive(42), e -> e.getAsInt(), 0);
        Assert.assertEquals(42, result);
    }

    /* valid JsonPrimitive("hello") -> extraction function applied, returns "hello" */
    @Test
    public void testValidStringElementExtracted() {
        String result = GsonUtil.getAsOrDefault(new JsonPrimitive("hello"), e -> e.getAsString(), "");
        Assert.assertEquals("hello", result);
    }

    /* valid JsonPrimitive(true) -> extraction function applied, returns true */
    @Test
    public void testValidBooleanElementExtracted() {
        boolean result = GsonUtil.getAsOrDefault(new JsonPrimitive(true), e -> e.getAsBoolean(), false);
        Assert.assertTrue(result);
    }
}
