import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class DashboardServer {
    private static final int PORT = 8080;
    private static final Path REPORTS_DIRECTORY = Path.of("reports").toAbsolutePath().normalize();
    private static final Pattern REPORT_NAME = Pattern.compile("CrossBrowserTesting-\\d{8}-\\d{6}\\.html");
    private static final DateTimeFormatter SAVED_AT = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a z");

    private DashboardServer() {
    }

    public static void main(String[] args) throws IOException {
        InetAddress localHost = InetAddress.getByName("localhost");
        HttpServer server = HttpServer.create(new InetSocketAddress(localHost, PORT), 0);
        server.createContext("/", DashboardServer::handleRequest);
        server.start();
        Path processFile = Path.of(".dashboard", "server.pid");
        Files.createDirectories(processFile.getParent());
        long processId = ProcessHandle.current().pid();
        long startedAt = ProcessHandle.current().info().startInstant().orElse(Instant.now()).toEpochMilli();
        Files.writeString(processFile, processId + System.lineSeparator() + startedAt, StandardCharsets.UTF_8);
        System.out.println("CrossBrowserTesting dashboard is available at http://localhost:" + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            try {
                Files.deleteIfExists(processFile);
            } catch (IOException ignored) {
                // The stop script also removes the PID file.
            }
        }, "dashboard-shutdown"));
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/".equals(path)) {
            send(exchange, 200, "text/html; charset=utf-8", dashboardPage());
            return;
        }

        Path file = requestedFile(path);
        if (file == null || !Files.isRegularFile(file)) {
            send(exchange, 404, "text/plain; charset=utf-8", "Not found");
            return;
        }

        String contentType = path.endsWith(".html") ? "text/html; charset=utf-8" : "image/png";
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (var response = exchange.getResponseBody()) {
            response.write(body);
        }
    }

    private static Path requestedFile(String path) {
        String prefix;
        Path directory;
        String fileName;
        if (path.startsWith("/reports/")) {
            prefix = "/reports/";
            directory = REPORTS_DIRECTORY;
            fileName = path.substring(prefix.length());
            if (!REPORT_NAME.matcher(fileName).matches()) {
                return null;
            }
        } else if (path.startsWith("/screenshots/")) {
            prefix = "/screenshots/";
            directory = REPORTS_DIRECTORY.resolve("screenshots").normalize();
            fileName = path.substring(prefix.length());
            if (!fileName.matches("[A-Za-z0-9._-]+\\.png")) {
                return null;
            }
        } else {
            return null;
        }

        if (fileName.contains("/") || fileName.contains("\\")) {
            return null;
        }
        return directory.resolve(fileName).normalize();
    }

    private static String dashboardPage() throws IOException {
        List<Path> savedReports = latestReports();
        String reportCard;
        if (savedReports.isEmpty()) {
            reportCard = """
                    <div class="report-empty">
                      <strong>No saved HTML report found.</strong>
                      <p>Run <code>mvn clean test</code> separately to create a new test result.</p>
                    </div>
                    """;
        } else {
            Path report = savedReports.get(0);
            FileTime modified = Files.getLastModifiedTime(report);
            String reportName = report.getFileName().toString();
            String savedAt = SAVED_AT.format(modified.toInstant().atZone(ZoneId.systemDefault()));
            reportCard = """
                    <div class="report-card">
                      <span class="report-label">Latest available saved report</span>
                      <a class="report-link" href="/reports/%s">%s</a>
                      <span class="saved-at">File saved %s</span>
                    </div>
                    """.formatted(escape(reportName), escape(reportName), escape(savedAt));
        }

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <meta name="theme-color" content="#f5f7fb">
                  <title>CrossBrowserTesting | Local Dashboard</title>
                  <style>
                    :root { color-scheme: light; --ink:#16233b; --muted:#63718a; --line:#e3e9f2; --blue:#315efb; --green:#18794e; --bg:#f5f7fb; }
                    * { box-sizing:border-box; }
                    body { margin:0; background:var(--bg); color:var(--ink); font:15px/1.55 "Segoe UI",Arial,sans-serif; }
                    a { color:var(--blue); }
                    .shell { width:min(1080px,calc(100%% - 40px)); margin:0 auto; }
                    header { padding:48px 0 32px; background:linear-gradient(125deg,#edf2ff,#fff 72%%); border-bottom:1px solid var(--line); }
                    .eyebrow { margin:0 0 10px; color:var(--blue); font-size:12px; font-weight:700; letter-spacing:.13em; text-transform:uppercase; }
                    h1 { margin:0; font-size:clamp(30px,5vw,46px); letter-spacing:-.04em; line-height:1.12; }
                    .intro { max-width:710px; margin:15px 0 0; color:var(--muted); font-size:17px; }
                    main { padding:28px 0 54px; }
                    .top-grid { display:grid; grid-template-columns:1.35fr .85fr; gap:18px; }
                    .card { padding:22px; background:#fff; border:1px solid var(--line); border-radius:16px; box-shadow:0 5px 18px rgba(24,46,86,.035); }
                    .card h2 { margin:0 0 14px; font-size:18px; letter-spacing:-.02em; }
                    .target { display:flex; align-items:center; justify-content:space-between; gap:16px; }
                    .target p { margin:3px 0 0; color:var(--muted); }
                    .pill { display:inline-flex; padding:6px 11px; border-radius:999px; background:#eef2ff; color:#294bd0; font-size:13px; font-weight:700; white-space:nowrap; }
                    .tech-list,.browser-list { display:flex; flex-wrap:wrap; gap:8px; }
                    .tech-list span { padding:7px 10px; border:1px solid var(--line); border-radius:9px; background:#fbfcff; font-size:13px; }
                    .browser-list { margin-top:8px; }
                    .browser { display:flex; align-items:center; gap:9px; padding:11px 13px; border:1px solid var(--line); border-radius:10px; font-weight:650; }
                    .browser-dot { width:9px; height:9px; border-radius:50%%; background:var(--green); }
                    .section { margin-top:22px; }
                    .modules { display:grid; grid-template-columns:repeat(3,1fr); gap:11px; }
                    .module { padding:13px 15px; border:1px solid var(--line); border-radius:11px; background:#fff; }
                    .module strong { display:block; font-size:14px; }
                    .module span { display:block; margin-top:3px; color:var(--muted); font-size:12px; }
                    .report-card { display:flex; flex-direction:column; align-items:flex-start; gap:6px; }
                    .report-label { color:var(--green); font-size:12px; font-weight:750; letter-spacing:.06em; text-transform:uppercase; }
                    .report-link { font-size:17px; font-weight:700; overflow-wrap:anywhere; }
                    .saved-at,.report-empty p { color:var(--muted); font-size:13px; }
                    .report-empty p { margin:5px 0 0; }
                    .notice { display:grid; grid-template-columns:auto 1fr; gap:13px; align-items:start; margin-top:18px; padding:17px 19px; border:1px solid #dce6ff; border-radius:12px; background:#f0f4ff; color:#34466f; }
                    .notice strong { color:#1b2f61; }
                    .notice p { margin:2px 0 0; font-size:13px; }
                    code { padding:2px 5px; border-radius:5px; background:rgba(35,57,108,.08); font: .93em Consolas,monospace; }
                    footer { padding:18px 0 28px; color:var(--muted); font-size:12px; }
                    @media(max-width:760px) { .top-grid { grid-template-columns:1fr; } .modules { grid-template-columns:repeat(2,1fr); } header { padding-top:34px; } }
                    @media(max-width:480px) { .modules { grid-template-columns:1fr; } .target { align-items:flex-start; flex-direction:column; } }
                  </style>
                </head>
                <body>
                  <header><div class="shell">
                    <p class="eyebrow">Local project dashboard</p>
                    <h1>CrossBrowserTesting</h1>
                    <p class="intro">A Selenium and TestNG project that checks The Internet practice site in Google Chrome and Microsoft Edge.</p>
                  </div></header>
                  <main class="shell">
                    <div class="top-grid">
                      <section class="card">
                        <h2>Project target</h2>
                        <div class="target">
                          <div><strong>The Internet</strong><p>Only configured test website</p></div>
                          <a class="pill" href="https://the-internet.herokuapp.com/" target="_blank" rel="noreferrer">Open practice site ↗</a>
                        </div>
                      </section>
                      <section class="card">
                        <h2>Supported browsers</h2>
                        <div class="browser-list"><span class="browser"><i class="browser-dot"></i>Google Chrome</span><span class="browser"><i class="browser-dot"></i>Microsoft Edge</span></div>
                      </section>
                    </div>
                    <section class="card section">
                      <h2>Technology stack</h2>
                      <div class="tech-list"><span>Java</span><span>Selenium WebDriver</span><span>TestNG</span><span>Maven</span><span>Page Object Model</span><span>ExtentReports</span><span>Selenium Manager</span></div>
                    </section>
                    <section class="section">
                      <h2>Test modules</h2>
                      <div class="modules">
                        <div class="module"><strong>Form Authentication</strong><span>Valid login, invalid login, logout</span></div>
                        <div class="module"><strong>Checkboxes</strong><span>Select, deselect, verify states</span></div>
                        <div class="module"><strong>Dropdown</strong><span>Select and verify available options</span></div>
                        <div class="module"><strong>JavaScript Alerts</strong><span>Alert, confirm, and prompt dialogs</span></div>
                        <div class="module"><strong>Frames</strong><span>Iframe and nested frame content</span></div>
                        <div class="module"><strong>Multiple Windows</strong><span>Open, inspect, and return</span></div>
                        <div class="module"><strong>Data Tables</strong><span>Read table data; sorting when available</span></div>
                        <div class="module"><strong>Dynamic Controls</strong><span>Add, remove, enable, disable</span></div>
                        <div class="module"><strong>Dynamic Loading</strong><span>Hidden and delayed content</span></div>
                        <div class="module"><strong>File Upload</strong><span>Upload the project fixture</span></div>
                      </div>
                    </section>
                    <section class="card section">
                      <h2>Saved test results</h2>
                      %s
                    </section>
                    <aside class="notice">
                      <span aria-hidden="true">ℹ</span>
                      <div><strong>Saved results are not a new test run.</strong><p>The report above is the newest HTML file currently saved in <code>reports/</code>. Starting or refreshing this dashboard does not run Selenium. To create new results, run <code>mvn clean test</code> separately; the dashboard will link to the new report after it is saved.</p></div>
                    </aside>
                  </main>
                  <footer class="shell">This local dashboard reads saved report files from this project. It does not start browser tests.</footer>
                </body>
                </html>
                """.formatted(reportCard);
    }

    private static List<Path> latestReports() throws IOException {
        if (!Files.isDirectory(REPORTS_DIRECTORY)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(REPORTS_DIRECTORY)) {
            return files.filter(Files::isRegularFile)
                    .filter(file -> REPORT_NAME.matcher(file.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(DashboardServer::lastModified).reversed())
                    .toList();
        }
    }

    private static FileTime lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file);
        } catch (IOException e) {
            return FileTime.fromMillis(0);
        }
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void send(HttpExchange exchange, int status, String contentType, String content) throws IOException {
        byte[] body = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(status, body.length);
        try (var response = exchange.getResponseBody()) {
            response.write(body);
        }
    }
}
