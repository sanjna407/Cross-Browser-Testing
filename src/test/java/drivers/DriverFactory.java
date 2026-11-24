
package drivers;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.Locale;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver create(String browserName) {
        String browser = browserName.trim().toLowerCase(Locale.ROOT);
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "false"));

        return switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browserName +
                    "'. Choose chrome or edge.");
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.addArguments("--window-size=1440,1000");

        if (headless) {
            options.addArguments("--headless=new");
        }

        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        options.addArguments("--window-size=1440,1000");

        if (headless) {
            options.addArguments("--headless=new");
        }

        return options;
    }
}
