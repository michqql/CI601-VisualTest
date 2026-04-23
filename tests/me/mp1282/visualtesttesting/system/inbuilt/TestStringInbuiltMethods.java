package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.StringInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

public class TestStringInbuiltMethods {

    /* --- length / isEmpty --- */

    @Test
    public void testLength() {
        Assert.assertEquals(5, StringInbuiltMethods.length("hello"));
        Assert.assertEquals(0, StringInbuiltMethods.length(""));
        Assert.assertEquals(1, StringInbuiltMethods.length("x"));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue (StringInbuiltMethods.isEmpty(""));
        Assert.assertFalse(StringInbuiltMethods.isEmpty("a"));
        Assert.assertFalse(StringInbuiltMethods.isEmpty(" "));
    }

    /* --- charAt / substring --- */

    @Test
    public void testCharAt() {
        Assert.assertEquals("h", StringInbuiltMethods.charAt("hello", 0));
        Assert.assertEquals("o", StringInbuiltMethods.charAt("hello", 4));
    }

    @Test
    public void testSubstring() {
        Assert.assertEquals("llo",   StringInbuiltMethods.substring("hello", 2));
        Assert.assertEquals("hello", StringInbuiltMethods.substring("hello", 0));
    }

    @Test
    public void testSubstringRange() {
        Assert.assertEquals("ell", StringInbuiltMethods.substringRange("hello", 1, 4));
        Assert.assertEquals("he",  StringInbuiltMethods.substringRange("hello", 0, 2));
    }

    /* --- search --- */

    @Test
    public void testIndexOf() {
        Assert.assertEquals(2,  StringInbuiltMethods.indexOf("hello", "ll"));
        Assert.assertEquals(0,  StringInbuiltMethods.indexOf("hello", "he"));
        Assert.assertEquals(-1, StringInbuiltMethods.indexOf("hello", "xyz"));
    }

    @Test
    public void testContains() {
        Assert.assertTrue (StringInbuiltMethods.contains("hello world", "world"));
        Assert.assertFalse(StringInbuiltMethods.contains("hello world", "xyz"));
    }

    @Test
    public void testStartsWith() {
        Assert.assertTrue (StringInbuiltMethods.startsWith("hello", "hel"));
        Assert.assertFalse(StringInbuiltMethods.startsWith("hello", "llo"));
    }

    @Test
    public void testEndsWith() {
        Assert.assertTrue (StringInbuiltMethods.endsWith("hello", "llo"));
        Assert.assertFalse(StringInbuiltMethods.endsWith("hello", "hel"));
    }

    /* --- case / trim / replace / concat --- */

    @Test
    public void testToUpperCase() {
        Assert.assertEquals("HELLO", StringInbuiltMethods.toUpperCase("hello"));
        Assert.assertEquals("HELLO", StringInbuiltMethods.toUpperCase("HELLO"));
    }

    @Test
    public void testToLowerCase() {
        Assert.assertEquals("hello", StringInbuiltMethods.toLowerCase("HELLO"));
        Assert.assertEquals("hello", StringInbuiltMethods.toLowerCase("hello"));
    }

    @Test
    public void testTrim() {
        Assert.assertEquals("hello", StringInbuiltMethods.trim("  hello  "));
        Assert.assertEquals("hello", StringInbuiltMethods.trim("hello"));
        Assert.assertEquals("",      StringInbuiltMethods.trim("   "));
    }

    @Test
    public void testReplace() {
        Assert.assertEquals("hXllX",    StringInbuiltMethods.replace("hello", "o", "X"));
        Assert.assertEquals("hi world", StringInbuiltMethods.replace("hello world", "hello", "hi"));
        Assert.assertEquals("hello",    StringInbuiltMethods.replace("hello", "x", "y"));
    }

    @Test
    public void testConcat() {
        Assert.assertEquals("hello world", StringInbuiltMethods.concat("hello ", "world"));
        Assert.assertEquals("ab",          StringInbuiltMethods.concat("a", "b"));
        Assert.assertEquals("a",           StringInbuiltMethods.concat("a", ""));
    }

    /* --- equality --- */

    @Test
    public void testEquals() {
        Assert.assertTrue (StringInbuiltMethods.equals("abc", "abc"));
        Assert.assertFalse(StringInbuiltMethods.equals("abc", "ABC"));
        Assert.assertFalse(StringInbuiltMethods.equals("abc", "xyz"));
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assert.assertTrue (StringInbuiltMethods.equalsIgnoreCase("abc", "ABC"));
        Assert.assertTrue (StringInbuiltMethods.equalsIgnoreCase("Hello", "hello"));
        Assert.assertFalse(StringInbuiltMethods.equalsIgnoreCase("abc",   "xyz"));
    }

    /* --- parse --- */

    @Test
    public void testParseInt() {
        Assert.assertEquals(42,   StringInbuiltMethods.parseInt("42"));
        Assert.assertEquals(-7,   StringInbuiltMethods.parseInt("-7"));
        Assert.assertEquals(0,    StringInbuiltMethods.parseInt("0"));
    }

    @Test
    public void testParseLong() {
        Assert.assertEquals(9999999999L,  StringInbuiltMethods.parseLong("9999999999"));
        Assert.assertEquals(-1L,          StringInbuiltMethods.parseLong("-1"));
    }

    @Test
    public void testParseFloat() {
        Assert.assertEquals(3.14f, StringInbuiltMethods.parseFloat("3.14"), 0.001f);
        Assert.assertEquals(-1.5f, StringInbuiltMethods.parseFloat("-1.5"), 0.001f);
    }

    @Test
    public void testParseDouble() {
        Assert.assertEquals(3.14159, StringInbuiltMethods.parseDouble("3.14159"), 0.00001);
        Assert.assertEquals(-0.5,    StringInbuiltMethods.parseDouble("-0.5"),    0.0001);
    }

    /* --- from --- */

    @Test
    public void testFromInt() {
        Assert.assertEquals("42",  StringInbuiltMethods.fromInt(42));
        Assert.assertEquals("-7",  StringInbuiltMethods.fromInt(-7));
        Assert.assertEquals("0",   StringInbuiltMethods.fromInt(0));
    }

    @Test
    public void testFromLong() {
        Assert.assertEquals("9999999999", StringInbuiltMethods.fromLong(9999999999L));
        Assert.assertEquals("-1",         StringInbuiltMethods.fromLong(-1L));
    }

    @Test
    public void testFromBoolean() {
        Assert.assertEquals("true",  StringInbuiltMethods.fromBoolean(true));
        Assert.assertEquals("false", StringInbuiltMethods.fromBoolean(false));
    }
}
