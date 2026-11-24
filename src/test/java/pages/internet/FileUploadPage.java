package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.nio.file.Path;

public class FileUploadPage extends InternetPage {
    private final By fileInput = By.id("file-upload");
    private final By uploadButton = By.id("file-submit");
    private final By uploadedFileName = By.id("uploaded-files");

    public FileUploadPage(WebDriver driver) { super(driver); }

    public FileUploadPage open() {
        open("/upload");
        wait.until(ExpectedConditions.visibilityOfElementLocated(fileInput));
        return this;
    }

    public String upload(Path file) {
        driver.findElement(fileInput).sendKeys(file.toAbsolutePath().toString());
        driver.findElement(uploadButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadedFileName));
        return driver.findElement(uploadedFileName).getText().trim();
    }

    public String heading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3"))).getText();
    }
}