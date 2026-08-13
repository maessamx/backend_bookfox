package com.Mahmoud.sales_backend.model;

import lombok.Data;

@Data
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String title;
    private double price;
    private int quantity;
    private String kind;
    private double total;
}
