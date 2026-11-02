package com.example.attendance.dto;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public record AttendanceResponse(
        Long id,
        Long employeeId,
        String employeeName,
        LocalDate date,
        AttendanceStatus status) {

    public static AttendanceResponse from(Attendance a) {
        return new AttendanceResponse(
                a.getId(),
                a.getEmployee().getId(),
                a.getEmployee().getName(),
                a.getDate(), a.getStatus());
    }
}