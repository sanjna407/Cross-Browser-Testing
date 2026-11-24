# CrossBrowserTesting: The Internet

A Java Selenium suite for [The Internet](https://the-internet.herokuapp.com/), run in Google Chrome and Microsoft Edge. The project uses Maven, TestNG, Page Objects, Selenium Manager, and ExtentReports. Selenium Manager resolves compatible browser drivers when the tests start.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Google Chrome and Microsoft Edge installed
- Internet access for Maven dependencies, driver resolution, and the practice site

Firefox is not configured or required. No driver manager installation or system-level browser setup is needed; Selenium Manager handles driver discovery and resolution.

## Run all tests

From this project directory, run:

```powershell
mvn clean test
```

The default suite executes each implemented test in Chrome and Edge. To run one browser:

```powershell
mvn clean test -Dbrowsers=chrome
mvn clean test -Dbrowsers=edge
```

To run without opening browser windows:

```powershell
mvn clean test -Dheadless=true
```

The browser list defaults to `chrome,edge`. Unsupported browser names fail explicitly.

## Implemented coverage

The same TestNG tests run in both browsers. The Internet test package currently covers:

- Form Authentication: valid login, invalid login, and logout
- Checkboxes: select and deselect, then check state
- Dropdown: select and verify each available option
- JavaScript Alerts: alert, confirm, and prompt dialogs
- Frames: iframe and nested frame content, then return to the main document
- Multiple Windows: open a new window, verify it, and return
- Data Tables: inspect table data and check sorting only if the page offers a sortable control
- Dynamic Controls: remove/add an element and disable/enable a control with explicit waits
- Dynamic Loading: reveal hidden content and wait for delayed content
- File Upload: upload the committed fixture at `src/test/resources/fixtures/upload-sample.txt`

Each test is independent and uses explicit waits for asynchronous interactions. Data-table coverage reflects the site's current static example; it does not claim sorting where no sorting control exists.

## Reports and results

Every run creates a timestamped ExtentReports HTML report under `reports/` with browser, status, duration, and failure details. Failure screenshots are saved under `reports/screenshots/`. Maven Surefire output and machine-readable TestNG results are under `target/surefire-reports/`.

The latest report is at the top of `reports/`; earlier runs are retained under `reports/historical/`.

## Project structure

```text
src/test/java/
  base/       shared browser setup and teardown
  drivers/    Chrome/Edge creation and browser data provider
  listeners/  TestNG result capture and failure screenshots
  pages/internet/  Page Objects for The Internet examples
  reports/    ExtentReports setup
  tests/internet/  independent feature tests
src/test/resources/fixtures/  file-upload fixture
```

The canonical TestNG suite is `testng.xml`; Maven Surefire runs only this suite.

## Local project dashboard

The dashboard is a small standalone Java server with no extra Maven dependencies. It is separate from TestNG and does not run Selenium tests when it starts or when the page is refreshed. The start command runs the server in the current terminal so it stays available while that terminal remains open.

In VS Code, open a PowerShell terminal at the `CrossBrowserTesting` project root and start it with:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\start-dashboard.ps1
```

Open [http://localhost:8080](http://localhost:8080) in a browser. Keep the VS Code terminal running while you use the dashboard. To stop from that same terminal, press `Ctrl+C`. The page shows the project stack, Chrome and Edge support, the test modules, and the newest saved HTML report under `reports/`. It labels that report as saved output; it does not present it as a new run. To create fresh test results, run `mvn clean test` separately and refresh the dashboard after the run finishes.

Stop the dashboard from a PowerShell terminal in the project root with:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\stop-dashboard.ps1
```

No Maven command is required to start or stop the dashboard. `-ExecutionPolicy Bypass` applies only to the PowerShell process launched for that command; it does not change the machine execution policy. The start script uses the installed JDK to compile the dashboard into the ignored `.dashboard/` working directory. Its process ID is stored there so the stop script can also stop the server from a second terminal. The dashboard binds to localhost on port 8080; if that port is already in use, stop its current process or free the port before starting the dashboard.
