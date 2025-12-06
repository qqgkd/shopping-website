package com.example.shop.service;

import com.example.shop.model.User;
import com.example.shop.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Base64;

@Service
public class AuthService {
  private final UserRepository userRepository;
  public AuthService(UserRepository userRepository) { this.userRepository = userRepository; }

  @Transactional
  public User register(String username, String password) {
    Optional<User> existing = userRepository.findByUsername(username);
    if (existing.isPresent()) throw new RuntimeException("用户名已存在");
    User u = new User();
    u.setUsername(username);
    u.setPasswordHash(hash(password));
    u.setCreatedAt(LocalDateTime.now());
    return userRepository.save(u);
  }

  public User login(String username, String password) {
    Optional<User> u = userRepository.findByUsername(username);
    if (u.isEmpty()) throw new RuntimeException("用户不存在");
    if (!u.get().getPasswordHash().equals(hash(password))) throw new RuntimeException("密码错误");
    return u.get();
  }

  private String hash(String raw) {
    return Base64.getEncoder().encodeToString(raw.getBytes());
  }
}

