package com.myuniplacement.admin.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSize(MaxUploadSizeExceededException e, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "File too large. Maximum size is 10 MB.");
        return "redirect:/dashboard";
    }
}