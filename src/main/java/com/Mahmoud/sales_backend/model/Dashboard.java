package com.Mahmoud.sales_backend.model;

import lombok.Data;

@Data
public class Dashboard {
    private int totalBooks;
    private int totalSupplies;
    private int totalProducts;
    private int totalOrders;
    private int totalUsers;
    private int ordersToday;
    private double revenue;
    private double revenueToday;
    private int pendingOrders;
    private int lowStockProducts;
}
