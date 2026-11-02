package com.example.attendance.service;

import com.example.attendance.entity.Employee;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public EmployeeService(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAllByOrderByNameAsc();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    public boolean emailTaken(Employee e) {
        return e.getId() == null
                ? employeeRepository.existsByEmail(e.getEmail())
                : employeeRepository.existsByEmailAndIdNot(e.getEmail(), e.getId());
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(Long id) {
        attendanceRepository.deleteByEmployeeId(id);
        employeeRepository.deleteById(id);
    }

    public long count() {
        return employeeRepository.count();
    }
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}
