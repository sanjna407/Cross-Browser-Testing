package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DynamicLoadingPage extends InternetPage {
    private final By startButton = By.cssSelector("#start button");
    private final By finishHeading = By.cssSelector("#finish h4");

    public DynamicLoadingPage(WebDriver driver) { super(driver); }

    public DynamicLoadingPage open(int example) {
        open("/dynamic_loading/" + example);
        wait.until(ExpectedConditions.elementToBeClickable(startButton));
        return this;
    }

    public boolean resultInitiallyVisible() {
        return !driver.findElements(finishHeading).isEmpty()
                && driver.findElement(finishHeading).isDisplayed();
    }

    public String startAndWaitForResult() {
        driver.findElement(startButton).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(finishHeading)).getText();
    }
}