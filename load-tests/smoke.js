import http from 'k6/http';
import { check, group } from 'k6';
import { BASE_URL, assertTargetSafety, jsonOrNull, searchPath, stayDates } from './lib/config.js';

export const options = {
  scenarios: {
    smoke: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      maxDuration: '1m',
    },
  },
  thresholds: {
    checks: ['rate==1'],
    http_req_failed: ['rate==0'],
    http_req_duration: ['p(95)<2000'],
  },
};

export default function () {
  assertTargetSafety();
  const city = __ENV.CITY || 'Mumbai';
  const { checkInDate, checkOutDate } = stayDates();

  group('health', () => {
    const response = http.get(`${BASE_URL}/roomly/api/v1/health`, { tags: { name: 'GET /health' } });
    check(response, { 'health returns 200': (res) => res.status === 200 });
  });

  let hotel;
  group('hotel search', () => {
    const response = http.get(`${BASE_URL}${searchPath(city, checkInDate, checkOutDate)}`, {
      tags: { name: 'GET /hotels/search' },
    });
    const body = jsonOrNull(response);
    hotel = body && Array.isArray(body.content) ? body.content[0] : null;
    check(response, {
      'search returns 200': (res) => res.status === 200,
      'search returns at least one hotel': () => Boolean(hotel && hotel.id),
    });
  });

  if (!hotel) return;

  let room;
  group('available rooms', () => {
    const path = `/roomly/api/v1/hotels/${hotel.id}?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&numberOfRooms=1`;
    const response = http.get(`${BASE_URL}${path}`, { tags: { name: 'GET /hotels/:id' } });
    const body = jsonOrNull(response);
    room = Array.isArray(body) ? body[0] : null;
    check(response, {
      'rooms returns 200': (res) => res.status === 200,
      'rooms returns at least one room': () => Boolean(room && room.id),
    });
  });

  if (!room) return;

  group('room detail', () => {
    const response = http.get(`${BASE_URL}/roomly/api/v1/hotels/${hotel.id}/rooms/${room.id}`, {
      tags: { name: 'GET /hotels/:hotelId/rooms/:roomId' },
    });
    check(response, { 'room detail returns 200': (res) => res.status === 200 });
  });
}
