\set ON_ERROR_STOP on

-- Required psql variables:
--   check_in_date, check_out_date, capacity
-- This script intentionally mutates a TestDB seed room. Use only on a disposable test database.

BEGIN;

SELECT h.id AS hotel_id, r.id AS room_id, h.city AS city
FROM hotels h
JOIN rooms r ON r.hotel_id = h.id
WHERE h.hotel_name = 'TestDB - Azure Mumbai'
ORDER BY r.id
LIMIT 1
\gset target_

SELECT
  COUNT(*) = (:'check_out_date'::date - :'check_in_date'::date) AS fixture_dates_exist
FROM inventory
WHERE hotel_id = :target_hotel_id
  AND room_id = :target_room_id
  AND date >= :'check_in_date'::date
  AND date < :'check_out_date'::date
\gset

\if :fixture_dates_exist
\else
  \echo 'ERROR: inventory rows do not cover the requested stay. Rerun testdb_seed.sql or select dates inside its 90-day window.'
  ROLLBACK;
  \quit 1
\endif

SELECT COUNT(*) = 0 AS fixture_is_unused
FROM bookings
WHERE hotel_id = :target_hotel_id
  AND room_id = :target_room_id
  AND status <> 'CANCELLED'
  AND check_in_date < :'check_out_date'::date
  AND checkout_date > :'check_in_date'::date
\gset

\if :fixture_is_unused
\else
  \echo 'ERROR: the target room already has overlapping bookings. Clean the prior run or use a fresh disposable database.'
  ROLLBACK;
  \quit 1
\endif

UPDATE rooms
SET total_count = :capacity,
    updated_at = CURRENT_TIMESTAMP
WHERE id = :target_room_id;

UPDATE inventory
SET booked_count = 0,
    total_count = :capacity,
    closed = FALSE,
    surge_factor = 1.00,
    updated_at = CURRENT_TIMESTAMP
WHERE hotel_id = :target_hotel_id
  AND room_id = :target_room_id
  AND date >= :'check_in_date'::date
  AND date < :'check_out_date'::date;

COMMIT;

\echo 'Contention fixture ready. Pass these values to run-k6.ps1:'
\echo 'HOTEL_ID=' :target_hotel_id
\echo 'ROOM_ID=' :target_room_id
\echo 'CITY=' :'target_city'
\echo 'CHECK_IN_DATE=' :'check_in_date'
\echo 'CHECK_OUT_DATE=' :'check_out_date'
\echo 'EXPECTED_SUCCESSES=' :capacity
