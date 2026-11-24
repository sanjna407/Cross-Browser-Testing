package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.CheckboxesPage;

public class CheckboxTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void selectAndDeselectCheckboxes(String browser) {
        CheckboxesPage page = new CheckboxesPage(driver()).open();
        Assert.assertFalse(page.isSelected(1), "Checkbox 1 starts unchecked");
        Assert.assertTrue(page.isSelected(2), "Checkbox 2 starts checked");
        page.setSelected(1, true);
        page.setSelected(2, false);
        Assert.assertTrue(page.isSelected(1), "Checkbox 1 should be selected");
        Assert.assertFalse(page.isSelected(2), "Checkbox 2 should be deselected");
        page.setSelected(1, false);
        page.setSelected(2, true);
        Assert.assertFalse(page.isSelected(1), "Checkbox 1 should be deselected");
        Assert.assertTrue(page.isSelected(2), "Checkbox 2 should be selected again");
    }
}