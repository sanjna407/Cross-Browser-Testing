package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.DynamicControlsPage;

public class DynamicControlTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void removeAddAndEnableDisableControls(String browser) {
        DynamicControlsPage page = new DynamicControlsPage(driver()).open();
        page.removeCheckbox();
        page.waitForCheckboxRemoved();
        Assert.assertFalse(page.checkboxPresent(), "Checkbox should be removed");
        page.addCheckbox();
        Assert.assertTrue(page.checkboxPresent(), "Checkbox should be added again");
        Assert.assertFalse(page.inputEnabled(), "Text field should initially be disabled");
        page.enableInput();
        page.waitForInputEnabled();
        Assert.assertTrue(page.inputEnabled(), "Text field should become enabled");
        page.textInput().sendKeys("enabled");
        page.disableInput();
        page.waitForInputDisabled();
        Assert.assertFalse(page.inputEnabled(), "Text field should become disabled again");
    }
}