# Roomly - Hotel Booking and Management System

Roomly is a full-stack hotel booking platform built with Spring Boot, React, PostgreSQL, JWT security, dynamic pricing, manager/admin tools, and Razorpay test payments.

It started as an Airbnb-style hotel booking backend and grew into a complete guest + hotel manager system with real booking rules, inventory locking, role-based access, and a polished frontend.

## Features

### Guest

- Search hotels by city, dates, and number of rooms.
- View available rooms for a hotel.
- Dynamic room pricing based on inventory/date conditions.
- Signup/login with JWT authentication.
- Create bookings as a logged-in guest.
- View only your own bookings.
- Add guests to your own booking.
- Cancel your own booking.
- Pay through Razorpay Checkout.
- Booking becomes `CONFIRMED` only after backend verifies Razorpay signature.

### Hotel Manager / Admin

- Role-aware manager dashboard.
- Create, view, update, and delete hotels.
- Create, view, update, and delete rooms.
- Update inventory for a room/date.
- View bookings for a hotel.
- Filter bookings by date/status.
- Generate booking reports.
- Manager/admin accounts cannot book rooms as guests from the frontend.

### Security

- Spring Security + JWT access/refresh tokens.
- BCrypt password hashing.
- Role and permission based authorization.
- Public signup creates only `USER` accounts.
- Manager pages are hidden from guest users.
- Guest booking pages are hidden from manager/admin users.
- Service-layer ownership checks prevent users from accessing other users' bookings.
- Global exception handlers for custom, validation, security, and generic errors.

### Payments

- Razorpay Java SDK on backend.
- Razorpay Checkout on frontend.
- Backend creates Razorpay orders using server-side booking amount.
- Frontend never controls the payment amount.
- Backend verifies:
  - `razorpay_order_id`
  - `razorpay_payment_id`
  - `razorpay_signature`
- Payment success updates:
  - `Payment.status = SUCCESS`
  - `Booking.status = CONFIRMED`

## Tech Stack

| Layer | Tech |
| --- | --- |
| Frontend | React, Vite, CSS, lucide-react |
| Backend | Java, Spring Boot, Spring MVC |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL, Spring Data JPA, Hibernate |
| Payments | Razorpay |
| Validation | Jakarta Bean Validation |
| Mapping | ModelMapper |
| Build | Maven, npm |

## Important Backend Concepts

- **Inventory locking:** booking uses pessimistic locking to avoid double booking.
- **Dynamic pricing:** pricing is built with chained strategies:
  - base price
  - surge factor
  - occupancy adjustment
  - urgency adjustment
  - holiday adjustment
- **Ownership checks:** controllers are secured, but services still verify booking ownership.
- **Role permissions:** roles map to authorities through `PermissionMapping`.
- **Payment verification:** Razorpay success is trusted only after backend signature verification.

## Roles

| Role | Access |
| --- | --- |
| `USER` | Search, book, pay, view own bookings, add guests, cancel own bookings |
| `HOTEL_MANAGER` | View/manage rooms, inventory, and hotel bookings |
| `HOTEL_ADMIN` | Manage hotels, rooms, inventory, and reports |
| `ROOMLY_ADMIN` | All permissions |

## Main API Routes

Backend local URL:

```text
http://localhost:8081
```

### Auth

| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/roomly/api/v1/signup` | Create guest account |
| `POST` | `/roomly/api/v1/login` | Login and get tokens |
| `POST` | `/roomly/api/v1/refresh` | Refresh access token |

### Guest Search

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/roomly/api/v1/hotels/search` | Search available hotels |
| `GET` | `/roomly/api/v1/hotels/{hotelId}` | Get available rooms |
| `GET` | `/roomly/api/v1/hotels/{hotelId}/rooms/{roomId}` | Get room details |

Example:

```text
/roomly/api/v1/hotels/search?city=Mumbai&checkInDate=2026-05-20&checkOutDate=2026-05-23&numberOfRooms=1&pageNumber=0&pageSize=12
```

### Guest Booking

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/roomly/api/v1/bookings` | Current user's bookings |
| `GET` | `/roomly/api/v1/booking/{bookingId}` | Current user's booking by id |
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
| `POST` | `/roomly/api/v1/payments/verify` | Verify payment signature |

Create order:

```json
{
  "bookingId": 12
}
```

Verify payment:

```json
{
  "bookingId": 12,
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "signature_xxx"
}
```

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
| `GET` | `/bookings` | Manager booking ledger |
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
- One booking has one payment.
- Inventory is unique per `hotel + room + date`.

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
├── src/main/resources
├── frontend
│   ├── src
│   ├── package.json
│   └── index.html
├── pom.xml
└── README.md
```

## Running Locally

### Requirements

- Java 25
- Maven or Maven wrapper
- PostgreSQL
- Node.js + npm
- Razorpay test account

### Backend setup

Create `src/main/resources/application.properties`:

```properties
spring.application.name=AirBnb-Hotel-Management-and-Booking-System

spring.datasource.url=jdbc:postgresql://localhost:5432/Roomly
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8081

JWT_SECRET_KEY=replace_with_long_secret

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

If wrapper fails, run from IntelliJ or install Maven and use:

```bash
mvn spring-boot:run
```

### Frontend setup

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://127.0.0.1:5173
```

If port `5173` is busy, Vite may use `5174`.

Optional `frontend/.env`:

```properties
VITE_API_BASE_URL=http://localhost:8081
```

## Notes

- `application.properties`, `target`, `frontend/node_modules`, and `frontend/dist` are ignored by Git.
- Use Razorpay test cards/details while testing payments.
- Manager/admin accounts are not created through public signup.
- Controllers should keep request handler methods `public`, especially with Spring method security.
- If dependency injection acts weird after hot reload, stop the Java process on port `8081` and run the backend fresh.

## Current Status

Done:

- Auth and roles
- Guest booking flow
- Manager dashboard
- Inventory locking
- Dynamic pricing
- Exception handling
- Booking ownership checks
- Razorpay backend + frontend integration
- Role-aware frontend
- README and `.gitignore`

Still worth doing:

- Add seed/dummy data.
- Run one complete Razorpay test payment.
- Add automated tests for booking/payment/security.
- Add a clear admin user creation flow.

