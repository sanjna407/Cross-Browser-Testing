package pages.internet;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class JavaScriptAlertsPage extends InternetPage {
    private final By alertButton = By.cssSelector("button[onclick='jsAlert()']");
    private final By confirmButton = By.cssSelector("button[onclick='jsConfirm()']");
    private final By promptButton = By.cssSelector("button[onclick='jsPrompt()']");
    private final By result = By.id("result");

    public JavaScriptAlertsPage(WebDriver driver) { super(driver); }

    public JavaScriptAlertsPage open() {
        open("/javascript_alerts");
        wait.until(ExpectedConditions.visibilityOfElementLocated(alertButton));
        return this;
    }

    public String acceptAlert() {
        driver.findElement(alertButton).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public String confirm(boolean accept) {
        driver.findElement(confirmButton).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        if (accept) alert.accept(); else alert.dismiss();
        return text;
    }

    public String prompt(String input) {
        driver.findElement(promptButton).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.sendKeys(input);
        alert.accept();
        return text;
    }

    public String result() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(result)).getText();
    }
}