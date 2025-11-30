package com.myuniplacement.admin.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("user")
    public Object addUserToModel(HttpSession session) {
        return session.getAttribute("user");
    }
}