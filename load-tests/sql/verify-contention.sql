\set ON_ERROR_STOP on

-- Required psql variables:
--   run_id, hotel_id, room_id, check_in_date, check_out_date, expected_successes

SELECT
  COUNT(*)::integer AS committed_bookings,
  COUNT(DISTINCT b.user_id)::integer AS distinct_bookers,
  COUNT(*) = :expected_successes::integer AS booking_count_ok,
  COUNT(DISTINCT b.user_id) = :expected_successes::integer AS distinct_users_ok,
  COALESCE(BOOL_AND(b.status = 'RESERVED'), FALSE) AS statuses_ok
FROM bookings b
JOIN users u ON u.id = b.user_id
WHERE u.email LIKE ('loadtest.' || :'run_id' || '.%@roomly.test')
  AND b.hotel_id = :hotel_id
  AND b.room_id = :room_id
  AND b.check_in_date = :'check_in_date'::date
  AND b.checkout_date = :'check_out_date'::date
\gset booking_

SELECT
  COUNT(*)::integer AS affected_inventory_days,
  MIN(booked_count)::integer AS minimum_booked_count,
  MAX(booked_count)::integer AS maximum_booked_count,
  COALESCE(BOOL_AND(booked_count = :expected_successes::integer), FALSE) AS counts_ok,
  COALESCE(BOOL_AND(booked_count <= total_count), FALSE) AS no_oversell,
  COUNT(*) = (:'check_out_date'::date - :'check_in_date'::date) AS all_days_present
FROM inventory
WHERE hotel_id = :hotel_id
  AND room_id = :room_id
  AND date >= :'check_in_date'::date
  AND date < :'check_out_date'::date
\gset inventory_

\echo 'Committed bookings: ' :booking_committed_bookings
\echo 'Distinct booking users: ' :booking_distinct_bookers
\echo 'Affected inventory days: ' :inventory_affected_inventory_days
\echo 'Booked-count range: ' :inventory_minimum_booked_count ' to ' :inventory_maximum_booked_count

\if :booking_booking_count_ok
\else
  \echo 'FAILED: committed booking count does not equal expected capacity.'
  \quit 1
\endif

\if :booking_distinct_users_ok
\else
  \echo 'FAILED: successful bookings did not come from the expected number of distinct users.'
  \quit 1
\endif

\if :booking_statuses_ok
\else
  \echo 'FAILED: one or more committed bookings has an unexpected status.'
  \quit 1
\endif

\if :inventory_counts_ok
\else
  \echo 'FAILED: inventory booked_count differs across the stay or does not equal expected capacity.'
  \quit 1
\endif

\if :inventory_no_oversell
\else
  \echo 'FAILED: inventory was oversold.'
  \quit 1
\endif

\if :inventory_all_days_present
\else
  \echo 'FAILED: one or more inventory days is missing.'
  \quit 1
\endif

\echo 'PASSED: booking count, distinct users, statuses, and every inventory-day invariant are consistent.'
