package com.Mahmoud.sales_backend.model;
import lombok.Data;
import java.util.Date;

@Data
public class Client {
    private int id;
    private String name;
    private String address;
    private String phone;
    private double debt;
    private double totalpaid;
    private double totalinvoice;
    private int mandoobId;
    private Date createdAt; 
    private Date updatedAt; 
}
