import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import {
  BASE_URL,
  assertTargetSafety,
  integerEnvironment,
  jsonOrNull,
  requireEnvironment,
} from './lib/config.js';

const requestCount = integerEnvironment('REQUEST_COUNT', 100);
const expectedSuccesses = integerEnvironment('EXPECTED_SUCCESSES', 10);

const bookingSuccesses = new Counter('booking_successes');
const bookingRejections = new Counter('booking_capacity_rejections');
const bookingUnexpected = new Counter('booking_unexpected_responses');
const bookingDuration = new Trend('booking_write_duration', true);

http.setResponseCallback(http.expectedStatuses(200, 409));

export const options = {
  setupTimeout: '10m',
  scenarios: {
    booking_contention: {
      executor: 'per-vu-iterations',
      vus: requestCount,
      iterations: 1,
      maxDuration: '2m',
      gracefulStop: '0s',
    },
  },
  thresholds: {
    checks: ['rate==1'],
    booking_unexpected_responses: ['count==0'],
    booking_successes: [`count==${expectedSuccesses}`],
    booking_capacity_rejections: [`count==${requestCount - expectedSuccesses}`],
    booking_write_duration: ['p(95)<5000'],
  },
};

function normalizedRunId() {
  const value = requireEnvironment('RUN_ID').toLowerCase().replace(/[^a-z0-9]/g, '').slice(0, 8);
  if (value.length < 3) throw new Error('RUN_ID must contain at least three letters or numbers');
  return value;
}

export function setup() {
  assertTargetSafety({ writes: true });
  const runId = normalizedRunId();
  const password = __ENV.TEST_USER_PASSWORD || 'Roomly!Load1A';
  const users = [];
  const signupRequests = [];

  for (let index = 1; index <= requestCount; index += 1) {
    const email = `loadtest.${runId}.${index}@roomly.test`;
    const username = `lt${runId}${index}`;
    users.push({ email, password });
    signupRequests.push({
      method: 'POST',
      url: `${BASE_URL}/roomly/api/v1/signup`,
      body: JSON.stringify({ email, username, password }),
      params: {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'POST /signup (setup)' },
      },
    });
  }

  const signupResponses = http.batch(signupRequests);
  signupResponses.forEach((response, index) => {
    if (response.status !== 200) {
      throw new Error(`Signup ${index + 1} failed with HTTP ${response.status}. Use a new RUN_ID or clean the previous run.`);
    }
  });

  const loginResponses = http.batch(users.map((user) => ({
    method: 'POST',
    url: `${BASE_URL}/roomly/api/v1/login`,
    body: JSON.stringify(user),
    params: {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'POST /login (setup)' },
    },
  })));

  const tokens = loginResponses.map((response, index) => {
    const body = jsonOrNull(response);
    if (response.status !== 200 || !body || !body.accessToken) {
      throw new Error(`Login ${index + 1} failed with HTTP ${response.status}`);
    }
    return body.accessToken;
  });

  return {
    tokens,
    startAt: Date.now() + 5000,
    booking: {
      hotelId: integerEnvironment('HOTEL_ID'),
      roomId: integerEnvironment('ROOM_ID'),
      city: requireEnvironment('CITY'),
      checkInDate: requireEnvironment('CHECK_IN_DATE'),
      checkOutDate: requireEnvironment('CHECK_OUT_DATE'),
      numberOfRooms: 1,
    },
  };
}

export default function (data) {
  const waitMilliseconds = data.startAt - Date.now();
  if (waitMilliseconds > 0) sleep(waitMilliseconds / 1000);

  const response = http.post(`${BASE_URL}/roomly/api/v1/bookings`, JSON.stringify(data.booking), {
    headers: {
      Authorization: `Bearer ${data.tokens[__VU - 1]}`,
      'Content-Type': 'application/json',
    },
    tags: { name: 'POST /bookings', operation: 'contended_booking' },
  });

  bookingDuration.add(response.timings.duration);
  if (response.status === 200) bookingSuccesses.add(1);
  else if (response.status === 409) bookingRejections.add(1);
  else bookingUnexpected.add(1);

  check(response, {
    'booking is committed or safely rejected': (res) => res.status === 200 || res.status === 409,
  });
}
