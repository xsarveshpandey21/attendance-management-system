package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.entity.Employee;
import com.example.attendance.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeService employeeService) {
        this.attendanceRepository = attendanceRepository;
        this.employeeService = employeeService;
    }


    public Map<Long, String> statusMapForDate(LocalDate date) {
        Map<Long, String> map = new HashMap<>();
        for (Employee e : employeeService.findAll()) {
            map.put(e.getId(), AttendanceStatus.PRESENT.name()); // default selection
        }
        for (Attendance a : attendanceRepository.findByDate(date)) {
            map.put(a.getEmployee().getId(), a.getStatus().name());
        }
        return map;
    }

    @Transactional
    public void markAttendance(LocalDate date, Map<Long, AttendanceStatus> statuses) {
        statuses.forEach((employeeId, status) -> {
            Attendance record = attendanceRepository.findByEmployeeIdAndDate(employeeId, date)
                    .orElseGet(() -> new Attendance(employeeService.findById(employeeId), date, status));
            record.setStatus(status);
            attendanceRepository.save(record);
        });
    }

    public List<Attendance> report(Long employeeId, LocalDate from, LocalDate to) {
        if (employeeId == null) {
            return attendanceRepository.findByDateBetweenOrderByDateDescEmployeeNameAsc(from, to);
        }
        return attendanceRepository.findByEmployeeIdAndDateBetweenOrderByDateDesc(employeeId, from, to);
    }

    public long countForDate(LocalDate date, AttendanceStatus status) {
        return attendanceRepository.countByDateAndStatus(date, status);
    }
    @Transactional
    public Attendance markOne(Long employeeId, LocalDate date, AttendanceStatus status) {
        Attendance record = attendanceRepository.findByEmployeeIdAndDate(employeeId, date)
                .orElseGet(() -> new Attendance(employeeService.findById(employeeId), date, status));
        record.setStatus(status);
        return attendanceRepository.save(record);
    }

    public List<Attendance> forDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}
