package drivers;

import org.testng.annotations.DataProvider;

import java.util.Arrays;

public class BrowserDataProvider {
    @DataProvider(name = "browserMatrix", parallel = false)
    public static Object[][] browsers() {
        String configuredBrowsers = System.getProperty("browsers", "chrome,edge");
        Object[][] browserCases = Arrays.stream(configuredBrowsers.split(","))
                .map(String::trim)
                .filter(browser -> !browser.isEmpty())
                .map(browser -> new Object[]{browser})
                .toArray(Object[][]::new);

        if (browserCases.length == 0) {
            throw new IllegalArgumentException("Set at least one browser with -Dbrowsers=chrome or -Dbrowsers=edge.");
        }
        return browserCases;
    }
}