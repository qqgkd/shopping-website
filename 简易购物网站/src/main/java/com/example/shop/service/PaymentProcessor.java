package com.example.shop.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentProcessor {
  @Async
  public CompletableFuture<Boolean> processPayment(Long orderId) {
    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return CompletableFuture.completedFuture(false);
    }
    return CompletableFuture.completedFuture(true);
  }
}

