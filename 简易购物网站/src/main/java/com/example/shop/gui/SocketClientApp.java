package com.example.shop.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientApp {
  private JTextArea area;
  private JTextField input;
  private PrintWriter out;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SocketClientApp().start());
  }

  private void start() {
    JFrame frame = new JFrame("Socket 客户端");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    area = new JTextArea();
    area.setEditable(false);
    input = new JTextField();
    JButton send = new JButton("发送");
    send.addActionListener(this::send);
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.add(input, BorderLayout.CENTER);
    bottom.add(send, BorderLayout.EAST);
    frame.add(new JScrollPane(area), BorderLayout.CENTER);
    frame.add(bottom, BorderLayout.SOUTH);
    frame.setSize(500, 400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    connect();
  }

  private void connect() {
    try {
      Socket s = new Socket("127.0.0.1", 9090);
      out = new PrintWriter(s.getOutputStream(), true);
      Thread t = new Thread(() -> {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
          String line;
          while ((line = br.readLine()) != null) {
            String l = line;
            SwingUtilities.invokeLater(() -> area.append(l + "\n"));
          }
        } catch (Exception ignored) {}
      });
      t.start();
    } catch (Exception e) {
      SwingUtilities.invokeLater(() -> area.append("连接失败\n"));
    }
  }

  private void send(ActionEvent e) {
    if (out != null) {
      out.println(input.getText());
      input.setText("");
    }
  }
}

