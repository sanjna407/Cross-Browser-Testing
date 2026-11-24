package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.JavaScriptAlertsPage;

public class JavaScriptAlertTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void handleAlertConfirmAndPrompt(String browser) {
        JavaScriptAlertsPage page = new JavaScriptAlertsPage(driver()).open();
        Assert.assertEquals(page.acceptAlert(), "I am a JS Alert");
        Assert.assertEquals(page.result(), "You successfully clicked an alert");
        Assert.assertEquals(page.confirm(true), "I am a JS Confirm");
        Assert.assertEquals(page.result(), "You clicked: Ok");
        Assert.assertEquals(page.confirm(false), "I am a JS Confirm");
        Assert.assertEquals(page.result(), "You clicked: Cancel");
        Assert.assertEquals(page.prompt("cross-browser input"), "I am a JS prompt");
        Assert.assertEquals(page.result(), "You entered: cross-browser input");
    }
}