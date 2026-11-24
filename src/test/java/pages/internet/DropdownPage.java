package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

public class DropdownPage extends InternetPage {
    private final By dropdown = By.id("dropdown");

    public DropdownPage(WebDriver driver) { super(driver); }

    public DropdownPage open() {
        open("/dropdown");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown));
        return this;
    }

    public List<String> optionValues() {
        return new Select(driver.findElement(dropdown)).getOptions().stream()
                .map(option -> option.getAttribute("value"))
                .filter(value -> value != null && !value.isBlank())
                .toList();
    }

    public void select(String value) { new Select(driver.findElement(dropdown)).selectByValue(value); }

    public String selectedValue() {
        return new Select(driver.findElement(dropdown)).getFirstSelectedOption().getAttribute("value");
    }
}