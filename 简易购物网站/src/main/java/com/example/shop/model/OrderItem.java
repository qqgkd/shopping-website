package com.example.shop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional = false)
  private Order order;
  @ManyToOne(optional = false)
  private Product product;
  @Column(nullable = false)
  private Integer quantity;
  @Column(nullable = false)
  private BigDecimal unitPrice;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}

