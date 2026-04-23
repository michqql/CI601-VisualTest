package me.mp1282.visualtesttesting.system.inbuilt;

import me.mp1282.visualtest.system.inbuilt.category.TimeInbuiltMethods;
import org.junit.Assert;
import org.junit.Test;

public class TestTimeInbuiltMethods {

    /* --- currentTimeMillis / nanoTime --- */

    /* Verify the value is a plausible Unix timestamp (after 2024-01-01) */
    @Test
    public void testCurrentTimeMillisIsPositive() {
        long millis = TimeInbuiltMethods.currentTimeMillis();
        Assert.assertTrue(millis > 1_700_000_000_000L);
    }

    @Test
    public void testNanoTimeIsPositive() {
        long nano = TimeInbuiltMethods.nanoTime();
        Assert.assertTrue(nano > 0);
    }

    /* Two calls taken close together must be non-decreasing */
    @Test
    public void testCurrentTimeMillisIsNonDecreasing() {
        long t1 = TimeInbuiltMethods.currentTimeMillis();
        long t2 = TimeInbuiltMethods.currentTimeMillis();
        Assert.assertTrue(t2 >= t1);
    }

    /* --- millisToSeconds --- */

    @Test
    public void testMillisToSeconds() {
        Assert.assertEquals(1.0,   TimeInbuiltMethods.millisToSeconds(1000L), 1e-9);
        Assert.assertEquals(0.5,   TimeInbuiltMethods.millisToSeconds(500L),  1e-9);
        Assert.assertEquals(0.0,   TimeInbuiltMethods.millisToSeconds(0L),    1e-9);
        Assert.assertEquals(60.0,  TimeInbuiltMethods.millisToSeconds(60_000L), 1e-9);
    }

    /* --- secondsToMillis --- */

    @Test
    public void testSecondsToMillis() {
        Assert.assertEquals(1000L, TimeInbuiltMethods.secondsToMillis(1.0));
        Assert.assertEquals(500L,  TimeInbuiltMethods.secondsToMillis(0.5));
        Assert.assertEquals(0L,    TimeInbuiltMethods.secondsToMillis(0.0));
    }

    /* --- minutesToMillis --- */

    @Test
    public void testMinutesToMillis() {
        Assert.assertEquals(60_000L,  TimeInbuiltMethods.minutesToMillis(1.0));
        Assert.assertEquals(30_000L,  TimeInbuiltMethods.minutesToMillis(0.5));
        Assert.assertEquals(0L,       TimeInbuiltMethods.minutesToMillis(0.0));
        Assert.assertEquals(120_000L, TimeInbuiltMethods.minutesToMillis(2.0));
    }

    /* --- millisToMinutes --- */

    @Test
    public void testMillisToMinutes() {
        Assert.assertEquals(1.0, TimeInbuiltMethods.millisToMinutes(60_000L),  1e-9);
        Assert.assertEquals(0.5, TimeInbuiltMethods.millisToMinutes(30_000L),  1e-9);
        Assert.assertEquals(0.0, TimeInbuiltMethods.millisToMinutes(0L),       1e-9);
    }

    /* round-trip: convert to millis then back */
    @Test
    public void testSecondsRoundTrip() {
        double seconds = 7.5;
        long millis = TimeInbuiltMethods.secondsToMillis(seconds);
        double back  = TimeInbuiltMethods.millisToSeconds(millis);
        Assert.assertEquals(seconds, back, 1e-9);
    }

    @Test
    public void testMinutesRoundTrip() {
        double minutes = 3.25;
        long millis = TimeInbuiltMethods.minutesToMillis(minutes);
        double back  = TimeInbuiltMethods.millisToMinutes(millis);
        Assert.assertEquals(minutes, back, 1e-9);
    }

    /* --- elapsedMillis --- */

    /* Elapsed time from a past start must be non-negative */
    @Test
    public void testElapsedMillisNonNegative() {
        long start   = TimeInbuiltMethods.currentTimeMillis();
        long elapsed = TimeInbuiltMethods.elapsedMillis(start);
        Assert.assertTrue(elapsed >= 0);
    }

    /* Elapsed time from a start 100 ms in the past must be >= 100 */
    @Test
    public void testElapsedMillisFromPast() {
        long pastStart = TimeInbuiltMethods.currentTimeMillis() - 100L;
        long elapsed   = TimeInbuiltMethods.elapsedMillis(pastStart);
        Assert.assertTrue(elapsed >= 100L);
    }
}
