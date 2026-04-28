package com.Mahmoud.sales_backend.model;
import lombok.Data;
import java.util.Date;

@Data
public class Payment {
    private int id;
    private int invoiceId;
    private double amount;
    private String notes;
    private Date createdAt;
}