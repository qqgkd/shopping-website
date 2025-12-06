package com.example.shop.service;

import com.example.shop.model.*;
import com.example.shop.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public OrderService(ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
    this.productRepository = productRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Transactional
  public Order createOrder(User user, Map<Long, Integer> cart) {
    Order order = new Order();
    order.setUser(user);
    order.setCreatedAt(LocalDateTime.now());
    order.setStatus(OrderStatus.PENDING);
    order.setTotal(BigDecimal.ZERO);
    order = orderRepository.save(order);

    BigDecimal total = BigDecimal.ZERO;
    for (Map.Entry<Long, Integer> e : cart.entrySet()) {
      Product p = productRepository.findById(e.getKey()).orElseThrow();
      int qty = e.getValue();
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(p);
      item.setQuantity(qty);
      item.setUnitPrice(p.getPrice());
      orderItemRepository.save(item);
      total = total.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
    }
    order.setTotal(total);
    return orderRepository.save(order);
  }

  @Transactional
  public Order markPaid(Long orderId) {
    Order o = orderRepository.findById(orderId).orElseThrow();
    o.setStatus(OrderStatus.PAID);
    o.getItems().size(); // Initialize lazy collection
    return orderRepository.save(o);
  }

  public List<Order> listOrders(User user) {
    return orderRepository.findByUser(user);
  }
}

