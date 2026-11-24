package pages.internet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public abstract class InternetPage {
    protected static final String BASE_URL = "https://the-internet.herokuapp.com";
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected InternetPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void open(String path) { driver.get(BASE_URL + path); }
}