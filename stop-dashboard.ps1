$ErrorActionPreference = 'Stop'
$dashboardDirectory = Join-Path $PSScriptRoot '.dashboard'
$pidFile = Join-Path $dashboardDirectory 'server.pid'

if (-not (Test-Path -LiteralPath $pidFile)) {
    Write-Host 'No dashboard process started by this project was found.'
    exit 0
}

$savedState = Get-Content -LiteralPath $pidFile
if ($savedState.Count -lt 2) {
    Remove-Item -LiteralPath $pidFile -Force
    Write-Host 'Removed a stale dashboard process file.'
    exit 0
}

$serverPid = [int]$savedState[0]
$process = Get-Process -Id $serverPid -ErrorAction SilentlyContinue
if ($process -and $process.ProcessName -eq 'java') {
    $actualStart = [DateTimeOffset]$process.StartTime.ToUniversalTime()
    $expectedStart = [DateTimeOffset]::FromUnixTimeMilliseconds([long]$savedState[1])
    if ([Math]::Abs(($actualStart - $expectedStart).TotalSeconds) -lt 2) {
        Stop-Process -Id $serverPid -Force
        Start-Sleep -Milliseconds 500
        if (Get-Process -Id $serverPid -ErrorAction SilentlyContinue) {
            throw "Could not stop the dashboard Java process (PID $serverPid)."
        }
        Write-Host 'Dashboard stopped.'
    } else {
        Write-Host 'The recorded PID now belongs to a different process; it was left untouched.'
    }
} else {
    Write-Host 'The recorded dashboard process is not running.'
}
Remove-Item -LiteralPath $pidFile -Force -ErrorAction SilentlyContinue
