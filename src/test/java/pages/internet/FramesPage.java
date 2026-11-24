package pages.internet;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FramesPage extends InternetPage {
    private final By iframeLink = By.cssSelector("a[href='/iframe']");

    public FramesPage(WebDriver driver) { super(driver); }

    public FramesPage open() {
        open("/frames");
        wait.until(ExpectedConditions.elementToBeClickable(iframeLink));
        return this;
    }

    public void enterEditorFrame() {
        driver.findElement(iframeLink).click();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mce_0_ifr")));
    }

    public boolean isEditorReady() {
    WebElement editor = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("tinymce"))
    );

    return editor.isDisplayed()
            && "true".equals(editor.getAttribute("contenteditable"));
}

    public void enterNestedMiddleFrame() {
        open("/nested_frames");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("frame[name='frame-top']")));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("frame[name='frame-middle']")));
    }

    public String nestedFrameText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))).getText().trim();
    }

    public void returnToMainPage() {
        driver.switchTo().defaultContent();
        // The nested-frames example is a frameset document and has no normal body element.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("html")));
    }
}