package ar.com.wnc.btctracker.util;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void testCalculatePercentDiffNullValues() {
        final String assertMsg = "MathUtils.calculatePercentDiff null args must return Double.NaN";
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(null, null).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(10.0, null).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(null, 10.0).isNaN());
    }

    @Test
    public void testCalculatePercentDiffNaNValues() {
        final String assertMsg = "MathUtils.calculatePercentDiff NaN args must return Double.NaN";
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(Double.NaN, Double.NaN).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(10.0, Double.NaN).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(Double.NaN, 10.0).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(Double.NaN, null).isNaN());
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(null, Double.NaN).isNaN());
    }

    @Test
    public void testCalculatePercentDiff() {
        final String assertMsg = "MathUtils.calculatePercentDiff unexpected error";
        Assert.assertEquals(assertMsg,
                Double.valueOf(1.0 / 10.0 * 100.0),
                MathUtils.calculatePercentDiff(10.0, 9.0));
        Assert.assertEquals(assertMsg,
                Double.valueOf(-10.0 / 90.0 * 100.0),
                MathUtils.calculatePercentDiff(90.0, 100.0));
        Assert.assertEquals(assertMsg,
                Double.valueOf(0.0),
                MathUtils.calculatePercentDiff(100.0, 100.0));
        Assert.assertEquals(assertMsg,
                Double.valueOf(50.0),
                MathUtils.calculatePercentDiff(1.0, 0.5));
        Assert.assertTrue(assertMsg,
                MathUtils.calculatePercentDiff(0.0, 10.0).isNaN());
    }
}
