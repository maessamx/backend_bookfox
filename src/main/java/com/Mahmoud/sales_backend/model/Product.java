package com.Mahmoud.sales_backend.model;

import java.util.Date;
import lombok.Data;

@Data
public class Product {
    private int id;
    private String kind;
    private String title;
    private int authorId;
    private String authorName;
    private String category;
    private double price;
    private String cover;
    private String description;
    private int quantity;
    private int minStock;
    private Date createdAt;
    private Date updatedAt;
}
