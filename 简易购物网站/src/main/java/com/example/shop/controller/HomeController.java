package com.example.shop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping("/")
  public String index(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/login";
    return "redirect:/shop";
  }
}

