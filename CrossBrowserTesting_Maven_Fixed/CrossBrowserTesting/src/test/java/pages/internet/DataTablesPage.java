package pages.internet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class DataTablesPage extends InternetPage {
    private final By rows = By.cssSelector("#table1 tbody tr");
    private final By lastNameHeader = By.cssSelector("#table1 thead th:nth-child(1)");

    public DataTablesPage(WebDriver driver) { super(driver); }

    public DataTablesPage open() {
        open("/tables");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(rows, 0));
        return this;
    }

    public List<String> lastNames() {
        return driver.findElements(rows).stream()
                .map(row -> row.findElement(By.cssSelector("td:nth-child(1)")).getText().trim())
                .toList();
    }

    public boolean hasSortableLastNameHeader() {
        WebElement header = driver.findElement(lastNameHeader);
        return !header.findElements(By.cssSelector("a, button, [role='button']")).isEmpty()
                || header.getAttribute("aria-sort") != null;
    }

    public void sortLastNameIfSupported() {
        WebElement header = driver.findElement(lastNameHeader);
        if (!header.findElements(By.cssSelector("a, button, [role='button']")).isEmpty()) {
            header.findElement(By.cssSelector("a, button, [role='button']")).click();
        } else if (header.getAttribute("aria-sort") != null) {
            header.click();
        }
    }
}