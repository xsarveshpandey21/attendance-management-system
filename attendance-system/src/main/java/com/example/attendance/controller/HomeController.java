package com.example.attendance.controller;

import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;

    public HomeController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("totalEmployees", employeeService.count());
        model.addAttribute("present", attendanceService.countForDate(today, AttendanceStatus.PRESENT));
        model.addAttribute("absent", attendanceService.countForDate(today, AttendanceStatus.ABSENT));
        model.addAttribute("leave", attendanceService.countForDate(today, AttendanceStatus.LEAVE));
        return "index";
    }
}
