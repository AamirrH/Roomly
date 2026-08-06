[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$ResultPath,

    [Parameter(Mandatory = $true)]
    [string]$EnvironmentLabel,

    [string]$CommitSha = 'not-recorded',
    [string]$DatasetDescription = 'not-recorded',
    [switch]$DatabaseVerificationPassed
)

$ErrorActionPreference = 'Stop'
$resolvedResult = (Resolve-Path -LiteralPath $ResultPath).Path
$summary = Get-Content -Raw -LiteralPath $resolvedResult | ConvertFrom-Json

function MetricValues([string]$name) {
    $metric = $summary.metrics.PSObject.Properties[$name]
    if (-not $metric) { return $null }
    if ($metric.Value.PSObject.Properties['values']) { return $metric.Value.values }
    return $metric.Value
}

function NumberOrNA($value, [int]$digits = 2) {
    if ($null -eq $value) { return 'n/a' }
    return ([math]::Round([double]$value, $digits)).ToString()
}

$requests = MetricValues 'http_reqs'
$duration = MetricValues 'http_req_duration'
$failures = MetricValues 'http_req_failed'
$checks = MetricValues 'checks'
$iterations = MetricValues 'iterations'
$bookingSuccesses = MetricValues 'booking_successes'
$bookingRejections = MetricValues 'booking_capacity_rejections'
$bookingUnexpected = MetricValues 'booking_unexpected_responses'

$failureRate = if ($failures.PSObject.Properties['rate']) { $failures.rate } else { $failures.value }
$checkRate = if ($checks.PSObject.Properties['rate']) { $checks.rate } else { $checks.value }
$failurePercent = if ($null -ne $failureRate) { [double]$failureRate * 100 } else { $null }
$checkPercent = if ($null -ne $checkRate) { [double]$checkRate * 100 } else { $null }
$createdAt = Get-Date -Format 'yyyy-MM-dd HH:mm:ss K'
$reportPath = [System.IO.Path]::ChangeExtension($resolvedResult, '.md')

$lines = @(
    '# Roomly load-test evidence',
    '',
    "- Generated: $createdAt",
    "- Environment: $EnvironmentLabel",
    "- Commit: $CommitSha",
    "- Dataset: $DatasetDescription",
    "- Raw k6 summary: $([System.IO.Path]::GetFileName($resolvedResult))",
    '',
    '## HTTP results',
    '',
    '| Metric | Result |',
    '| --- | ---: |',
    "| Requests | $(NumberOrNA $requests.count 0) |",
    "| Throughput | $(NumberOrNA $requests.rate) req/s |",
    "| Iterations | $(NumberOrNA $iterations.count 0) |",
    "| Median latency | $(NumberOrNA $duration.med) ms |",
    "| p90 latency | $(NumberOrNA $duration.'p(90)') ms |",
    "| p95 latency | $(NumberOrNA $duration.'p(95)') ms |",
    "| p99 latency | $(NumberOrNA $duration.'p(99)') ms |",
    "| HTTP failure rate | $(NumberOrNA $failurePercent 3)% |",
    "| Check pass rate | $(NumberOrNA $checkPercent 3)% |"
)

if ($bookingSuccesses -or $bookingRejections -or $bookingUnexpected) {
    $lines += @(
        '',
        '## Booking-contention results',
        '',
        '| Metric | Result |',
        '| --- | ---: |',
        "| Committed reservations | $(NumberOrNA $bookingSuccesses.count 0) |",
        "| Controlled capacity rejections | $(NumberOrNA $bookingRejections.count 0) |",
        "| Unexpected responses | $(NumberOrNA $bookingUnexpected.count 0) |",
        '',
        "- Database invariant verification: $(if ($DatabaseVerificationPassed) { 'PASSED' } else { 'NOT RECORDED' })",
        '',
        '> The database claim is valid only when `verify-contention.sql` also exits successfully.'
    )
}

$lines += @(
    '',
    '## Reporting checklist',
    '',
    '- [ ] Same test completed three times against a reset dataset.',
    '- [ ] Median run selected; no failed threshold was hidden.',
    '- [ ] Render CPU/RAM and Neon connection metrics were captured.',
    '- [ ] Test duration, virtual users, hosting tier, and dataset size were recorded.',
    "- [$($(if ($DatabaseVerificationPassed) { 'x' } else { ' ' }))] Database invariant verification passed for the contention claim."
)

Set-Content -LiteralPath $reportPath -Value $lines -Encoding utf8
Write-Host "Evidence report written to $reportPath"
