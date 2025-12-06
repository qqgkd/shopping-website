package com.example.shop.controller;

import com.example.shop.model.User;
import com.example.shop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
  private final AuthService authService;
  public AuthController(AuthService authService) { this.authService = authService; }

  @GetMapping("/login")
  public String loginPage() { return "login"; }

  @PostMapping("/login")
  public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
    try {
      User u = authService.login(username, password);
      session.setAttribute("userId", u.getId());
      return "redirect:/shop";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      return "login";
    }
  }

  @GetMapping("/register")
  public String registerPage() { return "register"; }

  @PostMapping("/register")
  public String doRegister(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
    try {
      authService.register(username, password);
      return "redirect:/login";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      return "register";
    }
  }
}
