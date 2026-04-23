package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.BooleanLogicInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

public class TestBooleanLogicInbuiltMethods {

    /* AND truth table */
    @Test
    public void testAnd() {
        Assert.assertTrue (BooleanLogicInbuiltMethods.and(true,  true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.and(true,  false));
        Assert.assertFalse(BooleanLogicInbuiltMethods.and(false, true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.and(false, false));
    }

    /* NAND truth table */
    @Test
    public void testNand() {
        Assert.assertFalse(BooleanLogicInbuiltMethods.nand(true,  true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.nand(true,  false));
        Assert.assertTrue (BooleanLogicInbuiltMethods.nand(false, true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.nand(false, false));
    }

    /* OR truth table */
    @Test
    public void testOr() {
        Assert.assertTrue (BooleanLogicInbuiltMethods.or(true,  true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.or(true,  false));
        Assert.assertTrue (BooleanLogicInbuiltMethods.or(false, true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.or(false, false));
    }

    /* XOR truth table */
    @Test
    public void testXor() {
        Assert.assertFalse(BooleanLogicInbuiltMethods.xor(true,  true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.xor(true,  false));
        Assert.assertTrue (BooleanLogicInbuiltMethods.xor(false, true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.xor(false, false));
    }

    /* NOR truth table */
    @Test
    public void testNor() {
        Assert.assertFalse(BooleanLogicInbuiltMethods.nor(true,  true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.nor(true,  false));
        Assert.assertFalse(BooleanLogicInbuiltMethods.nor(false, true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.nor(false, false));
    }

    /* NOT */
    @Test
    public void testNot() {
        Assert.assertFalse(BooleanLogicInbuiltMethods.not(true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.not(false));
    }

    /* isEqual truth table */
    @Test
    public void testIsEqual() {
        Assert.assertTrue (BooleanLogicInbuiltMethods.isEqual(true,  true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.isEqual(true,  false));
        Assert.assertFalse(BooleanLogicInbuiltMethods.isEqual(false, true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.isEqual(false, false));
    }

    /* isNotEqual truth table */
    @Test
    public void testIsNotEqual() {
        Assert.assertFalse(BooleanLogicInbuiltMethods.isNotEqual(true,  true));
        Assert.assertTrue (BooleanLogicInbuiltMethods.isNotEqual(true,  false));
        Assert.assertTrue (BooleanLogicInbuiltMethods.isNotEqual(false, true));
        Assert.assertFalse(BooleanLogicInbuiltMethods.isNotEqual(false, false));
    }
}
