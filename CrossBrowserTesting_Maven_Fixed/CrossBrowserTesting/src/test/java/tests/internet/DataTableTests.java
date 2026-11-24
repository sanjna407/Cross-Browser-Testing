package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import pages.internet.DataTablesPage;
import java.util.List;

public class DataTableTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void readTableAndCheckSortingWhenAvailable(String browser) {
        DataTablesPage page = new DataTablesPage(driver()).open();
        List<String> names = page.lastNames();
        Assert.assertEquals(names.size(), 4, "Example table should contain four data rows");
        Assert.assertTrue(names.containsAll(List.of("Smith", "Bach", "Doe", "Conway")),
                "The table should expose the expected last-name data");
        if (page.hasSortableLastNameHeader()) {
            page.sortLastNameIfSupported();
            List<String> sorted = page.lastNames();
            Assert.assertEquals(sorted, sorted.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList(),
                    "Last-name sorting should be alphabetical");
        } else {
            Reporter.log("This demo table has no sortable header control; validated row data and structure.", true);
        }
    }
}