package com.example.shop.controller;

import com.example.shop.model.Product;
import java.math.BigDecimal;

public class CartLine {
  private Product product;
  private int quantity;
  private BigDecimal subtotal;

  public CartLine(Product product, int quantity, BigDecimal subtotal) {
    this.product = product;
    this.quantity = quantity;
    this.subtotal = subtotal;
  }

  public Product getProduct() { return product; }
  public int getQuantity() { return quantity; }
  public BigDecimal getSubtotal() { return subtotal; }
}
