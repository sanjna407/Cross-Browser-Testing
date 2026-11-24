package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.DropdownPage;
import java.util.List;

public class DropdownTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void selectEveryAvailableOption(String browser) {
        DropdownPage page = new DropdownPage(driver()).open();
        List<String> options = page.optionValues();
        Assert.assertEquals(options, List.of("1", "2"), "The demo should expose both selectable options");
        for (String option : options) {
            page.select(option);
            Assert.assertEquals(page.selectedValue(), option, "Selected option should match " + option);
        }
    }
}