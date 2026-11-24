$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$dashboardDirectory = Join-Path $projectRoot '.dashboard'
$classDirectory = Join-Path $dashboardDirectory 'classes'
$pidFile = Join-Path $dashboardDirectory 'server.pid'
$java = (Get-Command java -ErrorAction Stop).Source
$javac = (Get-Command javac -ErrorAction Stop).Source

if (Test-Path -LiteralPath $pidFile) {
    $savedState = Get-Content -LiteralPath $pidFile
    if ($savedState.Count -ge 2) {
        $existing = Get-Process -Id ([int]$savedState[0]) -ErrorAction SilentlyContinue
        if ($existing -and $existing.ProcessName -eq 'java') {
            try {
                $response = Invoke-WebRequest -Uri 'http://127.0.0.1:8080/' -UseBasicParsing -TimeoutSec 2
                if ($response.StatusCode -eq 200) {
                    Write-Host 'Dashboard is already running at http://localhost:8080'
                    exit 0
                }
            } catch { }
        }
    }
    Remove-Item -LiteralPath $pidFile -Force
}

$probe = [System.Net.Sockets.TcpClient]::new()
try {
    $probe.Connect('127.0.0.1', 8080)
    throw 'Port 8080 is already in use. Stop the process using it before starting this dashboard.'
} catch [System.Net.Sockets.SocketException] {
    # No server is listening on the requested port.
} finally {
    $probe.Dispose()
}

New-Item -ItemType Directory -Force -Path $classDirectory | Out-Null
$source = Join-Path $projectRoot 'src/dashboard/java/DashboardServer.java'
& $javac --add-modules jdk.httpserver -encoding UTF-8 -d $classDirectory $source
if ($LASTEXITCODE -ne 0) {
    throw 'Dashboard compilation failed.'
}

Write-Host 'Dashboard is starting at http://localhost:8080'
Write-Host 'Keep this terminal open. Press Ctrl+C here to stop the dashboard.'
& $java --add-modules jdk.httpserver -cp .dashboard/classes DashboardServer
$serverExitCode = $LASTEXITCODE
Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
if ($serverExitCode -ne 0) {
    throw "Dashboard server exited with code $serverExitCode."
}
