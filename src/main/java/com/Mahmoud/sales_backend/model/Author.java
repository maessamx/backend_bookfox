package com.Mahmoud.sales_backend.model;

import java.util.Date;
import lombok.Data;

@Data
public class Author {
    private int id;
    private String name;
    private String type;
    private String country;
    private String bio;
    private String joined;
    private Date createdAt;
    private Date updatedAt;
}
