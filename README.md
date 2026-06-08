<img width="881" height="673" alt="Roomly-Favicon" src="https://github.com/user-attachments/assets/efbbafc5-aa97-4209-8499-ebeb33000428" />

# Disclaimer
Due to the web app being deployed on **Render - Free Tier** "Cold Starts" are expected and the web app may take upto 2 minutes to get connected to the backend.

# Roomly 🏨

Roomly is a full-stack hotel booking and hotel-management platform built as a student/portfolio project. It combines a Spring Boot backend, React frontend, PostgreSQL database, JWT security, live inventory search, dynamic pricing, Razorpay test payments, and role-aware hotel manager tools.

The project started as an Airbnb-style booking backend and grew into a complete guest + manager workflow with real booking rules, ownership checks, inventory locking, deployment, and production-style error handling.

## Recruiter Highlights ✨

- 🔐 **Spring Security + JWT** with role/permission based access control.
- 🧾 **Razorpay test payment integration** with backend order creation and signature verification.
- 📅 **Google Calendar Holiday API** used by the pricing engine for holiday-aware price adjustments.
- ⚙️ **Dynamic pricing engine** using strategy classes: base price, surge, occupancy, urgency, and holiday pricing.
- 🧵 **Concurrency control** with pessimistic locking during booking to reduce double-booking risk.
- 🏨 **Hotel manager/admin dashboard** for hotels, rooms, inventory, bookings, and reports.
- 👤 **User ownership checks** at the service layer so users cannot act on another user's booking by guessing IDs.
- 🧯 **Global exception handling** for custom domain errors, validation errors, security errors, and generic failures.
- 🚀 **Deployed full-stack app** using Vercel frontend, Railway backend, and Neon PostgreSQL.

## Live Demo 🌐

- Frontend: `https://roomly-xi.vercel.app/`
- Backend: `https://roomly-production-dba9.up.railway.app/`

Note: this is a portfolio deployment using demo hotels and test payment credentials. Some data is seeded and may not represent real hotels.

## Features

### Guest Experience

- Search hotels by city, check-in date, check-out date, and number of rooms.
- Browse live hotel availability with pagination and frontend caching.
- View room options for a selected hotel.
- Signup/login as a guest.
- Create bookings only as an authenticated guest.
- Pay with Razorpay Checkout in test mode.
- View personal bookings grouped into Upcoming, Past, and Cancelled.
- Cancel eligible bookings through a custom confirmation modal.
- Add guests to bookings.
- Guests cannot access manager/admin tools.

### Manager/Admin Experience

- Role-aware dashboard for hotel managers/admins.
- Create, update, and delete hotels.
- Create, update, and delete rooms.
- Update room inventory for specific dates.
- View booking ledger for managed hotels.
- Filter bookings by date and status.
- Generate booking reports.
- Manager/admin accounts are blocked from guest booking flows in the frontend.

### Security

- JWT-based authentication.
- BCrypt password hashing.
- Role and permission mapping through Spring Security authorities.
- Public signup creates normal `USER` accounts only.
- Protected controller endpoints with method security.
- Service-layer booking ownership checks for:
  - viewing a booking
  - adding guests
  - cancelling bookings
  - payment order creation and verification
- Custom security exception responses.

### Payments

- Backend creates Razorpay orders using the server-side booking amount.
- Frontend opens Razorpay Checkout.
- Backend verifies:
  - `razorpay_order_id`
  - `razorpay_payment_id`
  - `razorpay_signature`
- Booking becomes `CONFIRMED` only after backend verification.
- Cancelled/expired/confirmed bookings are guarded against invalid payment attempts.

### Pricing and Inventory

- Inventory is stored per `hotel + room + date`.
- Unique inventory constraint prevents duplicate inventory rows.
- Search checks availability across the full stay range.
- Booking updates inventory counts.
- Cancellation releases inventory back.
- Dynamic price calculation uses chained strategies:
  - base price
  - surge factor
  - occupancy pricing
  - urgency pricing
  - holiday pricing through Google Calendar API

## Tech Stack 🛠️

| Layer | Tech |
| --- | --- |
| Frontend | React, Vite, CSS, lucide-react |
| Backend | Java 21, Spring Boot, Spring MVC |
| Security | Spring Security, JWT/JJWT, BCrypt |
| Database | PostgreSQL, Spring Data JPA, Hibernate |
| Payments | Razorpay Java SDK + Razorpay Checkout |
| External API | Google Calendar Holiday API |
| Validation | Jakarta Bean Validation |
| Mapping | ModelMapper |
| Deployment | Vercel, Railway, Neon PostgreSQL |
| Build Tools | Maven, npm |

## Roles

| Role | Access |
| --- | --- |
| `USER` | Search, book, pay, view own bookings, add guests, cancel own bookings |
| `HOTEL_MANAGER` | Manage hotel operations for assigned/managed hotels |
| `HOTEL_ADMIN` | Hotel, room, inventory, booking, and report operations |
| `ROOMLY_ADMIN` | Highest-level admin permissions |

## Main API Routes

Local backend URL:

```text
http://localhost:8081
```

### Auth

| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/roomly/api/v1/signup` | Create guest account |
| `POST` | `/roomly/api/v1/login` | Login and receive tokens |
| `POST` | `/roomly/api/v1/refresh` | Refresh access token |

### Hotel Search

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/roomly/api/v1/hotels/search` | Search available hotels |
| `GET` | `/roomly/api/v1/hotels/{hotelId}` | Get available rooms for hotel |
| `GET` | `/roomly/api/v1/hotels/{hotelId}/rooms/{roomId}` | Get room details |

Example:

```text
/roomly/api/v1/hotels/search?city=Mumbai&checkInDate=2026-05-20&checkOutDate=2026-05-23&numberOfRooms=1&pageNumber=0&pageSize=9
```

### Guest Booking

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/roomly/api/v1/bookings` | Current user's bookings |
| `GET` | `/roomly/api/v1/booking/{bookingId}` | Current user's booking by ID |
| `POST` | `/roomly/api/v1/bookings` | Create booking |
| `PATCH` | `/roomly/api/v1/bookings/{bookingId}/guests` | Add guests |
| `PATCH` | `/roomly/api/v1/bookings/{bookingId}/cancel` | Cancel booking |

Create booking body:

```json
{
  "roomId": 1,
  "hotelId": 1,
  "city": "Mumbai",
  "checkInDate": "2026-05-20",
  "checkOutDate": "2026-05-23",
  "numberOfRooms": 1
}
```

### Payments

| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/roomly/api/v1/payments/orders` | Create Razorpay order |
| `POST` | `/roomly/api/v1/payments/verify` | Verify Razorpay signature |

### Manager/Admin

Base paths:

```text
/api/v1/admin
/api/v1/admins
```

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/roomly/hotels` | List hotels |
| `POST` | `/hotels` | Create hotel |
| `GET` | `/hotels/{hotelId}` | Get hotel |
| `PATCH` | `/hotels/{hotelId}` | Update hotel |
| `DELETE` | `/hotels/{hotelId}` | Delete hotel |
| `GET` | `/hotels/{hotelId}/rooms` | List rooms |
| `POST` | `/hotels/{hotelId}/rooms` | Create room |
| `GET` | `/hotels/{hotelId}/rooms/{roomId}` | Get room |
| `PATCH` | `/hotels/{hotelId}/rooms/{roomId}` | Update room |
| `DELETE` | `/hotels/{hotelId}/rooms/{roomId}` | Delete room |
| `GET` | `/bookings` | Hotel booking ledger |
| `POST` | `/reports` | Booking report |
| `PATCH` | `/inventory/{hotelId}/{roomId}/{date}` | Update inventory |

## Database Model

Main entities:

- `User`
- `Hotel`
- `ContactInfo`
- `Room`
- `Inventory`
- `Booking`
- `Guest`
- `Payment`

Important relationships:

- One hotel has many rooms.
- One hotel has many inventory records.
- One room has many inventory records.
- One user has many bookings.
- One booking belongs to one user, hotel, and room.
- One booking can have many guests.
- One booking has payment records.
- Inventory is unique for `hotel_id + room_id + date`.

Useful indexes:

- `inventory(date)`
- `inventory(hotel_id, date)`
- `inventory(room_id, date)`
- `hotels(city)`

## Project Structure

```text
.
├── src/main/java/.../airbnbhotelmanagementandbookingsystem
│   ├── controllers
│   ├── services
│   ├── repositories
│   ├── entities
│   ├── DTOs
│   ├── security
│   ├── strategies
│   ├── exceptions
│   └── advices
├── frontend
│   ├── src/components
│   ├── src/data
│   ├── src/lib
│   ├── src/main.jsx
│   └── src/styles.css
├── ARCHITECTURE.md
├── pom.xml
└── README.md
```

## Running Locally

### Requirements

- Java 21
- Maven or Maven wrapper
- PostgreSQL
- Node.js + npm
- Razorpay test account
- Google Calendar API key

### Backend Setup

Create `src/main/resources/application.properties`:

```properties
spring.application.name=AirBnb-Hotel-Management-and-Booking-System

spring.datasource.url=jdbc:postgresql://localhost:5432/roomly
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8081

JWT_SECRET_KEY=replace_with_a_long_secret

razorpay.key-id=rzp_test_your_key
razorpay.key-secret=your_test_secret
razorpay.currency=INR

google.calender.holiday.api.key=your_google_api_key
google.calender.holiday.base.url=https://www.googleapis.com/calendar/v3/calendars
google.calender.holiday.base.calender.id=holiday@group.v.calendar.google.com
google.calender.holiday.base.region=en.indian
google.calender.api=${google.calender.holiday.base.url}/${google.calender.holiday.base.region}%23${google.calender.holiday.base.calender.id}/events?key=${google.calender.holiday.api.key}
```

Run backend:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Optional `frontend/.env`:

```properties
VITE_API_BASE_URL=http://localhost:8081
```

Frontend usually runs at:

```text
http://127.0.0.1:5173
```

## Deployment Notes 🚀

- Frontend can be deployed on Vercel.
- Backend can be deployed on Railway.
- PostgreSQL can be hosted on Neon.
- On Railway, use environment variables instead of committing `application.properties`.
- Set `SERVER_PORT=${{PORT}}` or use Railway's expected port behavior.
- Add the deployed frontend URL to CORS config/env.
- Use `JAVA_TOOL_OPTIONS=-Xms128m -Xmx512m -XX:+UseSerialGC` on small containers if memory is tight.

## Things to Keep in Mind 📝

- This is a student/portfolio project, not a real hotel marketplace.
- Hotels, rooms, images, users, and inventory can be demo/seed data.
- Razorpay is configured for test mode.
- Public signup creates guest users only; manager/admin roles should be assigned manually.
- `application.properties`, secrets, `target`, `frontend/node_modules`, and `frontend/dist` should stay out of Git.
- Some UI flows are intentionally portfolio-friendly and may need extra production hardening for a real business.
- The backend has stronger business logic than the frontend; the frontend is polished but still intentionally lightweight.

## Current Status

Roomly is deployed and functional with:

- live hotel search
- paginated hotel browsing
- booking creation
- Razorpay test checkout
- payment verification
- booking cancellation
- role-aware frontend
- admin/manager dashboard
- Neon PostgreSQL data
- Railway backend
- Vercel frontend

Built with a lot of debugging, iteration, and stubbornness. 🙂
