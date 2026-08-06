import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, assertTargetSafety, jsonOrNull, searchPath, soakConfiguration, stayDates } from './lib/config.js';

const soak = soakConfiguration();

export const options = {
  scenarios: {
    soak: {
      executor: 'constant-vus',
      vus: soak.vus,
      duration: soak.duration,
      gracefulStop: '30s',
    },
  },
  thresholds: {
    checks: ['rate>0.99'],
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
  },
};

export function setup() {
  assertTargetSafety();
  const city = __ENV.CITY || 'Mumbai';
  const dates = stayDates();
  const response = http.get(`${BASE_URL}${searchPath(city, dates.checkInDate, dates.checkOutDate)}`, {
    tags: { name: 'setup search' },
  });
  const body = jsonOrNull(response);
  const hotels = body && Array.isArray(body.content) ? body.content : [];
  if (response.status !== 200 || hotels.length === 0) throw new Error('Soak setup could not find a hotel');
  return { city, dates };
}

export default function (data) {
  const response = http.get(`${BASE_URL}${searchPath(data.city, data.dates.checkInDate, data.dates.checkOutDate)}`, {
    tags: { name: 'GET /hotels/search', operation: 'soak_search' },
  });
  check(response, { 'soak search is successful': (res) => res.status === 200 });
  sleep(1 + Math.random() * 2);
}
