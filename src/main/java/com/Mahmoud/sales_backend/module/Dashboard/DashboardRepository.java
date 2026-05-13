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

    // ================= Admin Stats =================
    public double getTotalSalesToday() {
        List<Map<String, Object>> result = query(
            "SELECT COALESCE(SUM(total), 0) AS val FROM invoices WHERE DATE(created_at) = CURDATE()");
        return toDouble(result, "val");
    }

    public int getInvoicesToday() {
        List<Map<String, Object>> result = query(
            "SELECT COUNT(*) AS val FROM invoices WHERE DATE(created_at) = CURDATE()");
        return toInt(result, "val");
    }

    public double getCollectionsToday() {
        List<Map<String, Object>> result = query(
            "SELECT COALESCE(SUM(amount), 0) AS val FROM payments WHERE DATE(created_at) = CURDATE()");
        return toDouble(result, "val");
    }

    public int getTotalClients() {
        List<Map<String, Object>> result = query(
            "SELECT COUNT(*) AS val FROM clients");
        return toInt(result, "val");
    }

    public int getTotalProducts() {
        List<Map<String, Object>> result = query(
            "SELECT COUNT(*) AS val FROM products");
        return toInt(result, "val");
    }

    public int getLowStockProducts() {
        List<Map<String, Object>> result = query(
            "SELECT COUNT(*) AS val FROM products WHERE quantity <= min_stock");
        return toInt(result, "val");
    }

    public double getTotalDebts() {
        List<Map<String, Object>> result = query("""
            SELECT COALESCE(SUM(total - paid), 0) AS val
            FROM invoices
        """);
        //            WHERE status != 'paid'
        return toDouble(result, "val");
    }

    public int getPendingCashRequests() {
        List<Map<String, Object>> result = query(
            "SELECT COUNT(*) AS val FROM cash_requests WHERE status = 'pending'");
        return toInt(result, "val");
    }
    public int getMandoobStockTotalQuantity(int mandoobId) {

    String sql = """
        SELECT COALESCE(SUM(quantity), 0) AS total_quantity
        FROM mandoob_stock
        WHERE mandoob_id = ?
    """;

    List<Map<String, Object>> result = query(sql, mandoobId);

    if (result.isEmpty() || result.get(0).get("total_quantity") == null) {
        return 0;
    }

    return ((Number) result.get(0).get("total_quantity")).intValue();
}

    public double getTotalProfitToday() {
        List<Map<String, Object>> result = query("""
            SELECT COALESCE(SUM(ii.profit), 0) AS val
            FROM invoice_items ii
            JOIN invoices i ON i.id = ii.invoice_id
            WHERE DATE(i.created_at) = CURDATE()
        """);
        return toDouble(result, "val");
    }

    public double getTotalProfitAllTime() {
        List<Map<String, Object>> result = query(
            "SELECT COALESCE(SUM(profit), 0) AS val FROM invoice_items");
        return toDouble(result, "val");
    }
    public double getTotalMandoobNetBalance() {

    List<Map<String, Object>> debt = query("""
        SELECT COALESCE(SUM(paid), 0) AS val
        FROM invoices
    """);

    List<Map<String, Object>> cash = query("""
        SELECT COALESCE(SUM(amount), 0) AS val
        FROM cash_requests
        WHERE status = 'approved'
    """);

    double invoiceDebt = debt.isEmpty() ? 0 :
            ((Number) debt.get(0).get("val")).doubleValue();

    double cashApproved = cash.isEmpty() ? 0 :
            ((Number) cash.get(0).get("val")).doubleValue();

    return invoiceDebt - cashApproved;
}
    public List<Map<String, Object>> getAllMandoobNetBalances() {

    String sql = """
        SELECT 
            u.id AS mandoob_id,
            u.name AS mandoob_name,

            COALESCE(SUM(i.total - i.paid), 0) AS invoice_debt,
            COALESCE(SUM(CASE WHEN cr.status = 'approved' THEN cr.amount ELSE 0 END), 0) AS approved_cash,

            (
                COALESCE(SUM(i.total - i.paid), 0) -
                COALESCE(SUM(CASE WHEN cr.status = 'approved' THEN cr.amount ELSE 0 END), 0)
            ) AS net_balance

        FROM users u

        LEFT JOIN invoices i 
            ON i.mandoob_id = u.id

        LEFT JOIN cash_requests cr 
            ON cr.mandoob_id = u.id

        GROUP BY u.id, u.name
        ORDER BY net_balance DESC
    """;

    return query(sql);
}

    // ================= Mandoob Stats =================
    public double getMandoobSalesToday(int mandoobId) {
        List<Map<String, Object>> result = query("""
            SELECT COALESCE(SUM(total), 0) AS val
            FROM invoices
            WHERE mandoob_id = ? AND DATE(created_at) = CURDATE()
        """, mandoobId);
        return toDouble(result, "val");
    }

    public double getMandoobDebts(int mandoobId) {
        List<Map<String, Object>> result = query("""
            SELECT COALESCE(SUM(total - paid), 0) AS val
            FROM invoices
            WHERE mandoob_id = ? AND status != 'paid'
        """, mandoobId);
        return toDouble(result, "val");
    }
    public double getMandoobNetBalance(int mandoobId) {

    List<Map<String, Object>> debt = query("""
        SELECT COALESCE(SUM(paid), 0) AS val
        FROM invoices
        WHERE mandoob_id = ?
    """, mandoobId);

    List<Map<String, Object>> cash = query("""
        SELECT COALESCE(SUM(amount), 0) AS val
        FROM cash_requests
        WHERE mandoob_id = ? AND status = 'approved'
    """, mandoobId);

    double invoiceDebt = debt.isEmpty() ? 0 :
            ((Number) debt.get(0).get("val")).doubleValue();

    double cashApproved = cash.isEmpty() ? 0 :
            ((Number) cash.get(0).get("val")).doubleValue();

    return invoiceDebt - cashApproved;
}
    public double getMandoobNetBalanceWithpending(int mandoobId) {

    List<Map<String, Object>> debt = query("""
        SELECT COALESCE(SUM(paid), 0) AS val
        FROM invoices
        WHERE mandoob_id = ?
    """, mandoobId);

    List<Map<String, Object>> cash = query("""
        SELECT COALESCE(SUM(amount), 0) AS val
        FROM cash_requests
        WHERE mandoob_id = ? AND status != 'rejected'
    """, mandoobId);

    double invoiceDebt = debt.isEmpty() ? 0 :
            ((Number) debt.get(0).get("val")).doubleValue();

    double cashApproved = cash.isEmpty() ? 0 :
            ((Number) cash.get(0).get("val")).doubleValue();

    return invoiceDebt - cashApproved;
}

    public int getMandoobInvoicesToday(int mandoobId) {
        List<Map<String, Object>> result = query("""
            SELECT COUNT(*) AS val
            FROM invoices
            WHERE mandoob_id = ? AND DATE(created_at) = CURDATE()
        """, mandoobId);
        return toInt(result, "val");
    }

    public double getMandoobCollectionsToday(int mandoobId) {
        List<Map<String, Object>> result = query("""
            SELECT COALESCE(SUM(p.amount), 0) AS val
            FROM payments p
            JOIN invoices i ON i.id = p.invoice_id
            WHERE i.mandoob_id = ? AND DATE(p.created_at) = CURDATE()
        """, mandoobId);
        return toDouble(result, "val");
    }
    public int getTotalClientsByMandoob(int mandoobId) {
    List<Map<String, Object>> result = query(
        "SELECT COUNT(*) AS val FROM clients WHERE mandoob_id = ?",
        mandoobId
    );
    return toInt(result, "val");
}




// ===== أضف الـ methods دي جوا DashboardRepository =====

// ─────────────────────────────────────────────
// 1. أكثر المنتجات مبيعاً (top N)
// ─────────────────────────────────────────────
public List<Map<String, Object>> getTopProducts(int limit) {
    String sql = """
        SELECT
            p.id            AS product_id,
            p.name          AS product_name,
            SUM(ii.quantity)                        AS quantity_sold,
            SUM(ii.sell_price * ii.quantity)        AS revenue,
            SUM(ii.profit)                          AS profit
        FROM invoice_items ii
        JOIN products p ON p.id = ii.product_id
        GROUP BY p.id, p.name
        ORDER BY quantity_sold DESC
        LIMIT ?
    """;
    return query(sql, limit);
}

// ─────────────────────────────────────────────
// 2. تطور المبيعات آخر N يوم (يومي)
// ─────────────────────────────────────────────
public List<Map<String, Object>> getSalesTrend(int days) {
    String sql = """
        SELECT
            DATE(i.created_at)                      AS sale_date,
            COUNT(i.id)                             AS invoices_count,
            COALESCE(SUM(i.total), 0)               AS total_sales,
            COALESCE(SUM(i.paid), 0)                AS total_collected,
            COALESCE(SUM(ii.profit), 0)             AS total_profit
        FROM invoices i
        LEFT JOIN invoice_items ii ON ii.invoice_id = i.id
        WHERE i.created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
        GROUP BY DATE(i.created_at)
        ORDER BY sale_date ASC
    """;
    return query(sql, days);
}

// ─────────────────────────────────────────────
// 3. أداء المندوبين الكامل
// ─────────────────────────────────────────────
public List<Map<String, Object>> getMandoobsPerformance() {
    String sql = """
        SELECT
            u.id                                                        AS mandoob_id,
            u.name                                                      AS mandoob_name,
            u.phone                                                     AS mandoob_phone,
            u.status                                                    AS mandoob_status,

            -- عملاء
            COUNT(DISTINCT c.id)                                        AS total_clients,

            -- فواتير
            COUNT(DISTINCT i.id)                                        AS total_invoices,
            COALESCE(SUM(i.total), 0)                                   AS total_sales,
            COALESCE(SUM(i.paid), 0)                                    AS total_collected,
            COALESCE(SUM(i.total - i.paid), 0)                         AS total_debts,

            -- ربح
            COALESCE(SUM(ii.profit), 0)                                 AS total_profit,

            -- مخزون معه
            COALESCE(ms.stock_qty, 0)                                   AS stock_quantity,

            -- طلبات سحب
            COALESCE(cr_pending.cnt, 0)                                 AS pending_cash_requests,
            COALESCE(cr_approved.total_approved, 0)                     AS total_approved_cash,

            -- صافي الرصيد (ما قبضه - ما سُحب)
            (
                COALESCE(SUM(i.paid), 0) -
                COALESCE(cr_approved.total_approved, 0)
            )                                                           AS net_balance

        FROM users u

        LEFT JOIN clients c
            ON c.mandoob_id = u.id

        LEFT JOIN invoices i
            ON i.mandoob_id = u.id

        LEFT JOIN invoice_items ii
            ON ii.invoice_id = i.id

        -- مخزون المندوب (subquery لتجنب تضخيم الصفوف)
        LEFT JOIN (
            SELECT mandoob_id, SUM(quantity) AS stock_qty
            FROM mandoob_stock
            GROUP BY mandoob_id
        ) ms ON ms.mandoob_id = u.id

        -- طلبات معلقة
        LEFT JOIN (
            SELECT mandoob_id, COUNT(*) AS cnt
            FROM cash_requests
            WHERE status = 'pending'
            GROUP BY mandoob_id
        ) cr_pending ON cr_pending.mandoob_id = u.id

        -- إجمالي المسحوب المعتمد
        LEFT JOIN (
            SELECT mandoob_id, SUM(amount) AS total_approved
            FROM cash_requests
            WHERE status = 'approved'
            GROUP BY mandoob_id
        ) cr_approved ON cr_approved.mandoob_id = u.id

        WHERE u.role = 'mandoob'

        GROUP BY
            u.id, u.name, u.phone, u.status,
            ms.stock_qty,
            cr_pending.cnt,
            cr_approved.total_approved

        ORDER BY total_sales DESC
    """;
    return query(sql);
}
    // ================= Helpers =================
    private double toDouble(List<Map<String, Object>> result, String col) {
        if (result.isEmpty() || result.get(0).get(col) == null) return 0;
        return ((Number) result.get(0).get(col)).doubleValue();
    }

    private int toInt(List<Map<String, Object>> result, String col) {
        if (result.isEmpty() || result.get(0).get(col) == null) return 0;
        return ((Number) result.get(0).get(col)).intValue();
    }
}