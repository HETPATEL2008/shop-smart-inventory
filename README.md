# ShopSmart (Order & Inventory Management System)

A high-performance, console-based e-commerce backend engine engineered using modern Core Java features and strict Layered Architecture principles. The system handles secure user authentication, transactional catalog updates, and concurrent-safe inventory workflows.

---

## 🏗️ Architectural Overview

The application utilizes a clean **Layered Architecture** to maintain a strict separation of concerns (SoC), decoupling edge user-interface interactions from processing routines and transactional persistence blocks.

```text
       ┌─────────────────────────────────────────────────────────┐
       │                       UI Layer                          │
       │                 (com.shopsmart.ui)                      │
       └────────────────────────────┬────────────────────────────┘
                                    │
                                    ▼
       ┌─────────────────────────────────────────────────────────┐
       │                     Service Layer                       │
       │               (com.shopsmart.service)                   │
       │       [Business Validation & ACID Transactions]         │
       └────────────────────────────┬────────────────────────────┘
                                    │
                                    ▼
       ┌─────────────────────────────────────────────────────────┐
       │                       DAO Layer                         │
       │                  (com.shopsmart.dao)                    │
       │       [Raw SQL Isolation & ResultSet Mapping]           │
       └────────────────────────────┬────────────────────────────┘
                                    │
                                    ▼
       ┌─────────────────────────────────────────────────────────┐
       │                    Persistence Layer                    │
       │            (MySQL Server via HikariCP Pool)             │
       └─────────────────────────────────────────────────────────┘