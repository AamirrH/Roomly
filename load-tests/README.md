# Roomly load-testing suite

This suite produces defensible performance and concurrency evidence for Roomly. It tests the backend API directly; it does not measure browser rendering performance.

## Safety rules

1. Run the write test only against a disposable local or staging database that you own.
2. Rotate the PostgreSQL/Neon, Razorpay, Google API, and JWT secrets before testing. `application.properties` was tracked in older Git history, so ignoring it now does not revoke those values.
3. Never point `booking-contention.js` at the public demo or production database unless you have intentionally prepared and isolated its data.
4. Do not put credentials in this directory. `application-loadtest.properties` reads them from environment variables.
5. Record the hosting tier, database compute size, dataset size, commit SHA, k6 version, and load-generator location with every publishable run.

## What is included

| File | Purpose |
| --- | --- |
| `smoke.js` | Validates health, search, room-list, and room-detail responses with one iteration. |
| `search-load.js` | Generates a 60/25/15 mix of hotel search, available-room, and room-detail traffic. |
| `soak.js` | Repeats searches for 60 minutes at 50 VUs in the resume profile. |
| `booking-contention.js` | Creates distinct test users and sends synchronized booking requests for the same inventory. |
| `sql/prepare-contention.sql` | Resets a seeded room/date range to known capacity. |
| `sql/verify-contention.sql` | Fails unless committed bookings and every inventory-day count match capacity without overselling. |
| `sql/cleanup-contention.sql` | Removes users/bookings from one run and resets its inventory. |
| `run-k6.ps1` | Runs installed k6 or falls back to Docker and exports a timestamped JSON summary. |
| `summarize-result.ps1` | Converts a k6 JSON summary into a Markdown evidence record. |
| `summarize-contention-series.ps1` | Validates and aggregates three or more contention summaries into one résumé evidence record. |

## 1. Prepare the application

Use a database containing `testdb_seed.sql`, then start the backend with the load-test profile. Supply real test-only values through environment variables:

```powershell
$env:LOADTEST_DATABASE_URL = 'jdbc:postgresql://localhost:5432/Roomly'
$env:LOADTEST_DATABASE_USERNAME = 'postgres'
$env:LOADTEST_DATABASE_PASSWORD = '<test-only password>'
$env:LOADTEST_JWT_SECRET = '<long test-only secret>'
$env:LOADTEST_GOOGLE_CALENDAR_API_KEY = '<test key>'
$env:LOADTEST_RAZORPAY_KEY_ID = '<test key id>'
$env:LOADTEST_RAZORPAY_KEY_SECRET = '<test key secret>'
./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=loadtest"
```

The load-test profile disables SQL statement logging, schema mutation, and automatic seed execution so those do not distort or reset a run.

## 2. Validate the scripts

The quick profile is deliberately short:

```powershell
./load-tests/run-k6.ps1 -Script smoke -BaseUrl http://localhost:8081
./load-tests/run-k6.ps1 -Script search-load -BaseUrl http://localhost:8081
./load-tests/run-k6.ps1 -Script soak -BaseUrl http://localhost:8081
```

If k6 is not installed, the runner uses `grafana/k6:latest` through Docker. When Docker is used, a localhost base URL is automatically changed to `host.docker.internal` inside the container.

## 3. Run the résumé search workload

The resume profile ramps through 10, 25, 50, and 100 concurrent virtual users and holds meaningful plateaus:

```powershell
./load-tests/run-k6.ps1 `
  -Script search-load `
  -BaseUrl https://your-staging-host.example `
  -Profile resume `
  -City Mumbai `
  -AllowRemoteTarget
```

Run it three times. Do not run the load generator on the same machine as a local backend when making a hosting-capacity claim. Capture Render request/latency and CPU/RAM graphs plus Neon connection/compute graphs during each run.

## 4. Run the soak workload

The resume default is 50 VUs for 60 minutes. Override it through `SOAK_VUS` and `SOAK_DURATION` when invoking k6 directly if needed.

```powershell
./load-tests/run-k6.ps1 `
  -Script soak `
  -BaseUrl https://your-staging-host.example `
  -Profile resume `
  -City Mumbai `
  -AllowRemoteTarget
```

Watch for latency drift, memory growth, connection exhaustion, and errors over time.

## 5. Prepare the booking-contention fixture

Choose dates inside the seed's 90-day inventory window. The SQL refuses to continue if the target room has overlapping active bookings.

```powershell
$checkIn = (Get-Date).Date.AddDays(14).ToString('yyyy-MM-dd')
$checkOut = (Get-Date).Date.AddDays(16).ToString('yyyy-MM-dd')

psql -d Roomly `
  -v check_in_date=$checkIn `
  -v check_out_date=$checkOut `
  -v capacity=10 `
  -f load-tests/sql/prepare-contention.sql
```

Copy the printed hotel ID and room ID into the next command.

## 6. Execute 100 synchronized booking attempts

Use a fresh 3-8 character alphanumeric run ID every time. The test creates 100 distinct guest accounts, logs each one in, and synchronizes one request per account.

```powershell
./load-tests/run-k6.ps1 `
  -Script booking-contention `
  -BaseUrl http://localhost:8081 `
  -RunId r001 `
  -HotelId <printed-hotel-id> `
  -RoomId <printed-room-id> `
  -City Mumbai `
  -CheckInDate $checkIn `
  -CheckOutDate $checkOut `
  -ExpectedSuccesses 10 `
  -RequestCount 100 `
  -AllowBookingWrites
```

Expected HTTP results: exactly 10 `200` responses, 90 controlled `409` responses, and zero other statuses. A `409` is an expected capacity rejection, not a test failure.

## 7. Verify the database invariant

HTTP results alone do not prove that inventory is correct:

```powershell
psql -d Roomly `
  -v run_id=r001 `
  -v hotel_id=<printed-hotel-id> `
  -v room_id=<printed-room-id> `
  -v check_in_date=$checkIn `
  -v check_out_date=$checkOut `
  -v expected_successes=10 `
  -f load-tests/sql/verify-contention.sql
```

The verifier exits nonzero unless it finds exactly 10 committed reservations from 10 distinct users, all with `RESERVED` status, and every inventory day has `booked_count = 10 <= total_count`.

After saving the evidence, clean the run before repeating it:

```powershell
psql -d Roomly `
  -v run_id=r001 `
  -v hotel_id=<printed-hotel-id> `
  -v room_id=<printed-room-id> `
  -v check_in_date=$checkIn `
  -v check_out_date=$checkOut `
  -f load-tests/sql/cleanup-contention.sql
```

## 8. Produce the evidence record

Every k6 run creates `load-tests/results/<scenario>-<timestamp>.json`. Convert a selected run into Markdown:

```powershell
$sha = git rev-parse --short HEAD
./load-tests/summarize-result.ps1 `
  -ResultPath load-tests/results/search-load-<timestamp>.json `
  -EnvironmentLabel 'Render <instance>, Neon <compute>, loader <region>' `
  -CommitSha $sha `
  -DatasetDescription '10 hotels, 30 rooms, 2,700 inventory-day rows'
```

Keep all three raw summaries. Report the median run, not the best run, and disclose threshold failures.

For a verified contention series, aggregate the three JSON files after all three database checks pass:

```powershell
./load-tests/summarize-contention-series.ps1 `
  -ResultPath (Get-ChildItem load-tests/results/booking-contention-*.json).FullName `
  -EnvironmentLabel 'Local PostgreSQL 17.6; Spring Boot 4.0.2' `
  -CommitSha $sha `
  -DatasetDescription '10-room fixture across 2 inventory days' `
  -DatabaseVerifiedRuns 3
```

## Résumé claim templates

Only replace placeholders after the corresponding evidence exists:

> Prevented inventory overselling using PostgreSQL pessimistic row locking; under 100 simultaneous booking attempts for 10 rooms, exactly 10 reservations committed, 90 were safely rejected, and every inventory-day count remained consistent across three runs.

> Sustained **[RPS] requests/second** across **100 concurrent virtual users** during a **[duration]-minute** mixed search workload, with **[p95] ms p95 latency**, **[p99] ms p99 latency**, and **[error]% HTTP errors** on **[documented Render/Neon configuration]**.

Do not turn 100 requests from one account into “100 concurrent users.” The contention test deliberately uses distinct authenticated accounts so that wording is defensible.
