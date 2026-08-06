import http from 'k6/http';
import { check } from 'k6';
import { BASE_URL, assertTargetSafety, jsonOrNull, loadStages, searchPath, stayDates } from './lib/config.js';

export const options = {
  scenarios: {
    mixed_search: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: loadStages(),
      gracefulRampDown: '15s',
    },
  },
  thresholds: {
    checks: ['rate>0.99'],
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<750', 'p(99)<1500'],
    'http_req_duration{name:GET /hotels/search}': ['p(95)<900'],
    'http_req_duration{name:GET /hotels/:id}': ['p(95)<750'],
    'http_req_duration{name:GET /hotels/:hotelId/rooms/:roomId}': ['p(95)<500'],
  },
};

export function setup() {
  assertTargetSafety();
  const city = __ENV.CITY || 'Mumbai';
  const dates = stayDates();
  const searchResponse = http.get(`${BASE_URL}${searchPath(city, dates.checkInDate, dates.checkOutDate)}`, {
    tags: { name: 'setup search' },
  });
  const searchBody = jsonOrNull(searchResponse);
  const hotels = searchBody && Array.isArray(searchBody.content) ? searchBody.content : [];
  if (searchResponse.status !== 200 || hotels.length === 0) {
    throw new Error(`No load-test hotel found for ${city} and ${dates.checkInDate}..${dates.checkOutDate}`);
  }

  const candidates = [];
  for (const hotel of hotels.slice(0, 5)) {
    const roomsPath = `/roomly/api/v1/hotels/${hotel.id}?checkInDate=${dates.checkInDate}&checkOutDate=${dates.checkOutDate}&numberOfRooms=1`;
    const roomsResponse = http.get(`${BASE_URL}${roomsPath}`, { tags: { name: 'setup rooms' } });
    const rooms = jsonOrNull(roomsResponse);
    if (roomsResponse.status === 200 && Array.isArray(rooms) && rooms.length > 0) {
      candidates.push({ hotelId: hotel.id, roomIds: rooms.map((room) => room.id) });
    }
  }

  if (candidates.length === 0) throw new Error('Search returned hotels but no available rooms');
  return { city, dates, candidates };
}

export default function (data) {
  const roll = Math.random();
  const candidate = data.candidates[Math.floor(Math.random() * data.candidates.length)];

  if (roll < 0.60) {
    const response = http.get(`${BASE_URL}${searchPath(data.city, data.dates.checkInDate, data.dates.checkOutDate)}`, {
      tags: { name: 'GET /hotels/search', operation: 'search' },
    });
    check(response, { 'search is successful': (res) => res.status === 200 });
    return;
  }

  if (roll < 0.85) {
    const path = `/roomly/api/v1/hotels/${candidate.hotelId}?checkInDate=${data.dates.checkInDate}&checkOutDate=${data.dates.checkOutDate}&numberOfRooms=1`;
    const response = http.get(`${BASE_URL}${path}`, {
      tags: { name: 'GET /hotels/:id', operation: 'available_rooms' },
    });
    check(response, { 'available rooms is successful': (res) => res.status === 200 });
    return;
  }

  const roomId = candidate.roomIds[Math.floor(Math.random() * candidate.roomIds.length)];
  const response = http.get(`${BASE_URL}/roomly/api/v1/hotels/${candidate.hotelId}/rooms/${roomId}`, {
    tags: { name: 'GET /hotels/:hotelId/rooms/:roomId', operation: 'room_detail' },
  });
  check(response, { 'room detail is successful': (res) => res.status === 200 });
}
