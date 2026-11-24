package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class CheckboxesPage extends InternetPage {
    private final By checkboxes = By.cssSelector("#checkboxes input[type='checkbox']");

    public CheckboxesPage(WebDriver driver) { super(driver); }

    public CheckboxesPage open() {
        open("/checkboxes");
        wait.until(ExpectedConditions.numberOfElementsToBe(checkboxes, 2));
        return this;
    }

    public boolean isSelected(int oneBasedIndex) { return boxes().get(oneBasedIndex - 1).isSelected(); }

    public void setSelected(int oneBasedIndex, boolean selected) {
        WebElement box = boxes().get(oneBasedIndex - 1);
        if (box.isSelected() != selected) {
            wait.until(ExpectedConditions.elementToBeClickable(box)).click();
        }
    }

    private List<WebElement> boxes() { return driver.findElements(checkboxes); }
}