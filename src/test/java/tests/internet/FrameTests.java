package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.FramesPage;

public class FrameTests extends BaseTest {
    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void iframeContentAndReturnToMainDocument(String browser) {
        FramesPage page = new FramesPage(driver()).open();
        page.enterEditorFrame();
        Assert.assertTrue(page.isEditorReady(),
        "The editor iframe should contain a visible editable area");
        page.returnToMainPage();
        Assert.assertTrue(driver().getCurrentUrl().endsWith("/iframe"),
                "Returning to default content should leave the driver on the iframe page");
        Assert.assertTrue(driver().getPageSource().contains("TinyMCE"),
                "The iframe page heading should remain available in the main document");
    }

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void nestedFrameContentAndReturnToMainDocument(String browser) {
        FramesPage page = new FramesPage(driver()).open();
        page.enterNestedMiddleFrame();
        Assert.assertEquals(page.nestedFrameText(), "MIDDLE");
        page.returnToMainPage();
        Assert.assertTrue(driver().getCurrentUrl().endsWith("/nested_frames"),
                "The driver should return to the nested-frames page context");
    }
}