package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FormAuthenticationPage extends InternetPage {
    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By flash = By.id("flash");
    private final By logoutButton = By.xpath("//a[normalize-space(.)='Logout' and @href='/logout']");

    public FormAuthenticationPage(WebDriver driver) { super(driver); }

    public FormAuthenticationPage open() {
        open("/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        return this;
    }

    public void login(String user, String pass) {
        driver.findElement(username).sendKeys(user);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(loginButton).click();
        wait.until(d -> d.getCurrentUrl().contains("/secure")
                || d.findElements(flash).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed));
    }

    public String message() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(flash)).getText();
    }

    public boolean isSecureArea() { return driver.getCurrentUrl().contains("/secure"); }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/login"));
    }
}
