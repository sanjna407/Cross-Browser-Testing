package listeners;

import org.openqa.selenium.WebDriverException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Re-runs a test once when it fails because of a browser or network error
 * (for example a page that takes too long to load). Real test failures,
 * such as a failed assertion, are never retried.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRIES = 1;
    private static final Map<String, Integer> ATTEMPTS = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        if (!(result.getThrowable() instanceof WebDriverException)) {
            return false;
        }
        String key = result.getMethod().getQualifiedName() + Arrays.toString(result.getParameters());
        int retriesSoFar = ATTEMPTS.merge(key, 1, Integer::sum) - 1;
        return retriesSoFar < MAX_RETRIES;
    }
}
