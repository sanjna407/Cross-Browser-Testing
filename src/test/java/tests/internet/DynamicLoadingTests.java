package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.DynamicLoadingPage;

public class DynamicLoadingTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void revealInitiallyHiddenElement(String browser) {
        DynamicLoadingPage page = new DynamicLoadingPage(driver()).open(1);
        Assert.assertFalse(page.resultInitiallyVisible(), "Example 1 result should start hidden");
        Assert.assertEquals(page.startAndWaitForResult(), "Hello World!",
                "Hidden result should appear after Start");
    }

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void waitForDelayedElement(String browser) {
        DynamicLoadingPage page = new DynamicLoadingPage(driver()).open(2);
        Assert.assertFalse(page.resultInitiallyVisible(), "Example 2 result should start absent");
        Assert.assertEquals(page.startAndWaitForResult(), "Hello World!",
                "Delayed result should appear after loading");
    }
}