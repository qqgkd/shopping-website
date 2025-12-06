package com.example.shop.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class OrderWebSocketConfig implements WebSocketConfigurer {
  private final OrderWebSocketHandler handler;
  public OrderWebSocketConfig(OrderWebSocketHandler handler) { this.handler = handler; }
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws/orders").setAllowedOrigins("*");
  }
}

