package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.MathInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

public class TestMathInbuiltMethods {

    private static final double EPS = 1e-9;

    /* --- constants --- */

    @Test
    public void testPi() {
        Assert.assertEquals(Math.PI, MathInbuiltMethods.pi(), EPS);
    }

    @Test
    public void testE() {
        Assert.assertEquals(Math.E, MathInbuiltMethods.e(), EPS);
    }

    /* --- roots --- */

    @Test
    public void testSquareRoot() {
        Assert.assertEquals(3.0,  MathInbuiltMethods.squareRoot(9.0),  EPS);
        Assert.assertEquals(2.0,  MathInbuiltMethods.squareRoot(4.0),  EPS);
        Assert.assertEquals(0.0,  MathInbuiltMethods.squareRoot(0.0),  EPS);
    }

    @Test
    public void testCubeRoot() {
        Assert.assertEquals(2.0,  MathInbuiltMethods.cubeRoot(8.0),   EPS);
        Assert.assertEquals(3.0,  MathInbuiltMethods.cubeRoot(27.0),  EPS);
        Assert.assertEquals(-2.0, MathInbuiltMethods.cubeRoot(-8.0),  EPS);
    }

    /* --- pow / exp / log --- */

    @Test
    public void testPow() {
        Assert.assertEquals(8.0,   MathInbuiltMethods.pow(2.0, 3.0), EPS);
        Assert.assertEquals(1.0,   MathInbuiltMethods.pow(5.0, 0.0), EPS);
        Assert.assertEquals(0.25,  MathInbuiltMethods.pow(2.0, -2.0), EPS);
    }

    @Test
    public void testExp() {
        Assert.assertEquals(Math.E,        MathInbuiltMethods.exp(1.0), EPS);
        Assert.assertEquals(1.0,           MathInbuiltMethods.exp(0.0), EPS);
        Assert.assertEquals(Math.E * Math.E, MathInbuiltMethods.exp(2.0), 1e-6);
    }

    @Test
    public void testLog() {
        Assert.assertEquals(0.0, MathInbuiltMethods.log(1.0),    EPS);
        Assert.assertEquals(1.0, MathInbuiltMethods.log(Math.E), EPS);
    }

    @Test
    public void testLog10() {
        Assert.assertEquals(0.0, MathInbuiltMethods.log10(1.0),   EPS);
        Assert.assertEquals(1.0, MathInbuiltMethods.log10(10.0),  EPS);
        Assert.assertEquals(2.0, MathInbuiltMethods.log10(100.0), EPS);
    }

    /* --- trig --- */

    @Test
    public void testSin() {
        Assert.assertEquals(0.0,  MathInbuiltMethods.sin(0.0),           EPS);
        Assert.assertEquals(1.0,  MathInbuiltMethods.sin(Math.PI / 2.0), 1e-9);
        Assert.assertEquals(0.0,  MathInbuiltMethods.sin(Math.PI),       1e-9);
    }

    @Test
    public void testCos() {
        Assert.assertEquals(1.0,  MathInbuiltMethods.cos(0.0),           EPS);
        Assert.assertEquals(0.0,  MathInbuiltMethods.cos(Math.PI / 2.0), 1e-9);
        Assert.assertEquals(-1.0, MathInbuiltMethods.cos(Math.PI),       1e-9);
    }

    @Test
    public void testTan() {
        Assert.assertEquals(0.0, MathInbuiltMethods.tan(0.0),           EPS);
        Assert.assertEquals(1.0, MathInbuiltMethods.tan(Math.PI / 4.0), 1e-9);
    }

    /* --- inverse trig --- */

    @Test
    public void testAsin() {
        Assert.assertEquals(0.0,           MathInbuiltMethods.asin(0.0), EPS);
        Assert.assertEquals(Math.PI / 2.0, MathInbuiltMethods.asin(1.0), EPS);
    }

    @Test
    public void testAcos() {
        Assert.assertEquals(0.0,           MathInbuiltMethods.acos(1.0), EPS);
        Assert.assertEquals(Math.PI / 2.0, MathInbuiltMethods.acos(0.0), EPS);
    }

    @Test
    public void testAtan() {
        Assert.assertEquals(0.0,           MathInbuiltMethods.atan(0.0), EPS);
        Assert.assertEquals(Math.PI / 4.0, MathInbuiltMethods.atan(1.0), EPS);
    }

    @Test
    public void testAtan2() {
        Assert.assertEquals(Math.PI / 4.0,  MathInbuiltMethods.atan2(1.0,  1.0), EPS);
        Assert.assertEquals(Math.PI / 2.0,  MathInbuiltMethods.atan2(1.0,  0.0), EPS);
        Assert.assertEquals(-Math.PI / 4.0, MathInbuiltMethods.atan2(-1.0, 1.0), EPS);
    }

    /* --- angle conversion --- */

    @Test
    public void testToRadians() {
        Assert.assertEquals(Math.PI,       MathInbuiltMethods.toRadians(180.0), EPS);
        Assert.assertEquals(Math.PI / 2.0, MathInbuiltMethods.toRadians(90.0),  EPS);
        Assert.assertEquals(0.0,           MathInbuiltMethods.toRadians(0.0),   EPS);
    }

    @Test
    public void testToDegrees() {
        Assert.assertEquals(180.0, MathInbuiltMethods.toDegrees(Math.PI),       EPS);
        Assert.assertEquals(90.0,  MathInbuiltMethods.toDegrees(Math.PI / 2.0), EPS);
        Assert.assertEquals(0.0,   MathInbuiltMethods.toDegrees(0.0),           EPS);
    }

    /* --- signum / hypot --- */

    @Test
    public void testSignum() {
        Assert.assertEquals( 1.0, MathInbuiltMethods.signum( 5.0), EPS);
        Assert.assertEquals(-1.0, MathInbuiltMethods.signum(-3.0), EPS);
        Assert.assertEquals( 0.0, MathInbuiltMethods.signum( 0.0), EPS);
    }

    @Test
    public void testHypot() {
        Assert.assertEquals(5.0,        MathInbuiltMethods.hypot(3.0, 4.0),  EPS);
        Assert.assertEquals(Math.sqrt(2), MathInbuiltMethods.hypot(1.0, 1.0), EPS);
        Assert.assertEquals(0.0,        MathInbuiltMethods.hypot(0.0, 0.0),  EPS);
    }
}
