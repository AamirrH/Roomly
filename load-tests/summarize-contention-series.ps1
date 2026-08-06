[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string[]]$ResultPath,

    [Parameter(Mandatory = $true)]
    [string]$EnvironmentLabel,

    [string]$CommitSha = 'not-recorded',
    [string]$DatasetDescription = 'not-recorded',
    [int]$ExpectedSuccesses = 10,
    [int]$RequestCount = 100,
    [int]$DatabaseVerifiedRuns = 0,
    [string]$OutputPath = (Join-Path $PSScriptRoot 'results\booking-contention-series.md')
)

$ErrorActionPreference = 'Stop'
if ($ResultPath.Count -lt 3) { throw 'Provide at least three contention result files.' }

function Metric($summary, [string]$name) {
    $property = $summary.metrics.PSObject.Properties[$name]
    if (-not $property) { throw "Metric '$name' is missing." }
    if ($property.Value.PSObject.Properties['values']) { return $property.Value.values }
    return $property.Value
}

$runs = foreach ($path in $ResultPath) {
    $resolved = (Resolve-Path -LiteralPath $path).Path
    $summary = Get-Content -Raw -LiteralPath $resolved | ConvertFrom-Json
    $success = Metric $summary 'booking_successes'
    $rejection = Metric $summary 'booking_capacity_rejections'
    $unexpected = Metric $summary 'booking_unexpected_responses'
    $duration = Metric $summary 'booking_write_duration'
    $failed = Metric $summary 'http_req_failed'
    $failureRate = if ($failed.PSObject.Properties['rate']) { $failed.rate } else { $failed.value }

    [pscustomobject]@{
        File = [System.IO.Path]::GetFileName($resolved)
        Successes = [int]$success.count
        Rejections = [int]$rejection.count
        Unexpected = [int]$unexpected.count
        P95 = [double]$duration.'p(95)'
        P99 = [double]$duration.'p(99)'
        FailurePercent = [double]$failureRate * 100
    }
}

foreach ($run in $runs) {
    if ($run.Successes -ne $ExpectedSuccesses) { throw "$($run.File) has $($run.Successes) successes; expected $ExpectedSuccesses." }
    if ($run.Rejections -ne ($RequestCount - $ExpectedSuccesses)) { throw "$($run.File) has an unexpected rejection count." }
    if ($run.Unexpected -ne 0 -or $run.FailurePercent -ne 0) { throw "$($run.File) contains unexpected or failed responses." }
}
if ($DatabaseVerifiedRuns -ne $runs.Count) {
    throw "Database verification count ($DatabaseVerifiedRuns) must equal result count ($($runs.Count))."
}

$sortedP95 = @($runs.P95 | Sort-Object)
$medianP95 = $sortedP95[[math]::Floor($sortedP95.Count / 2)]
$maximumP99 = ($runs.P99 | Measure-Object -Maximum).Maximum
$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null

$lines = @(
    '# Roomly booking-contention series',
    '',
    "- Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss K')",
    "- Environment: $EnvironmentLabel",
    "- Commit: $CommitSha",
    "- Dataset: $DatasetDescription",
    "- Runs with passing PostgreSQL invariant verification: $DatabaseVerifiedRuns/$($runs.Count)",
    '',
    '| Run | Committed | Controlled 409s | Unexpected | Write p95 | Write p99 | HTTP failures |',
    '| --- | ---: | ---: | ---: | ---: | ---: | ---: |'
)
foreach ($run in $runs) {
    $lines += "| $($run.File) | $($run.Successes) | $($run.Rejections) | $($run.Unexpected) | $([math]::Round($run.P95, 2)) ms | $([math]::Round($run.P99, 2)) ms | $([math]::Round($run.FailurePercent, 3))% |"
}
$lines += @(
    '',
    "- Median booking-write p95: $([math]::Round($medianP95, 2)) ms",
    "- Maximum booking-write p99: $([math]::Round($maximumP99, 2)) ms",
    '',
    '## Defensible resume claim',
    '',
    "> Prevented inventory overselling with PostgreSQL pessimistic locking; across $($runs.Count) runs of $RequestCount simultaneous booking attempts for $ExpectedSuccesses rooms, exactly $ExpectedSuccesses reservations committed per run, excess requests were safely rejected, and every inventory-day count remained consistent."
)

Set-Content -LiteralPath $OutputPath -Value $lines -Encoding utf8
Write-Host "Contention-series evidence written to $OutputPath"
