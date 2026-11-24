package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExtentReportManager {
    private static final DateTimeFormatter REPORT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static ExtentReports extentReports;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            extentReports = createReport();
        }
        return extentReports;
    }

    private static ExtentReports createReport() {
        try {
            Files.createDirectories(Path.of("reports"));
        } catch (Exception e) {
            throw new IllegalStateException("Could not create the reports directory", e);
        }

        String timestamp = LocalDateTime.now().format(REPORT_TIMESTAMP);
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(
                "reports/CrossBrowserTesting-" + timestamp + ".html");
        sparkReporter.config().setDocumentTitle("Cross-Browser Test Results");
        sparkReporter.config().setReportName("Cross-Browser Test Run");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Browsers", System.getProperty("browsers", "chrome,edge"));
        reports.setSystemInfo("Java", System.getProperty("java.version"));
        return reports;
    }
}