package com.Mahmoud.sales_backend.module.Dashboard;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;

@Repository
public class DashboardRepository extends BaseRepository {

    public DashboardRepository(Connection conn) {
        super(conn);
    }

    // ================= COUNTS =================
    public int getTotalBooks() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM products WHERE kind = 'book'"), "val");
    }

    public int getTotalSupplies() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM products WHERE kind = 'supply'"), "val");
    }

    public int getTotalProducts() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM products"), "val");
    }

    public int getTotalOrders() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM orders"), "val");
    }

    public int getTotalUsers() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM users"), "val");
    }

    public int getOrdersToday() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM orders WHERE DATE(created_at) = CURDATE()"), "val");
    }

    public int getPendingOrders() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM orders WHERE status = 'pending'"), "val");
    }

    public int getLowStockProducts() {
        return toInt(query(
                "SELECT COUNT(*) AS val FROM products WHERE quantity <= min_stock"), "val");
    }

    // ================= REVENUE =================
    public double getTotalRevenue() {
        return toDouble(query(
                "SELECT COALESCE(SUM(total), 0) AS val FROM orders WHERE status != 'cancelled'"), "val");
    }

    public double getRevenueToday() {
        return toDouble(query(
                "SELECT COALESCE(SUM(total), 0) AS val FROM orders WHERE DATE(created_at) = CURDATE() AND status != 'cancelled'"), "val");
    }

    // ================= ANALYTICS =================
    public List<Map<String, Object>> getSalesTrend(int days) {
        return query("""
            SELECT DATE(created_at) AS day, COUNT(*) AS orders, COALESCE(SUM(total), 0) AS revenue
            FROM orders
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
              AND status != 'cancelled'
            GROUP BY DATE(created_at)
            ORDER BY day ASC
            """, days);
    }

    public List<Map<String, Object>> getTopProducts(int limit) {
        return query("""
            SELECT p.id, p.title, p.kind, p.category,
                   SUM(oi.quantity) AS sold, SUM(oi.total) AS revenue
            FROM order_items oi
            JOIN orders o ON o.id = oi.order_id
            LEFT JOIN products p ON p.id = oi.product_id
            WHERE o.status != 'cancelled'
            GROUP BY p.id, p.title, p.kind, p.category
            ORDER BY sold DESC
            LIMIT ?
            """, limit);
    }

    public List<Map<String, Object>> getTopSellers(int limit) {
        return query("""
            SELECT oi.title, oi.kind,
                   SUM(oi.quantity) AS sold, SUM(oi.total) AS revenue
            FROM order_items oi
            JOIN orders o ON o.id = oi.order_id
            WHERE o.status != 'cancelled'
            GROUP BY oi.title, oi.kind
            ORDER BY sold DESC
            LIMIT ?
            """, limit);
    }

    // ================= HELPERS =================
    private int toInt(List<Map<String, Object>> result, String key) {
        if (result.isEmpty() || result.get(0).get(key) == null)
            return 0;
        return ((Number) result.get(0).get(key)).intValue();
    }

    private double toDouble(List<Map<String, Object>> result, String key) {
        if (result.isEmpty() || result.get(0).get(key) == null)
            return 0;
        return ((Number) result.get(0).get(key)).doubleValue();
    }
}
