package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.IntegerInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

public class TestIntegerInbuiltMethods {

    /* --- gcd --- */

    @Test
    public void testGcdBasic() {
        Assert.assertEquals(4, IntegerInbuiltMethods.gcd(12, 8));
        Assert.assertEquals(6, IntegerInbuiltMethods.gcd(18, 12));
    }

    @Test
    public void testGcdCoprime() {
        Assert.assertEquals(1, IntegerInbuiltMethods.gcd(7, 13));
        Assert.assertEquals(1, IntegerInbuiltMethods.gcd(17, 19));
    }

    /* gcd must treat negative inputs as their absolute values */
    @Test
    public void testGcdNegativeInputs() {
        Assert.assertEquals(4, IntegerInbuiltMethods.gcd(-12,  8));
        Assert.assertEquals(4, IntegerInbuiltMethods.gcd( 12, -8));
        Assert.assertEquals(4, IntegerInbuiltMethods.gcd(-12, -8));
    }

    /* gcd(0, n) = n; gcd(0, 0) = 0 */
    @Test
    public void testGcdZero() {
        Assert.assertEquals(5, IntegerInbuiltMethods.gcd(0, 5));
        Assert.assertEquals(5, IntegerInbuiltMethods.gcd(5, 0));
        Assert.assertEquals(0, IntegerInbuiltMethods.gcd(0, 0));
    }

    /* --- lcm --- */

    @Test
    public void testLcmBasic() {
        Assert.assertEquals(12, IntegerInbuiltMethods.lcm(4, 6));
        Assert.assertEquals(12, IntegerInbuiltMethods.lcm(3, 4));
    }

    /* lcm(0, n) = 0 */
    @Test
    public void testLcmZero() {
        Assert.assertEquals(0, IntegerInbuiltMethods.lcm(0, 5));
        Assert.assertEquals(0, IntegerInbuiltMethods.lcm(5, 0));
    }

    /* lcm of negative values equals lcm of their absolute values */
    @Test
    public void testLcmNegative() {
        Assert.assertEquals(12, IntegerInbuiltMethods.lcm(-4,  6));
        Assert.assertEquals(12, IntegerInbuiltMethods.lcm( 4, -6));
    }

    /* --- clamp --- */

    @Test
    public void testClampBelowMin() {
        Assert.assertEquals(5, IntegerInbuiltMethods.clamp(1, 5, 10));
    }

    @Test
    public void testClampInRange() {
        Assert.assertEquals(7, IntegerInbuiltMethods.clamp(7, 5, 10));
    }

    @Test
    public void testClampAboveMax() {
        Assert.assertEquals(10, IntegerInbuiltMethods.clamp(15, 5, 10));
    }

    @Test
    public void testClampAtBoundary() {
        Assert.assertEquals(5,  IntegerInbuiltMethods.clamp(5, 5, 10));
        Assert.assertEquals(10, IntegerInbuiltMethods.clamp(10, 5, 10));
    }

    /* --- parseHex --- */

    @Test
    public void testParseHex() {
        Assert.assertEquals(255,  IntegerInbuiltMethods.parseHex("ff"));
        Assert.assertEquals(255,  IntegerInbuiltMethods.parseHex("FF"));
        Assert.assertEquals(4096, IntegerInbuiltMethods.parseHex("1000"));
    }

    /* --- Basic arithmetic (one assertion each) --- */

    @Test
    public void testArithmetic() {
        Assert.assertEquals(7,  IntegerInbuiltMethods.add(3, 4));
        Assert.assertEquals(1,  IntegerInbuiltMethods.subtract(4, 3));
        Assert.assertEquals(12, IntegerInbuiltMethods.multiply(3, 4));
        Assert.assertEquals(3,  IntegerInbuiltMethods.divide(12, 4));
        Assert.assertEquals(1,  IntegerInbuiltMethods.mod(7, 3));
        Assert.assertEquals(8,  IntegerInbuiltMethods.power(2, 3));
        Assert.assertEquals(-5, IntegerInbuiltMethods.negate(5));
        Assert.assertEquals(5,  IntegerInbuiltMethods.abs(-5));
    }
}
