package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.FormAuthenticationPage;

public class FormAuthenticationTests extends BaseTest {
    private static final String USER = "tomsmith";
    private static final String PASSWORD = "SuperSecretPassword!";

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void validLogin(String browser) {
        FormAuthenticationPage page = new FormAuthenticationPage(driver()).open();
        page.login(USER, PASSWORD);
        Assert.assertTrue(page.isSecureArea(), "Valid credentials should open the secure area");
        Assert.assertTrue(page.message().contains("You logged into a secure area!"),
                "Successful login message should be displayed");
    }

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void invalidLogin(String browser) {
        FormAuthenticationPage page = new FormAuthenticationPage(driver()).open();
        page.login("wrong-user", "wrong-password");
        Assert.assertTrue(page.message().contains("Your username is invalid!"),
                "Invalid username should show an error");
    }

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void logout(String browser) {
        FormAuthenticationPage page = new FormAuthenticationPage(driver()).open();
        page.login(USER, PASSWORD);
        Assert.assertTrue(page.isSecureArea(), "User should be logged in before logout");
        page.logout();
        Assert.assertTrue(driver().getCurrentUrl().endsWith("/login"),
                "Logout should return to the login form");
        Assert.assertTrue(page.message().contains("You logged out of the secure area!"),
                "Logout confirmation should be shown");
    }
}