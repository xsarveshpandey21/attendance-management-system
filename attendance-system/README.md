# Attendance System (Spring Boot MVC)

Stack: Spring Boot 3.3, Spring MVC, Thymeleaf, Spring Data JPA, H2, Bean Validation. Requires Java 17+ and Maven.

## Run
    mvn spring-boot:run
Open http://localhost:8080
H2 console: /h2-console  (JDBC URL `jdbc:h2:file:./data/attendancedb`, user `sa`, empty password)

## MVC structure
- model/       Employee, Attendance, AttendanceStatus   (Model)
- repository/  Spring Data JPA repositories
- service/     Business logic
- controller/  Home, Employee, Attendance controllers   (Controller)
- templates/   Thymeleaf pages                          (View)

## Features
- Dashboard with today's Present / Absent / Leave counts
- Employee CRUD with validation and duplicate-email check
- Mark or update attendance for any date (one record per employee per day)
- Reports filtered by employee and date range

## Switch to MySQL
Add the `mysql-connector-j` dependency and change the `spring.datasource.*` properties.
