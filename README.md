# 🦊 BookFox Backend System

## 📋 Project Description
A backend system built using **Spring Boot** for managing the BookFox bookstore —
authentication, authors & publishers, books, school/office supplies, orders,
and a full admin dashboard.

The system is designed for the **Helwan Engineering** summer training team.
 
---

## 👥 Team Members
- Mahmoud Essam
- Youssef Farag
- Karim Arafat
- Youssef Mohamed
- Youssef Hegazy

---

## 🛠️ Tech Stack
- Java 17
- Spring Boot
- Spring Security (JWT + BCrypt)
- MySQL (plain JDBC via a universal BaseRepository)
- Maven

---

## ▶️ How to Run

```bash
# Run the application
./mvnw spring-boot:run
```

The server starts on http://localhost:8080

---

## 📁 Project Structure

```
src/main/java/com/Mahmoud/sales_backend/
├── SalesBackendApplication.java
├── Repository/
│   └── BaseRepository.java        # universal CRUD (getAll, query, insert, update, delete)
├── config/
│   └── DBConfig.java              # Connection bean, reads spring.datasource.* from application.properties
├── security/
│   ├── CorsConfig.java
│   ├── JwtAuthFilter.java         # Bearer token filter
│   ├── PasswordUtil.java          # BCrypt
│   └── UserContext.java           # ThreadLocal userId/role
├── shared/
│   ├── ApiResponse.java           # { success, message, data }
│   ├── JwtUtil.java
│   └── PhoneValidator.java
├── model/
│   ├── User.java  Author.java  Product.java
│   ├── Order.java  OrderItem.java  Dashboard.java
└── module/                        # each module = Controller + Service + Repository
    ├── Auth/       # /auth        login, register, verify-token
    ├── User/       # /users       admin CRUD + profile + role
    ├── Author/     # /authors     CRUD + books-by-author
    ├── Book/       # /books       CRUD (products where kind='book')
    ├── Supply/     # /supplies    CRUD (products where kind='supply')
    ├── Order/      # /orders      create with items, status, delete
    └── Dashboard/  # /dashboard   stats, sales-analytics, top-sellers
```

---

## 🔑 Demo Accounts

| Role  | Email              | Password  |
|-------|--------------------|-----------|
| Admin | admin@gmail.com    | admin123  |
| User  | user@gmail.com     | user123   |

---

## 📦 Database

The schema is in `sql1.md`. Run it once on your MySQL instance, then set the
connection details in `application.properties` (or the fallback in
`DatabaseConnection.java`).
