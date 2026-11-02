package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.EmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private static final String PREFIX = "status_";

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @GetMapping("/mark")
    public String markForm(@RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Model model) {
        LocalDate d = date != null ? date : LocalDate.now();
        model.addAttribute("date", d);
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("statusMap", attendanceService.statusMapForDate(d));
        return "attendance/mark";
    }

    @PostMapping("/mark")
    public String mark(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam Map<String, String> params,
                       RedirectAttributes redirect) {
        Map<Long, AttendanceStatus> statuses = new LinkedHashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith(PREFIX)) {
                statuses.put(Long.valueOf(key.substring(PREFIX.length())), AttendanceStatus.valueOf(value));
            }
        });
        attendanceService.markAttendance(date, statuses);
        redirect.addFlashAttribute("message", "Attendance saved for " + date);
        return "redirect:/attendance/mark?date=" + date;
    }

    @GetMapping("/report")
    public String report(@RequestParam(required = false) Long employeeId,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                         Model model) {
        LocalDate toDate = to != null ? to : LocalDate.now();
        LocalDate fromDate = from != null ? from : toDate.minusDays(30);
        List<Attendance> records = attendanceService.report(employeeId, fromDate, toDate);

        model.addAttribute("records", records);
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("from", fromDate);
        model.addAttribute("to", toDate);
        model.addAttribute("presentCount", records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        model.addAttribute("absentCount", records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());
        model.addAttribute("leaveCount", records.stream().filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count());
        return "attendance/report";
    }
}
