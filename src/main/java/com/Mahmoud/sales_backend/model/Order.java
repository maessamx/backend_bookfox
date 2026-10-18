package com.Mahmoud.sales_backend.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Order {
    private int id;
    private String orderCode;
    private int userId;
    private String customer;
    private String email;
    private double subtotal;
    private double shipping;
    private double total;
    private String status;
    private String governorate;
    private String city;
    private String address;
    private String notes;
    private String payment;
    private List<OrderItem> items;
    private Date createdAt;
}
