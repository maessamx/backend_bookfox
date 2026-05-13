package com.Mahmoud.sales_backend.model;
import lombok.Data;
@Data
public class InvoiceItem {
    private int id;
    private int invoiceId;
    private int productId;
    private int quantity;
    private String productName;

    private double pricecost;
    private double sellPrice;
    private double profit;
    private double total;
}
