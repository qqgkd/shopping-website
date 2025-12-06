package com.example.shop.repo;

import com.example.shop.model.Order;
import com.example.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUser(User user);
}

