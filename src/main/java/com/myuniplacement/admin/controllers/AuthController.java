package com.myuniplacement.admin.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/dashboard";
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirect) {

        if (!email.equals("carol.rainsford@tus.ie") &&
                !email.equals("robertoconnolly100@gmail.com")) {
            redirect.addFlashAttribute("error", "Access restricted to coordinators only.");
            return "redirect:/login";
        }

        if (!password.equals("robert")) {
            redirect.addFlashAttribute("error", "Invalid password.");
            return "redirect:/login";
        }

        try {
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
            session.setAttribute("user", user);

            redirect.addFlashAttribute("success", "Welcome back!");
            return "redirect:/dashboard";

        } catch (FirebaseAuthException e) {
            redirect.addFlashAttribute("error", "Unable to load user from Firebase.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("success", "Logged out successfully.");
        return "redirect:/login";
    }
}