# Roomly - Hotel Management and Booking System

Roomly is a full-stack hotel booking and management platform built as a student/portfolio project. It models a real hotel marketplace with guest search, dynamic room pricing, concurrency-safe booking, role-based manager operations, JWT security, exception handling, and Razorpay payment verification.

The project is intentionally bigger than a CRUD demo. It includes guest workflows, hotel manager workflows, protected APIs, booking ownership checks, pessimistic inventory locking, dynamic pricing strategies, and a React frontend that changes behavior based on the logged-in user's role.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [User Roles and Permissions](#user-roles-and-permissions)
- [Booking and Payment Flow](#booking-and-payment-flow)
- [Database Schema](#database-schema)
- [API Routes](#api-routes)
- [Frontend Behavior](#frontend-behavior)
- [Project Structure](#project-structure)
- [Local Setup](#local-setup)
- [Environment Configuration](#environment-configuration)
- [Razorpay Test Setup](#razorpay-test-setup)
- [Common Troubleshooting](#common-troubleshooting)
- [Deployment Notes](#deployment-notes)
- [Portfolio Talking Points](#portfolio-talking-points)

## Features

### Guest Features

- Public hotel search by city, check-in date, check-out date, and room count.
- Hotel detail page with available rooms for selected dates.
- Dynamic room prices based on pricing strategies.
- Guest signup and login.
- JWT-based protected booking APIs.
- Create bookings as authenticated guest users.
- Add guest details to a booking.
- View only the current user's bookings.
- Cancel only the current user's bookings.
- Razorpay Checkout payment flow.
- Server-side Razorpay signature verification before confirming payment.

### Hotel Manager / Admin Features

- Role-gated manager dashboard in the frontend.
- Create, view, update, and delete hotels.
- Create, view, update, and delete rooms.
- Patch inventory by hotel, room, and date.
- View bookings for managed hotels with date/status filters.
- Generate booking reports.
- Manager/admin users cannot create guest bookings from the frontend.

### Security Features

- Spring Security with JWT access and refresh tokens.
- Password hashing with BCrypt.
- Role and permission based API protection with `@PreAuthorize`.
- Public signup always creates a `USER` account.
- Manager/admin routes are hidden from guest users in the frontend.
- Guest booking routes are hidden from manager/admin users in the frontend.
- Service-layer booking ownership checks for:
  - View booking
  - Add guests
  - Cancel booking
  - Create payment order
  - Verify payment
- Global exception handling for domain and security exceptions.
- JWT filter clears invalid security context and routes security errors through exception handlers.

### Booking and Inventory Features

- Per-day inventory records for each hotel room.
- Availability search checks every date in the requested stay.
- Pessimistic locking prevents double booking during reservation.
- Booking cancellation releases inventory.
- Room inventory can be closed for specific dates.
- Booking statuses:
  - `PENDING`
  - `RESERVED`
  - `GUESTS_ADDED`
  - `CONFIRMED`
  - `CANCELLED`
  - `CHECKED_IN`
  - `CHECKED_OUT`
  - `EXPIRED`

### Dynamic Pricing Features

Roomly calculates final booking price using a strategy/decorator style pipeline:

- Base room price
- Surge factor
- Occupancy based adjustment
- Urgency based adjustment
- Holiday based adjustment using Google Calendar holiday API

The pricing chain is assembled in `PricingService`.

### Payment Features

- Razorpay Java SDK integration.
- Backend creates Razorpay order using trusted server-side booking amount.
- Frontend opens Razorpay Checkout.
- Frontend sends Razorpay payment id, order id, and signature back to backend.
- Backend verifies signature using Razorpay secret.
- Booking is marked `CONFIRMED` only after successful verification.
- Payment statuses:
  - `PENDING`
  - `SUCCESS`
  - `FAILED`
  - `REFUNDED`

## Tech Stack

### Backend

| Area | Technology |
| --- | --- |
| Language | Java |
| Framework | Spring Boot 4 |
| Web | Spring Web MVC |
| Security | Spring Security |
| Auth | JWT using JJWT |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Mapping | ModelMapper |
| Payments | Razorpay Java SDK |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |

### Frontend

| Area | Technology |
| --- | --- |
| Framework | React 19 |
| Build Tool | Vite |
| Styling | CSS |
| Icons | lucide-react |
| Auth Storage | localStorage access token |
| Payment UI | Razorpay Checkout.js |

## Architecture

Roomly uses a layered architecture:

```text
Frontend React App
        |
        v
Spring MVC Controllers
        |
        v
Services / Business Logic
        |
        v
Repositories
        |
        v
PostgreSQL Database
```

Important backend layers:

- `controllers`: REST endpoints for guest, manager, auth, and payment APIs.
- `services`: booking, hotel, room, inventory, pricing, payment, auth logic.
- `repositories`: Spring Data JPA access layer.
- `entities`: JPA models.
- `DTOs`: request/response payloads.
- `security`: JWT filter, user model, role/permission mapping, auth services.
- `advices`: global exception handlers.
- `strategies`: dynamic pricing strategies.

## User Roles and Permissions

Roles are defined in `Role.java`:

```text
USER
HOTEL_MANAGER
HOTEL_ADMIN
ROOMLY_ADMIN
```

Permissions are defined in `Permissions.java`:

```text
HOTEL_CREATE, HOTEL_DELETE, HOTEL_UPDATE, HOTEL_VIEW
ROOM_CREATE, ROOM_UPDATE, ROOM_VIEW, ROOM_DELETE
BOOKING_VIEW, BOOKING_CREATE, BOOKING_DELETE, BOOKING_UPDATE, BOOKING_CANCEL
INVENTORY_UPDATE, INVENTORY_VIEW, INVENTORY_DELETE, INVENTORY_CREATE
```

Role mapping:

| Role | Capabilities |
| --- | --- |
| `USER` | Search hotels/rooms, create bookings, view own bookings, add guests, cancel own bookings, pay for own bookings |
| `HOTEL_MANAGER` | View hotels/rooms, manage rooms, view/update/cancel bookings, view/update inventory |
| `HOTEL_ADMIN` | Manage hotels, manage rooms, manage bookings, create/view/update/delete inventory |
| `ROOMLY_ADMIN` | All permissions |

## Booking and Payment Flow

### Booking Flow

```text
Guest searches hotels
        |
Guest selects hotel and room
        |
Backend locks inventory rows for requested dates
        |
Backend verifies full date range availability
        |
Dynamic pricing is calculated
        |
Inventory booked count is incremented
        |
Booking is created as RESERVED
```

### Payment Flow

```text
Frontend creates booking
        |
Frontend calls POST /roomly/api/v1/payments/orders
        |
Backend checks booking ownership
        |
Backend creates Razorpay order using booking.finalCalculatedPrice
        |
Frontend opens Razorpay Checkout
        |
Razorpay returns payment_id, order_id, signature
        |
Frontend calls POST /roomly/api/v1/payments/verify
        |
Backend verifies Razorpay signature
        |
Payment becomes SUCCESS
        |
Booking becomes CONFIRMED
```

The backend never trusts the frontend amount. The amount is taken from the server-side booking record.

## Database Schema

### Main Tables

#### `users`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `username` | Display name |
| `email` | Unique login identifier |
| `password` | BCrypt hashed password |

Related tables:

- User roles are stored through an element collection.
- One user can have many bookings.
- One user can have many guest profiles.

#### `hotels`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `city` | Hotel city |
| `hotel_name` | Hotel display name |
| `contact_info_id` | One-to-one contact info |
| `active` | Hotel active/inactive flag |
| `created_at` | Creation timestamp |
| `updated_at` | Update timestamp |

Related tables:

- `hotel_photos`
- `hotel_amenities`
- One hotel has many rooms.
- One hotel has many bookings.
- One hotel has many inventory records.

#### `contact_info`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `complete_address` | Full address |
| `location` | Area/locality |
| `email` | Contact email |
| `phone_number` | Contact phone |

#### `rooms`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `hotel_id` | Many-to-one hotel |
| `type` | Room type |
| `base_price` | Base nightly price |
| `total_count` | Total rooms of this type |
| `capacity` | Guest capacity |
| `created_at` | Creation timestamp |
| `updated_at` | Update timestamp |

Related tables:

- `room_photos`
- `room_amenities`
- One room has many bookings.
- One room has many inventory records.

#### `inventory`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `hotel_id` | Hotel reference |
| `room_id` | Room reference |
| `date` | Inventory date |
| `booked_count` | Rooms booked for that date |
| `total_count` | Available room count for that date |
| `surge_factor` | Pricing multiplier |
| `closed` | Whether booking is closed for date |
| `created_at` | Creation timestamp |
| `updated_at` | Update timestamp |

Unique constraint:

```text
hotel_id + room_id + date
```

#### `bookings`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `hotel_id` | Hotel reference |
| `room_id` | Room reference |
| `user_id` | Booking owner |
| `final_calculated_price` | Server-side computed price |
| `status` | Booking lifecycle status |
| `check_in_date` | Check-in date |
| `checkout_date` | Check-out date |
| `rooms_count` | Number of rooms booked |
| `created_at` | Creation timestamp |
| `updated_at` | Update timestamp |

Related tables:

- Many-to-many relationship with `guests`.

#### `guests`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `user_id` | Owner user |
| `name` | Guest name |
| `gender` | Guest gender |
| `created_at` | Creation timestamp |

#### `payments`

| Column | Notes |
| --- | --- |
| `id` | Primary key |
| `booking_id` | Unique one-to-one booking reference |
| `transaction_id` | Razorpay payment/order transaction id |
| `price` | Amount charged |
| `currency` | Example: `INR` |
| `status` | Payment status |
| `razorpay_order_id` | Razorpay order id |
| `razorpay_payment_id` | Razorpay payment id |
| `razorpay_signature` | Razorpay verification signature |
| `created_at` | Creation timestamp |
| `updated_at` | Update timestamp |

## API Routes

Base backend URL during local development:

```text
http://localhost:8081
```

### Auth Routes

Base path:

```text
/roomly/api/v1
```

| Method | Route | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/signup` | Public | Create guest user account |
| `POST` | `/login` | Public | Login and receive JWT tokens |
| `POST` | `/refresh` | Public/cookie based | Refresh access token |

Signup request:

```json
{
  "username": "aamir",
  "email": "aamir@example.com",
  "password": "password123"
}
```

Login request:

```json
{
  "email": "aamir@example.com",
  "password": "password123"
}
```

Login response:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

### Guest Search Routes

Base path:

```text
/roomly/api/v1/hotels
```

| Method | Route | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/search` | Public | Search available hotels |
| `GET` | `/{hotelId}` | Public | Get available rooms for a hotel/date range |
| `GET` | `/{hotelId}/rooms/{roomId}` | Public | Get room details |

Hotel search query example:

```text
GET /roomly/api/v1/hotels/search?city=Mumbai&checkInDate=2026-05-20&checkOutDate=2026-05-23&numberOfRooms=1&pageNumber=0&pageSize=12
```

Hotel rooms query example:

```text
GET /roomly/api/v1/hotels/1?checkInDate=2026-05-20&checkOutDate=2026-05-23&numberOfRooms=1
```

### Guest Booking Routes

Base path:

```text
/roomly/api/v1
```

All routes require a valid bearer token.

| Method | Route | Permission | Description |
| --- | --- | --- | --- |
| `GET` | `/bookings` | `BOOKING_VIEW` | Get current user's bookings |
| `GET` | `/booking/{bookingId}` | `BOOKING_VIEW` | Get booking by id if owner |
| `POST` | `/bookings` | `BOOKING_CREATE` | Create booking |
| `PATCH` | `/bookings/{bookingId}/guests` | `BOOKING_UPDATE` | Add guests to booking |
| `PATCH` | `/bookings/{bookingId}/cancel` | `BOOKING_CANCEL` | Cancel booking |

Create booking request:

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

Add guests request:

```json
[
  {
    "name": "Aamir",
    "gender": "MALE"
  }
]
```

### Payment Routes

Base path:

```text
/roomly/api/v1/payments
```

All routes require a valid bearer token with booking permissions.

| Method | Route | Permission | Description |
| --- | --- | --- | --- |
| `POST` | `/orders` | `BOOKING_CREATE` | Create Razorpay order for booking |
| `POST` | `/verify` | `BOOKING_CREATE` | Verify Razorpay payment signature |

Create payment order request:

```json
{
  "bookingId": 12
}
```

Create payment order response:

```json
{
  "bookingId": 12,
  "paymentId": 4,
  "razorpayOrderId": "order_...",
  "razorpayKeyId": "rzp_test_...",
  "amount": 184000,
  "amountInPaise": 18400000,
  "currency": "INR",
  "status": "PENDING"
}
```

Verify payment request:

```json
{
  "bookingId": 12,
  "razorpayOrderId": "order_...",
  "razorpayPaymentId": "pay_...",
  "razorpaySignature": "..."
}
```

### Manager/Admin Routes

Base path:

```text
/api/v1/admin
/api/v1/admins
```

Both base paths are supported by the controller.

| Method | Route | Permission/Role | Description |
| --- | --- | --- | --- |
| `GET` | `/roomly/hotels` | `HOTEL_VIEW` | List hotels |
| `POST` | `/hotels` | `HOTEL_CREATE` | Create hotel |
| `GET` | `/hotels/{hotelId}` | `HOTEL_VIEW` | Get hotel |
| `PATCH` | `/hotels/{hotelId}` | `HOTEL_UPDATE` | Update hotel |
| `DELETE` | `/hotels/{hotelId}` | `HOTEL_DELETE` | Delete hotel |
| `GET` | `/hotels/{hotelId}/rooms` | `ROOM_VIEW` | List rooms |
| `POST` | `/hotels/{hotelId}/rooms` | `ROOM_CREATE` | Create room |
| `GET` | `/hotels/{hotelId}/rooms/{roomId}` | `ROOM_VIEW` | Get room |
| `PATCH` | `/hotels/{hotelId}/rooms/{roomId}` | `ROOM_UPDATE` | Update room |
| `DELETE` | `/hotels/{hotelId}/rooms/{roomId}` | `ROOM_DELETE` | Delete room |
| `GET` | `/bookings` | Manager/Admin role | Manager booking ledger |
| `POST` | `/reports` | Manager/Admin role | Generate booking report |
| `PATCH` | `/inventory/{hotelId}/{roomId}/{date}` | Manager/Admin role | Patch inventory |

Create hotel request:

```json
{
  "hotelName": "The Ritz Roomly",
  "city": "Mumbai",
  "contactInfo": {
    "completeAddress": "Marine Drive, Mumbai",
    "location": "Marine Drive",
    "email": "ritz@example.com",
    "phoneNumber": "9999999999"
  },
  "photos": ["https://example.com/hotel.jpg"],
  "amenities": ["Spa", "Pool", "Breakfast"]
}
```

Create room request:

```json
{
  "type": "Deluxe Suite",
  "basePrice": 12000,
  "totalCount": 10,
  "capacity": 2,
  "amenities": ["WiFi", "Sea View"],
  "photos": ["https://example.com/room.jpg"]
}
```

Patch inventory request:

```json
{
  "totalCount": 10,
  "bookedCount": 2,
  "surgeFactor": 1.25,
  "closed": false
}
```

## Frontend Behavior

The frontend is a Vite React app located in `frontend`.

Role-based behavior:

- Logged out users see public hotel browsing only.
- `USER` accounts see booking features and `My Bookings`.
- `HOTEL_MANAGER`, `HOTEL_ADMIN`, and `ROOMLY_ADMIN` see the manager console.
- Manager/admin accounts cannot create guest bookings in the frontend.
- Signup creates guest accounts only.
- Manager accounts are expected to be provisioned by an admin/backend process.

Payment behavior:

- Checkout button loads Razorpay Checkout.
- Backend creates a Razorpay order.
- Razorpay popup handles the payment UI.
- Backend verifies the returned signature.
- Booking is shown as confirmed only after backend verification.

## Project Structure

```text
AirBnb-Hotel-Management-and-Booking-System/
  pom.xml
  mvnw
  mvnw.cmd
  src/
    main/
      java/
        com/code/airbnb/app/airbnbhotelmanagementandbookingsystem/
          advices/
          configurations/
          controllers/
          DTOs/
          entities/
          exceptions/
          repositories/
          security/
          services/
          strategies/
      resources/
        application.properties
  frontend/
    package.json
    src/
      main.jsx
      styles.css
```

## Local Setup

### Prerequisites

- JDK 25, because `pom.xml` currently sets:

```xml
<java.version>25</java.version>
```

- Maven or a working Maven wrapper.
- PostgreSQL.
- Node.js and npm.
- Razorpay test account for payment testing.
- Google Calendar API key if holiday pricing is enabled.

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd AirBnb-Hotel-Management-and-Booking-System
```

### 2. Create PostgreSQL Database

Create a database named `Roomly` or update the datasource URL in `application.properties`.

Example:

```sql
CREATE DATABASE "Roomly";
```

### 3. Configure Backend

Create or update:

```text
src/main/resources/application.properties
```

Sample local config:

```properties
spring.application.name=AirBnb-Hotel-Management-and-Booking-System

spring.datasource.url=jdbc:postgresql://localhost:5432/Roomly
spring.datasource.username=postgres
spring.datasource.password=your_postgres_password
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

server.port=8081

google.calender.holiday.api.key=your_google_calendar_api_key
google.calender.holiday.base.url=https://www.googleapis.com/calendar/v3/calendars
google.calender.holiday.base.calender.id=holiday@group.v.calendar.google.com
google.calender.holiday.base.region=en.indian
google.calender.api=${google.calender.holiday.base.url}/${google.calender.holiday.base.region}%23${google.calender.holiday.base.calender.id}/events?key=${google.calender.holiday.api.key}

JWT_SECRET_KEY=replace_with_a_long_secret_key

razorpay.key-id=rzp_test_your_key_id
razorpay.key-secret=your_razorpay_test_secret
razorpay.currency=INR
```

If you prefer environment variables:

```properties
razorpay.key-id=${RAZORPAY_KEY_ID}
razorpay.key-secret=${RAZORPAY_KEY_SECRET}
razorpay.currency=${RAZORPAY_CURRENCY:INR}
```

### 4. Run Backend

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Using installed Maven:

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8081
```

### 5. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend usually runs on:

```text
http://127.0.0.1:5173
```

If port `5173` is busy, Vite may use `5174`.

### 6. Optional Frontend Environment

Create:

```text
frontend/.env
```

Example:

```properties
VITE_API_BASE_URL=http://localhost:8081
```

If omitted, the frontend defaults to:

```text
http://localhost:8081
```

## Environment Configuration

### Backend Environment Variables

These values can be kept in a local, gitignored `application.properties` file during development. For deployment, set them through the hosting provider's environment variable panel.

| Variable / Property | Required | Purpose |
| --- | --- | --- |
| `spring.datasource.url` | Yes | PostgreSQL connection URL |
| `spring.datasource.username` | Yes | PostgreSQL username |
| `spring.datasource.password` | Yes | PostgreSQL password |
| `JWT_SECRET_KEY` | Yes | Signs and verifies JWT tokens |
| `google.calender.holiday.api.key` | Yes for holiday pricing | Google Calendar API key |
| `razorpay.key-id` or `RAZORPAY_KEY_ID` | Yes for payments | Razorpay public key id |
| `razorpay.key-secret` or `RAZORPAY_KEY_SECRET` | Yes for payments | Razorpay secret key, backend only |
| `razorpay.currency` or `RAZORPAY_CURRENCY` | Optional | Defaults to `INR` |

### Frontend Environment Variables

| Variable | Required | Purpose |
| --- | --- | --- |
| `VITE_API_BASE_URL` | Optional | Backend base URL. Defaults to `http://localhost:8081` |

Example:

```properties
VITE_API_BASE_URL=http://localhost:8081
```

## Razorpay Test Setup

1. Create a Razorpay test account.
2. Copy test key id and key secret.
3. Add them to backend `application.properties`.
4. Restart backend.
5. Login as a `USER`.
6. Create a booking.
7. Click `Pay with Razorpay`.
8. Use Razorpay test payment details from the Razorpay dashboard/docs.

Important:

- The frontend receives only the Razorpay key id.
- The key secret stays on the backend.
- Backend verifies the Razorpay signature before confirming the booking.

## Common Troubleshooting

### `bookingService is null`

Cause is usually stale compiled code or Lombok annotation processing not running.

Fix:

1. Stop backend.
2. Kill any Java process still running on port `8081`.
3. Restart IDE.
4. Enable annotation processing.
5. Delete `target`.
6. Rebuild project.
7. Run backend again.

In IntelliJ:

```text
Settings > Build, Execution, Deployment > Compiler > Annotation Processors
```

Enable annotation processing.

### Maven Wrapper Fails

If `mvnw.cmd` fails with wrapper errors:

1. Install Maven manually and add it to PATH.
2. Or regenerate/repair the Maven wrapper.
3. Or run from IntelliJ after a full rebuild.

Check Maven:

```bash
mvn -v
```

### Razorpay Says Payment Failed

Check where it fails:

- If popup does not open, backend order creation likely failed.
- If popup opens but payment fails, use Razorpay test payment details.
- If payment succeeds but booking is not confirmed, signature verification likely failed.

Check backend logs for:

```text
Razorpay order creation failed
Razorpay signature verification failed
Unhandled application exception
```

### Frontend Cannot Reach Backend

Confirm backend is running:

```text
http://localhost:8081
```

Confirm frontend API base:

```properties
VITE_API_BASE_URL=http://localhost:8081
```

Confirm CORS allows:

```text
http://127.0.0.1:5173
http://127.0.0.1:5174
http://localhost:5173
http://localhost:5174
```

### Signup/Login Issues

- Signup creates `USER` accounts only.
- Login uses email and password.
- JWT access token is stored in localStorage.
- Manager/admin accounts must be created or promoted outside public signup.

## Deployment Notes

Before deploying:

- Move production secrets to environment variables.
- Do not commit production `application.properties`.
- Use a hosted PostgreSQL database.
- Set `spring.jpa.hibernate.ddl-auto` carefully:
  - `update` is convenient for development.
  - migrations are better for production.
- Configure frontend `VITE_API_BASE_URL` to deployed backend URL.
- Add deployed frontend URL to CORS allowed origins.
- Use HTTPS for frontend and backend.
- Rotate any secret that was ever committed or shared publicly.

Possible deployment split:

- Frontend: Vercel, Netlify, Render Static Site
- Backend: Render, Railway, Fly.io, AWS, Azure
- Database: Neon, Supabase Postgres, Railway Postgres, Render Postgres

## Portfolio Talking Points

Roomly demonstrates:

- Full-stack React + Spring Boot development.
- JWT authentication and role-based authorization.
- Real payment gateway integration with server-side verification.
- Concurrency-safe booking using pessimistic locks.
- Dynamic pricing using strategy/decorator style composition.
- Clean service-layer ownership checks beyond controller-level security.
- Centralized exception handling.
- Realistic hotel manager and guest workflows.
- PostgreSQL relational schema design.
- Production-aware concerns like CORS, secret management, and deployment configuration.

## Current Status

Implemented:

- Backend hotel, room, inventory, booking, security, exception, and payment flows.
- Frontend guest and manager experiences.
- Razorpay backend and frontend integration.
- Role-aware frontend navigation and route gating.
- Booking ownership checks.
- Frontend signup/login state separation.

Still recommended before final deployment:

- Fix Maven wrapper or install Maven.
- Run full backend compile/test locally.
- Complete one end-to-end Razorpay test payment.
- Add seed/admin creation workflow or document how manager/admin users are created.
- Add automated tests for booking ownership, payment verification, and inventory locking.
