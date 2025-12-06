package com.example.shop.socket;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Component
public class SocketServer {
  private final List<PrintWriter> clients = Collections.synchronizedList(new ArrayList<>());

  @PostConstruct
  public void start() {
    Thread t = new Thread(() -> {
      try (ServerSocket server = new ServerSocket(9090)) {
        while (true) {
          Socket s = server.accept();
          PrintWriter out = new PrintWriter(s.getOutputStream(), true);
          clients.add(out);
          Thread clientThread = new Thread(() -> handleClient(s, out));
          clientThread.start();
        }
      } catch (Exception e) {
      }
    });
    t.setDaemon(true);
    t.start();
  }

  private void handleClient(Socket s, PrintWriter out) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
      out.println("CONNECTED");
      String line;
      while ((line = br.readLine()) != null) {
        out.println("ECHO:" + line);
      }
    } catch (Exception ignored) {
    } finally {
      clients.remove(out);
      try { s.close(); } catch (Exception ignored) {}
    }
  }

  public void broadcast(String msg) {
    synchronized (clients) {
      for (PrintWriter c : clients) {
        try { c.println(msg); } catch (Exception ignored) {}
      }
    }
  }
}

