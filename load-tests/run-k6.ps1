[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('smoke', 'search-load', 'soak', 'booking-contention')]
    [string]$Script,

    [Parameter(Mandatory = $true)]
    [string]$BaseUrl,

    [ValidateSet('quick', 'resume')]
    [string]$Profile = 'quick',

    [string]$City = 'Mumbai',
    [string]$CheckInDate,
    [string]$CheckOutDate,
    [int]$HotelId,
    [int]$RoomId,
    [int]$ExpectedSuccesses = 10,
    [int]$RequestCount = 100,
    [string]$RunId,
    [switch]$AllowRemoteTarget,
    [switch]$AllowBookingWrites,
    [string]$K6Image = 'grafana/k6:latest'
)

$ErrorActionPreference = 'Stop'
$loadTestRoot = $PSScriptRoot
$resultsRoot = Join-Path $loadTestRoot 'results'
New-Item -ItemType Directory -Force -Path $resultsRoot | Out-Null

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$resultFileName = "$Script-$timestamp.json"
$hostResultPath = Join-Path $resultsRoot $resultFileName

$environment = [ordered]@{
    BASE_URL = $BaseUrl.TrimEnd('/')
    PROFILE  = $Profile
    CITY     = $City
}

if ($CheckInDate) { $environment.CHECK_IN_DATE = $CheckInDate }
if ($CheckOutDate) { $environment.CHECK_OUT_DATE = $CheckOutDate }
if ($AllowRemoteTarget) { $environment.ALLOW_REMOTE_TARGET = 'true' }

if ($Script -eq 'booking-contention') {
    if (-not $AllowBookingWrites) { throw 'booking-contention requires -AllowBookingWrites.' }
    if (-not $RunId -or $RunId -notmatch '^[A-Za-z0-9]{3,8}$') {
        throw 'booking-contention requires -RunId containing 3-8 letters or numbers.'
    }
    if ($HotelId -lt 1 -or $RoomId -lt 1 -or -not $CheckInDate -or -not $CheckOutDate) {
        throw 'booking-contention requires -HotelId, -RoomId, -CheckInDate, and -CheckOutDate.'
    }
    if ($ExpectedSuccesses -gt $RequestCount) {
        throw '-ExpectedSuccesses cannot exceed -RequestCount.'
    }
    $environment.ALLOW_BOOKING_WRITES = 'true'
    $environment.RUN_ID = $RunId.ToLowerInvariant()
    $environment.HOTEL_ID = $HotelId.ToString()
    $environment.ROOM_ID = $RoomId.ToString()
    $environment.EXPECTED_SUCCESSES = $ExpectedSuccesses.ToString()
    $environment.REQUEST_COUNT = $RequestCount.ToString()
}

$k6Command = Get-Command 'k6' -ErrorAction SilentlyContinue
$k6Executable = if ($k6Command) { $k6Command.Source } else { $null }
if (-not $k6Executable -and (Test-Path -LiteralPath 'C:\Program Files\k6\k6.exe')) {
    $k6Executable = 'C:\Program Files\k6\k6.exe'
}

if ($k6Executable) {
    $arguments = @(
        'run',
        '--summary-trend-stats', 'avg,min,med,max,p(90),p(95),p(99)',
        '--summary-export', $hostResultPath
    )
    foreach ($entry in $environment.GetEnumerator()) {
        $arguments += @('-e', "$($entry.Key)=$($entry.Value)")
    }
    $arguments += (Join-Path $loadTestRoot "$Script.js")
    & $k6Executable @arguments
    if ($LASTEXITCODE -ne 0) { throw "k6 failed with exit code $LASTEXITCODE" }
}
else {
    $dockerCommand = Get-Command 'docker' -ErrorAction SilentlyContinue
    if (-not $dockerCommand) {
        throw 'Neither k6 nor Docker is available. Install one of them, then rerun this command.'
    }

    if ($environment.BASE_URL -match '^http://(localhost|127\.0\.0\.1)(:\d+)?$') {
        $environment.BASE_URL = $environment.BASE_URL -replace '^http://(localhost|127\.0\.0\.1)', 'http://host.docker.internal'
    }

    $arguments = @(
        'run', '--rm',
        '-v', "${loadTestRoot}:/scripts:ro",
        '-v', "${resultsRoot}:/results"
    )
    foreach ($entry in $environment.GetEnumerator()) {
        $arguments += @('-e', "$($entry.Key)=$($entry.Value)")
    }
    $arguments += @(
        $K6Image,
        'run',
        '--summary-trend-stats', 'avg,min,med,max,p(90),p(95),p(99)',
        '--summary-export', "/results/$resultFileName",
        "/scripts/$Script.js"
    )
    & $dockerCommand.Source @arguments
    if ($LASTEXITCODE -ne 0) { throw "Dockerized k6 failed with exit code $LASTEXITCODE" }
}

Write-Host "k6 summary written to $hostResultPath"
