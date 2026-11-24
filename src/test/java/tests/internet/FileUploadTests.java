package tests.internet;

import base.BaseTest;
import drivers.BrowserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.internet.FileUploadPage;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUploadTests extends BaseTest {
    private static final Path FIXTURE = Path.of("src/test/resources/fixtures/upload-sample.txt");

    @Test(dataProvider = "browserMatrix", dataProviderClass = BrowserDataProvider.class)
    public void uploadProjectFixture(String browser) throws Exception {
        Assert.assertTrue(Files.isRegularFile(FIXTURE),
                "Project upload fixture is missing: " + FIXTURE.toAbsolutePath());
        FileUploadPage page = new FileUploadPage(driver()).open();
        String result = page.upload(FIXTURE);
        Assert.assertEquals(page.heading(), "File Uploaded!");
        Assert.assertEquals(result, FIXTURE.getFileName().toString(),
                "Uploaded page should display the fixture file name");
    }
}