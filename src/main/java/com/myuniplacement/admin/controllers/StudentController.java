package com.myuniplacement.admin.controllers;

import com.myuniplacement.admin.model.entitys.Application;
import com.myuniplacement.admin.model.entitys.Placement;
import com.myuniplacement.admin.model.entitys.Student;
import com.myuniplacement.admin.model.service.ApplicationService;
import com.myuniplacement.admin.model.service.PlacementService;
import com.myuniplacement.admin.model.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ApplicationService applicationService;
    private final PlacementService placementService;

    public StudentController(StudentService studentService,
                             ApplicationService applicationService,
                             PlacementService placementService) {
        this.studentService = studentService;
        this.applicationService = applicationService;
        this.placementService = placementService;
    }

    @GetMapping
    public String list(Model model, HttpSession session, HttpServletResponse res) throws Exception {
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/applications/{email}")
    public String applications(@PathVariable String email,
                               Model model,
                               HttpSession session) throws Exception {

        if (session.getAttribute("user") == null) return "redirect:/login";

        Student student = studentService.get(email);
        List<Application> apps = applicationService.getApplicationsForUser(email);
        List<Placement> placements = placementService.getAllPlacements();

        Map<String, Placement> map = new HashMap<>();
        for (Placement p : placements) map.put(p.getId(), p);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Application a : apps) {
            Map<String, Object> row = new HashMap<>();
            row.put("application", a);
            row.put("placement", map.get(a.getPlacementId()));
            rows.add(row);
        }

        model.addAttribute("student", student);
        model.addAttribute("rows", rows);

        return "students/applications";
    }
}