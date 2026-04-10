
# 🚀 Roomly — Intelligent Hotel Booking & Management Platform

> A high-performance hotel booking and management system powered by dynamic pricing, concurrency control, and scalable backend architecture.

---

## 🌟 Overview

**Roomly** is not just another booking system — it's a **smart, extensible platform** designed to simulate real-world hotel operations with:

* 🧠 Dynamic pricing strategies
* ⚡ Concurrency-safe booking flows
* 💳 Integrated payment handling
* 🏨 Inventory-aware room allocation

Built with a strong focus on **system design, scalability, and clean architecture**, Roomly reflects real-world backend engineering challenges.

---

## 🧩 Key Features

### 🏷️ Dynamic Pricing Engine

* Strategy-based pricing system
* Supports:

  * Base pricing
  * Seasonal pricing
  * Demand-based adjustments
* Easily extendable using design patterns (Strategy + Decorator)

---

### ⚙️ Concurrency-Safe Booking

* Prevents **double booking issues**
* Uses:

  * Database-level locking (`@Lock`)
  * Transactional consistency
* Simulates real-world race conditions

---

### 🏨 Inventory Management

* Tracks room availability in real-time
* Handles:

  * Room allocation
  * Availability checks
  * Booking lifecycle

---

### 💳 Payment Integration (Simulated/Pluggable)

* Payment flow abstraction
* Easily extendable to real gateways (Stripe, Razorpay)

---

### 🧱 Clean Architecture

* Layered architecture:

  * Controller → Service → Repository
* Strong separation of concerns
* Design pattern-driven implementation

---

## 🛠️ Tech Stack

| Layer        | Technology                 |
| ------------ | -------------------------- |
| Backend      | Spring Boot                |
| Language     | Java                       |
| Database     | JPA / Hibernate            |
| Concurrency  | Pessimistic Locking        |
| Architecture | Layered + Strategy Pattern |

---

## 🧠 System Design Highlights

### 🔹 Pricing Strategy Pattern

* Decouples pricing logic from core system
* Allows runtime flexibility in pricing decisions

### 🔹 Decorator Pattern (Pricing Enhancements)

* Dynamically composes pricing rules
* Avoids rigid conditional logic

### 🔹 Transaction Management

* Ensures atomic booking operations
* Handles edge cases like:

  * Simultaneous bookings
  * Inventory conflicts

---

## 📊 Real-World Problems Solved

* ❌ Double booking in high-traffic systems
* ❌ Static pricing inefficiencies
* ❌ Tight coupling in business logic
* ❌ Poor scalability in naive CRUD apps

---

## 🧪 Sample Flow

```text
User → Search Room → Check Availability → Lock Inventory → Apply Pricing Strategy → Process Payment → Confirm Booking
```

---

## 📁 Project Structure (Simplified)

```
roomly/
├── controller/
├── service/
├── repository/
├── entities/
├── strategies/
├── DTOs/
├── advices/
├── exceptions/
├── config/
```

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Maven / Gradle
* MySQL / PostgreSQL

### Run the Project

```bash
git clone https://github.com/AamirrH/Roomly.git
cd Roomly
./mvnw spring-boot:run
```

---
