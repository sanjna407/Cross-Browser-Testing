package listeners;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import reports.ExtentReportManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestListener implements ITestListener {
    private static final ConcurrentMap<String, ExtentTest> TESTS = new ConcurrentHashMap<>();

    @Override
    public void onTestStart(ITestResult result) {
        String browser = browserName(result);
        ExtentTest test = ExtentReportManager.getInstance()
                .createTest(result.getMethod().getMethodName())
                .assignCategory(browser)
                .assignDevice(browser);
        test.info("Browser: " + browser);
        TESTS.put(testKey(result), test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testFor(result);
        Reporter.getOutput(result).forEach(test::info);

        test.pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testFor(result);
        test.fail(result.getThrowable());

        if (!(result.getInstance() instanceof BaseTest baseTest)) {
            return;
        }

        WebDriver driver = baseTest.currentDriver();
        if (driver == null) {
            return;
        }

        try {
            test.info("Current URL: " + driver.getCurrentUrl());
        } catch (Exception ignored) {
            // The browser may already have closed after a startup or session failure.
        }

        if (driver instanceof TakesScreenshot screenshotDriver) {
            attachScreenshot(test, screenshotDriver, result);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testFor(result);
        if (result.getThrowable() == null) {
            test.skip("Test skipped");
        } else {
            test.skip(result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.getInstance().flush();
    }

    private static void attachScreenshot(ExtentTest test, TakesScreenshot driver, ITestResult result) {
        try {
            Path screenshotDirectory = Path.of("reports", "screenshots");
            Files.createDirectories(screenshotDirectory);

            String fileName = result.getMethod().getMethodName()
                    + "-" + browserName(result)
                    + "-" + System.currentTimeMillis()
                    + ".png";
            Path screenshot = screenshotDirectory.resolve(fileName);
            Files.write(screenshot, driver.getScreenshotAs(OutputType.BYTES));
            test.addScreenCaptureFromPath("screenshots/" + fileName);
        } catch (Exception captureError) {
            test.info("Could not save a failure screenshot: " + captureError.getMessage());
        }
    }

    private static ExtentTest testFor(ITestResult result) {
        return TESTS.computeIfAbsent(
                testKey(result),
                key -> ExtentReportManager.getInstance()
                        .createTest(result.getMethod().getMethodName())
                        .assignCategory(browserName(result))
                        .assignDevice(browserName(result)));
    }

    private static String testKey(ITestResult result) {
        return result.getMethod().getQualifiedName() + "-" + Thread.currentThread().getId();
    }

    private static String browserName(ITestResult result) {
        Object[] parameters = result.getParameters();
        return parameters.length == 0 ? "unknown" : String.valueOf(parameters[0]);
    }
}
