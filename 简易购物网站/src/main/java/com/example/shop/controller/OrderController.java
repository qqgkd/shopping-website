package com.example.shop.controller;

import com.example.shop.model.Order;
import com.example.shop.model.User;
import com.example.shop.repo.OrderRepository;
import com.example.shop.repo.UserRepository;
import com.example.shop.service.OrderService;
import com.example.shop.service.PaymentProcessor;
import com.example.shop.websocket.OrderWebSocketHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller
public class OrderController {
  private final OrderRepository orderRepository;
  private final OrderService orderService;
  private final PaymentProcessor paymentProcessor;
  private final OrderWebSocketHandler orderWebSocketHandler;
  private final UserRepository userRepository;

  public OrderController(OrderRepository orderRepository, OrderService orderService, PaymentProcessor paymentProcessor, OrderWebSocketHandler orderWebSocketHandler, UserRepository userRepository) {
    this.orderRepository = orderRepository;
    this.orderService = orderService;
    this.paymentProcessor = paymentProcessor;
    this.orderWebSocketHandler = orderWebSocketHandler;
    this.userRepository = userRepository;
  }

  @GetMapping("/order/{id}")
  public String orderPage(@PathVariable("id") Long id, Model model, HttpSession session) {
    Optional<Order> o = orderRepository.findById(id);
    if (o.isEmpty()) return "redirect:/shop";
    model.addAttribute("order", o.get());
    return "payment";
  }

  @PostMapping("/order/{id}/pay")
  public String pay(@PathVariable("id") Long id, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/login";
    Optional<User> u = userRepository.findById(userId);
    if (u.isEmpty()) return "redirect:/login";
    paymentProcessor.processPayment(id).thenAccept(success -> {
      if (success) {
        Order updated = orderService.markPaid(id);
        orderWebSocketHandler.notifyPaid(id);
        orderWebSocketHandler.notifySale(u.get(), updated);
      }
    });
    return "redirect:/order/" + id;
  }

  @GetMapping("/orders")
  public String orders(Model model, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/login";
    Optional<User> u = userRepository.findById(userId);
    if (u.isEmpty()) return "redirect:/login";
    model.addAttribute("orders", orderService.listOrders(u.get()));
    return "orders";
  }
}
