package com.example.attendance.controller;

import com.example.attendance.dto.AttendanceRequest;
import com.example.attendance.dto.AttendanceResponse;
import com.example.attendance.entity.Employee;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceRestController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;

    public AttendanceRestController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    // ---------- Employees ----------
    @GetMapping("/employees")
    public List<Employee> employees() {
        return employeeService.findAll();
    }

    @GetMapping("/employees/{id}")
    public Employee employee(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/employees/by-email")
    public ResponseEntity<Employee> byEmail(@RequestParam String email) {
        return employeeService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        employee.setId(null); // always create a new record
        if (employeeService.emailTaken(employee)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(employee));
    }

    // ---------- Attendance ----------
    @PostMapping("/attendance")
    public AttendanceResponse mark(@Valid @RequestBody AttendanceRequest request) {
        return AttendanceResponse.from(
                attendanceService.markOne(request.employeeId(), request.date(), request.status()));
    }

    @GetMapping("/attendance")
    public List<AttendanceResponse> report(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate toDate = to != null ? to : LocalDate.now();
        LocalDate fromDate = from != null ? from : toDate.minusDays(30);
        return attendanceService.report(employeeId, fromDate, toDate)
                .stream().map(AttendanceResponse::from).toList();
    }

    @GetMapping("/attendance/date/{date}")
    public List<AttendanceResponse> byDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.forDate(date).stream().map(AttendanceResponse::from).toList();
    }

    // Employee not found -> 404 JSON
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> notFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }
}