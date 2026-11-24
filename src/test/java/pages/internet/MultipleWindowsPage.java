package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.Set;

public class MultipleWindowsPage extends InternetPage {
    private final By newWindowLink = By.cssSelector("a[href='/windows/new']");

    public MultipleWindowsPage(WebDriver driver) { super(driver); }

    public MultipleWindowsPage open() {
        open("/windows");
        wait.until(ExpectedConditions.elementToBeClickable(newWindowLink));
        return this;
    }

    public String openAndReadNewWindow() {
        String original = driver.getWindowHandle();
        Set<String> before = driver.getWindowHandles();
        driver.findElement(newWindowLink).click();
        wait.until(d -> d.getWindowHandles().size() > before.size());
        String child = driver.getWindowHandles().stream().filter(handle -> !before.contains(handle))
                .findFirst().orElseThrow(() -> new AssertionError("The new window did not open"));
        driver.switchTo().window(child);
        String heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3"))).getText();
        driver.close();
        driver.switchTo().window(original);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        return heading;
    }
}