# Roomly Architecture

Roomly is built as a layered full-stack application:

```text
React Frontend
    |
REST API Controllers
    |
Service Layer
    |
Spring Data JPA Repositories
    |
PostgreSQL
```

The backend follows a classic controller-service-repository architecture. Controllers handle HTTP and authorization annotations, services contain business rules, repositories handle persistence, and entities model the relational database.

## Backend Architecture

### Layers

| Layer | Responsibility |
| --- | --- |
| Controllers | HTTP routes, request validation, role/permission annotations |
| Services | Booking rules, pricing, ownership checks, payment verification |
| Repositories | JPA queries, locking, database access |
| Entities | Database schema and relationships |
| DTOs | Request/response payloads |
| Security | JWT filter, user auth, roles, permissions |
| Advices | Global exception handling |
| Strategies | Dynamic pricing rules |

### Main Services

- `BookingService`: creates bookings, checks ownership, adds guests, cancels bookings, reports.
- `InventoryService`: searches available hotels/rooms and patches inventory.
- `PricingService`: applies pricing strategies.
- `PaymentService`: creates Razorpay orders and verifies signatures.
- `HotelService`: hotel CRUD and safe hotel updates.
- `RoomService`: room CRUD and yearly inventory initialization.
- `UserService` / `LoginService`: signup, login, refresh tokens.

## Security Design

Roomly uses JWT-based stateless authentication.

```text
Login
  -> access token + refresh token
  -> frontend stores access token
  -> JWTAuthFilter validates token
  -> Spring Security sets Authentication
  -> @PreAuthorize checks permissions
```

Roles:

- `USER`
- `HOTEL_MANAGER`
- `HOTEL_ADMIN`
- `ROOMLY_ADMIN`

Important security choices:

- Public signup creates only `USER`.
- Manager/admin pages are hidden on frontend.
- Booking ownership is checked inside services, not only at controller level.
- Payment order creation and verification both check booking ownership.

## Database Design

### Core Tables

```text
users
hotels
contact_info
rooms
inventory
bookings
guests
payments
```

### Relationships

```text
User 1 --- * Booking
User 1 --- * Guest

Hotel 1 --- 1 ContactInfo
Hotel 1 --- * Room
Hotel 1 --- * Inventory
Hotel 1 --- * Booking

Room 1 --- * Inventory
Room 1 --- * Booking

Booking * --- * Guest
Booking 1 --- 1 Payment
```

### Important Constraints

Inventory is unique per hotel, room, and date:

```text
hotel_id + room_id + date
```

Payment is unique per booking:

```text
payments.booking_id UNIQUE
```

Razorpay order and payment ids are also unique.

### Table Notes

#### `inventory`

Inventory is date-based. Each room type has one inventory row per date.

Important columns:

- `hotel_id`
- `room_id`
- `date`
- `booked_count`
- `total_count`
- `surge_factor`
- `closed`

This design makes searching and locking easier because booking a stay means locking multiple date rows.

#### `bookings`

Stores the final calculated price and booking status.

Important columns:

- `user_id`
- `hotel_id`
- `room_id`
- `check_in_date`
- `checkout_date`
- `rooms_count`
- `final_calculated_price`
- `status`

#### `payments`

Stores Razorpay payment state.

Important columns:

- `booking_id`
- `price`
- `currency`
- `status`
- `razorpay_order_id`
- `razorpay_payment_id`
- `razorpay_signature`

## Booking Flow

```text
Guest selects hotel, room, dates, and room count
        |
POST /roomly/api/v1/bookings
        |
BookingService loads hotel and room
        |
InventoryRepository locks matching inventory rows
        |
System verifies every date in the stay is available
        |
PricingService calculates final price per date
        |
booked_count is increased
        |
Booking is saved as RESERVED
```

Why locking matters:

Two users can try to book the same room at the same time. Pessimistic locking makes one transaction wait while the other updates inventory, preventing overbooking.

## Reservation + Payment Flow

```text
Booking is created as RESERVED
        |
Frontend calls /payments/orders
        |
Backend creates Razorpay order using booking price
        |
Frontend opens Razorpay Checkout
        |
Razorpay returns payment_id, order_id, signature
        |
Frontend calls /payments/verify
        |
Backend verifies signature with Razorpay secret
        |
Payment becomes SUCCESS
        |
Booking becomes CONFIRMED
```

The frontend never decides the amount. The backend uses `booking.finalCalculatedPrice`.

## Cancellation Flow

```text
User requests cancellation
        |
BookingService loads booking
        |
Ownership is checked
        |
If already cancelled, reject
        |
Related inventory rows are locked
        |
booked_count is decreased
        |
Booking becomes CANCELLED
```

Refund handling is intentionally left as a future improvement.

## Hotel Manager Flow

```text
Manager logs in
        |
JWT roles are read by frontend
        |
Manager dashboard becomes visible
        |
Manager can manage hotels, rooms, inventory, bookings, reports
```

Manager operations:

- Create/update/delete hotels.
- Add/update/delete rooms.
- Patch room inventory for a date.
- View hotel booking ledger.
- Generate booking reports.

Manager accounts are not created through public signup.

## Pricing Flow

Pricing uses chained strategies:

```text
BasePricing
  -> SurgePricing
  -> OccupancyPricing
  -> UrgencyPricing
  -> HolidayPricing
```

Each strategy wraps the previous price calculation and adds its own adjustment.

This keeps pricing extendable without putting all pricing rules inside `BookingService`.

## Frontend Architecture

The frontend is a Vite React app.

Main responsibilities:

- Public hotel browsing.
- Auth forms.
- Role-aware navigation.
- Guest booking/checkout flow.
- Razorpay Checkout integration.
- Manager dashboard.

Role behavior:

- Logged out: public hotel browsing.
- `USER`: bookings and checkout.
- Manager/admin: manager console.
- Manager/admin cannot book as guests from the same account.

## Error Handling

Backend errors are centralized:

- Domain errors: `GlobalExceptionHandler`
- Security/user errors: `GlobalUserExceptionHandler`
- Validation errors return readable messages.
- Generic errors return safe responses and log details.

Frontend reads backend error messages and shows them in toast notifications.

## Why This Architecture Works

- Controllers stay thin.
- Services enforce business rules.
- Database locking handles concurrency.
- Role checks are both frontend-visible and backend-enforced.
- Payment confirmation is server-verified.
- Pricing is extendable through strategies.
- Database design supports real date-based hotel inventory.

