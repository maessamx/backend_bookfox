package com.Mahmoud.sales_backend.model;
import lombok.Data;

@Data
public class Dashboard {
    private double totalSalesToday;
    private int    invoicesToday;
    private double collectionsToday;
    private double totalDebts;
    private double totalDebtsmandoob;
    private int    lowStockProducts;

    private int    totalClients;
    private int    totalProducts;
    private int    pendingCashRequests;
    private double totalProfitToday;
    private double totalProfitAllTime;
}