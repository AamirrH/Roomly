\set ON_ERROR_STOP on

-- Required psql variables:
--   run_id, hotel_id, room_id, check_in_date, check_out_date
-- Run only after verify-contention.sql has captured the result.

BEGIN;

DELETE FROM bookings
WHERE user_id IN (
  SELECT id FROM users
  WHERE email LIKE ('loadtest.' || :'run_id' || '.%@roomly.test')
);

DELETE FROM user_roles
WHERE user_id IN (
  SELECT id FROM users
  WHERE email LIKE ('loadtest.' || :'run_id' || '.%@roomly.test')
);

DELETE FROM users
WHERE email LIKE ('loadtest.' || :'run_id' || '.%@roomly.test');

UPDATE inventory
SET booked_count = 0,
    updated_at = CURRENT_TIMESTAMP
WHERE hotel_id = :hotel_id
  AND room_id = :room_id
  AND date >= :'check_in_date'::date
  AND date < :'check_out_date'::date;

COMMIT;

\echo 'Removed contention-test bookings/users and reset the selected inventory rows.'
