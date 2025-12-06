package com.example.shop.websocket;

import com.example.shop.model.Order;
import com.example.shop.model.OrderItem;
import com.example.shop.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {
  private final List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    sessions.add(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
    sessions.remove(session);
  }

  public void notifyPaid(Long orderId) {
    synchronized (sessions) {
      for (WebSocketSession s : sessions) {
        try { s.sendMessage(new TextMessage("ORDER_PAID:" + orderId)); } catch (Exception ignored) {}
      }
    }
  }

  public void notifySale(User user, Order order) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("type", "SALE");
    payload.put("orderId", order.getId());
    payload.put("user", user.getUsername());
    payload.put("total", order.getTotal());
    List<Map<String, Object>> items = order.getItems().stream().map(i -> {
      Map<String, Object> m = new HashMap<>();
      m.put("product", i.getProduct().getName());
      m.put("quantity", i.getQuantity());
      m.put("unitPrice", i.getUnitPrice());
      return m;
    }).collect(Collectors.toList());
    payload.put("items", items);
    try {
      String json = mapper.writeValueAsString(payload);
      synchronized (sessions) {
        for (WebSocketSession s : sessions) {
          try { s.sendMessage(new TextMessage(json)); } catch (Exception ignored) {}
        }
      }
    } catch (Exception ignored) {}
  }
}
