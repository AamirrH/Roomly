const rawBaseUrl = __ENV.BASE_URL || '';

export const BASE_URL = rawBaseUrl.replace(/\/$/, '');

export function requireEnvironment(name) {
  const value = __ENV[name];
  if (!value) {
    throw new Error(`${name} is required`);
  }
  return value;
}

export function integerEnvironment(name, fallback) {
  const raw = __ENV[name];
  if (raw === undefined || raw === '') {
    if (fallback === undefined) throw new Error(`${name} is required`);
    return fallback;
  }

  const value = Number.parseInt(raw, 10);
  if (!Number.isInteger(value) || value < 1) {
    throw new Error(`${name} must be a positive integer`);
  }
  return value;
}

export function assertTargetSafety({ writes = false } = {}) {
  if (!BASE_URL) {
    throw new Error('BASE_URL is required');
  }

  const localTarget = /^https?:\/\/(localhost|127\.0\.0\.1|host\.docker\.internal)(:\d+)?$/i.test(BASE_URL);
  if (!localTarget && __ENV.ALLOW_REMOTE_TARGET !== 'true') {
    throw new Error('Refusing to test a remote target. Set ALLOW_REMOTE_TARGET=true only for a system you own.');
  }

  if (writes && __ENV.ALLOW_BOOKING_WRITES !== 'true') {
    throw new Error('This scenario creates users and bookings. Set ALLOW_BOOKING_WRITES=true to continue.');
  }
}

function isoDateFromNow(offsetDays) {
  const date = new Date();
  date.setUTCDate(date.getUTCDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function stayDates() {
  const offset = integerEnvironment('DATE_OFFSET_DAYS', 14);
  const nights = integerEnvironment('STAY_NIGHTS', 2);
  const checkInDate = __ENV.CHECK_IN_DATE || isoDateFromNow(offset);

  let checkOutDate = __ENV.CHECK_OUT_DATE;
  if (!checkOutDate) {
    const checkout = new Date(`${checkInDate}T00:00:00Z`);
    checkout.setUTCDate(checkout.getUTCDate() + nights);
    checkOutDate = checkout.toISOString().slice(0, 10);
  }

  if (checkOutDate <= checkInDate) {
    throw new Error('CHECK_OUT_DATE must be after CHECK_IN_DATE');
  }

  return { checkInDate, checkOutDate };
}

export function searchPath(city, checkInDate, checkOutDate, rooms = 1, pageSize = 9) {
  const query = [
    `city=${encodeURIComponent(city)}`,
    `checkInDate=${checkInDate}`,
    `checkOutDate=${checkOutDate}`,
    `numberOfRooms=${rooms}`,
    'pageNumber=0',
    `pageSize=${pageSize}`,
  ].join('&');
  return `/roomly/api/v1/hotels/search?${query}`;
}

export function jsonOrNull(response) {
  try {
    return response.json();
  } catch (_) {
    return null;
  }
}

export function loadStages() {
  if ((__ENV.PROFILE || 'quick').toLowerCase() === 'resume') {
    return [
      { duration: '1m', target: 10 },
      { duration: '3m', target: 10 },
      { duration: '1m', target: 25 },
      { duration: '3m', target: 25 },
      { duration: '1m', target: 50 },
      { duration: '5m', target: 50 },
      { duration: '1m', target: 100 },
      { duration: '5m', target: 100 },
      { duration: '1m', target: 0 },
    ];
  }

  return [
    { duration: '10s', target: 2 },
    { duration: '20s', target: 5 },
    { duration: '10s', target: 0 },
  ];
}

export function soakConfiguration() {
  if ((__ENV.PROFILE || 'quick').toLowerCase() === 'resume') {
    return { vus: integerEnvironment('SOAK_VUS', 50), duration: __ENV.SOAK_DURATION || '60m' };
  }
  return { vus: 2, duration: '30s' };
}
