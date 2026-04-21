package com.Mahmoud.sales_backend.model;
import java.util.Date;
public class Invoice {
    private int id;
    private int mandoobId;
    private int clientId;
    private double total;
    private boolean paid;
    private Date createdAt;
}
