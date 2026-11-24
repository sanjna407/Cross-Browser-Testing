package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DynamicControlsPage extends InternetPage {
    private final By checkbox = By.cssSelector("#checkbox-example input[type='checkbox']");
    private final By checkboxButton = By.cssSelector("#checkbox-example button");
    private final By checkboxMessage = By.id("message");
    private final By textInput = By.cssSelector("#input-example input[type='text']");
    private final By inputButton = By.cssSelector("#input-example button");

    public DynamicControlsPage(WebDriver driver) { super(driver); }

    public DynamicControlsPage open() {
        open("/dynamic_controls");
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkbox));
        wait.until(ExpectedConditions.visibilityOfElementLocated(textInput));
        return this;
    }

    public void removeCheckbox() { driver.findElement(checkboxButton).click(); }
    public void waitForCheckboxRemoved() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(checkbox));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(checkboxMessage, "It's gone!"));
    }
    public void addCheckbox() {
        wait.until(ExpectedConditions.elementToBeClickable(checkboxButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkbox));
    }
    public boolean checkboxPresent() { return !driver.findElements(checkbox).isEmpty(); }
    public boolean inputEnabled() { return driver.findElement(textInput).isEnabled(); }
    public void enableInput() { driver.findElement(inputButton).click(); }
    public void waitForInputEnabled() { wait.until(d -> d.findElement(textInput).isEnabled()); }
    public void disableInput() { driver.findElement(inputButton).click(); }
    public void waitForInputDisabled() { wait.until(d -> !d.findElement(textInput).isEnabled()); }
    public WebElement textInput() { return driver.findElement(textInput); }
}