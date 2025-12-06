package com.example.shop.controller;

import com.example.shop.model.Product;
import com.example.shop.model.User;
import com.example.shop.repo.ProductRepository;
import com.example.shop.repo.UserRepository;
import com.example.shop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ShopController {
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final OrderService orderService;

  public ShopController(ProductRepository productRepository, UserRepository userRepository, OrderService orderService) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.orderService = orderService;
  }

  @GetMapping("/shop")
  public String shop(Model model, HttpSession session) {
    model.addAttribute("products", productRepository.findAll());
    Map<Long,Integer> cart = getCart(session);
    model.addAttribute("cart", cart);

    List<CartLine> lines = new ArrayList<>();
    java.math.BigDecimal total = java.math.BigDecimal.ZERO;
    for (Map.Entry<Long,Integer> e : cart.entrySet()) {
      productRepository.findById(e.getKey()).ifPresent(p -> {
        java.math.BigDecimal sub = p.getPrice().multiply(java.math.BigDecimal.valueOf(e.getValue()));
        lines.add(new CartLine(p, e.getValue(), sub));
      });
    }
    for (CartLine l : lines) total = total.add(l.getSubtotal());
    model.addAttribute("cartLines", lines);
    model.addAttribute("cartTotal", total);
    return "shop";
  }

  @PostMapping("/cart/add")
  public String addToCart(@RequestParam("productId") Long productId, @RequestParam(name = "qty", defaultValue = "1") Integer qty, HttpSession session) {
    Map<Long,Integer> cart = getCart(session);
    cart.put(productId, cart.getOrDefault(productId, 0) + qty);
    session.setAttribute("cart", cart);
    return "redirect:/shop";
  }

  @GetMapping("/checkout")
  public String checkout(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/login";
    Optional<User> u = userRepository.findById(userId);
    if (u.isEmpty()) return "redirect:/login";
    Map<Long,Integer> cart = getCart(session);
    if (cart.isEmpty()) return "redirect:/shop";
    var order = orderService.createOrder(u.get(), cart);
    session.removeAttribute("cart");
    return "redirect:/order/" + order.getId();
  }

  @ResponseBody
  @GetMapping("/api/cart")
  public Map<Long,Integer> apiCart(HttpSession session) {
    return getCart(session);
  }

  private Map<Long,Integer> getCart(HttpSession session) {
    Object c = session.getAttribute("cart");
    if (c instanceof Map) return (Map<Long,Integer>) c;
    Map<Long,Integer> cart = new HashMap<>();
    session.setAttribute("cart", cart);
    return cart;
  }
}
