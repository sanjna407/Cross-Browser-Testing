package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.MultipleWindowsPage;

public class MultipleWindowsTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void openInspectAndReturnFromNewWindow(String browser) {
        MultipleWindowsPage page = new MultipleWindowsPage(driver()).open();
        Assert.assertEquals(page.openAndReadNewWindow(), "New Window",
                "The child window should show its expected heading");
        Assert.assertTrue(driver().getCurrentUrl().endsWith("/windows"),
                "The original window should remain selected");
    }
}