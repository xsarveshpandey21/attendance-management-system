# Attendance Management System & API Proxy

A comprehensive Employee Attendance Management System accompanied by a reverse API Proxy service built with Spring Boot and Java.

---

## 📁 Repository Structure

```text
├── api-proxy/              # Spring Boot reverse proxy service (Port 8081)
│   ├── src/
│   ├── pom.xml
│   └── HELP.md
│
├── attendance-system/      # Spring Boot MVC Attendance application (Port 8080)
│   ├── src/
│   ├── pom.xml
│   └── README.md
│
├── .gitignore              # Repository gitignore rules
└── README.md               # Main project documentation
```

---

## 🛠️ Tech Stack & Prerequisites

- **Java**: 17 or higher
- **Framework**: Spring Boot 3.3.x
- **Build Tool**: Apache Maven (Maven Wrapper included in `api-proxy`)
- **Database**: H2 (In-memory / file-based)
- **Frontend / Views**: Thymeleaf, HTML5, CSS3

---

## 🚀 Services Overview

### 1. Attendance System (`attendance-system`)
Core MVC web application handling employee data and daily attendance logging.

- **Port**: `8080`
- **Features**:
  - Employee Management (CRUD operations with validation & duplicate email protection)
  - Daily Attendance Marking (Present / Absent / Leave status)
  - Date and employee filtering for attendance reports
  - Dashboard analytics
  - Built-in H2 Database & Console
- **Run command**:
  ```bash
  cd attendance-system
  mvn spring-boot:run
  ```
- **Access**:
  - Web UI: [http://localhost:8080](http://localhost:8080)
  - H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:file:./data/attendancedb`, User: `sa`, Password: *(empty)*)

---

### 2. API Proxy (`api-proxy`)
Reverse proxy gateway forwarding HTTP requests to the attendance service while stripping/rewriting sensitive hop-by-hop headers and redirect URLs.

- **Port**: `8081`
- **Target URL**: Configured via `proxy.target-url` (defaults to `http://localhost:8080`)
- **Run command**:
  ```bash
  cd api-proxy
  ./mvnw spring-boot:run
  # Or on Windows:
  # .\mvnw.cmd spring-boot:run
  ```
- **Access**:
  - Proxied UI / APIs: [http://localhost:8081](http://localhost:8081)

---

## 📄 License
This project is open source and available under the standard MIT License.
