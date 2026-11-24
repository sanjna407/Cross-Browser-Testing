package base;

import drivers.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void startBrowser(Object[] testParameters) {
        String browser = String.valueOf(testParameters[0]);
        WebDriver driver = DriverFactory.create(browser);
        DRIVER.set(driver);

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    protected WebDriver driver() {
        return DRIVER.get();
    }

    public WebDriver currentDriver() {
        return DRIVER.get();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } finally {
            DRIVER.remove();
        }
    }
}