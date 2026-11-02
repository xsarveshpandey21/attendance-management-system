package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByDate(LocalDate date);

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByDateBetweenOrderByDateDescEmployeeNameAsc(LocalDate from, LocalDate to);

    List<Attendance> findByEmployeeIdAndDateBetweenOrderByDateDesc(Long employeeId, LocalDate from, LocalDate to);

    long countByDateAndStatus(LocalDate date, AttendanceStatus status);

    void deleteByEmployeeId(Long employeeId);
}
