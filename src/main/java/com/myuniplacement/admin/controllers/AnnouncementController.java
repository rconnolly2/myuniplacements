package com.myuniplacement.admin.controllers;

import com.myuniplacement.admin.model.entitys.Announcement;
import com.myuniplacement.admin.model.service.AnnouncementService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model, HttpSession session, HttpServletResponse res) throws Exception {
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAttribute("announcements", service.getAll());
        return "announcements/list";
    }

    @GetMapping("/new")
    public String create(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAttribute("announcement", new Announcement());
        return "announcements/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("user") == null) return "redirect:/login";

        model.addAttribute("announcement", service.get(id));
        return "announcements/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Announcement a,
                       @RequestParam(value = "file", required = false) MultipartFile file,
                       RedirectAttributes ra) throws Exception {

        if (a.getId() == null || a.getId().isBlank()) {
            a.setId(UUID.randomUUID().toString());
        }

        try {
            service.save(a, file);
            ra.addFlashAttribute("success", "Announcement saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to save announcement.");
        }

        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) throws Exception {
        try {
            service.delete(id);
            ra.addFlashAttribute("success", "Announcement deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete announcement.");
        }
        return "redirect:/announcements";
    }
}