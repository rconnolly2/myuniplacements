package com.myuniplacement.admin.controllers;

import com.myuniplacement.admin.model.entitys.Placement;
import com.myuniplacement.admin.model.service.PlacementService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/placements")
public class PlacementController {

    private final PlacementService service;

    public PlacementController(PlacementService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model, HttpSession session, HttpServletResponse res) throws Exception {
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("placements", service.getAllPlacements());
        return "placements/list";
    }

    @GetMapping("/new")
    public String create(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("placement", new Placement());
        return "placements/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("user") == null) return "redirect:/login";
        model.addAttribute("placement", service.get(id));
        return "placements/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Placement p,
                       @RequestParam(value = "file", required = false) MultipartFile file,
                       RedirectAttributes ra) throws Exception {

        if (p.getId() == null || p.getId().isBlank()) {
            p.setId(UUID.randomUUID().toString());
        }

        try {
            service.save(p, file);
            ra.addFlashAttribute("success", "Placement saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to save placement.");
        }

        return "redirect:/placements";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) throws Exception {
        try {
            service.delete(id);
            ra.addFlashAttribute("success", "Placement deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete placement.");
        }
        return "redirect:/placements";
    }
}