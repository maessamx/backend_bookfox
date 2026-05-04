package com.Mahmoud.sales_backend.module.Payment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.Payment;

@Repository
public class PaymentRepository extends BaseRepository {

    public PaymentRepository(Connection conn) {
        super(conn);
    }

    // ================= Get All =================
    public List<Payment> getAll() {
        String sql = """
                    SELECT
                        p.*,
                        u.name AS mandoob_name
                        u.id AS mandoob_id
                        c.name AS client_name
                        c.id AS client_id
                    FROM payments p
                    LEFT JOIN invoices   i ON i.id = p.invoice_id
                    LEFT JOIN users   u ON u.id = i.mandoob_id
                    LEFT JOIN clients c ON c.id = i.client_id
                """;
        List<Map<String, Object>> result = query(sql);

        List<Payment> payments = new ArrayList<>();
        for (Map<String, Object> row : result) {
            payments.add(mapToPayment(row));
        }

        return payments;
    }

    public List<Payment> getPaymentsByMandoob(int mandoobid) {
        String sql = """
                    SELECT
                        p.*,
                        c.name AS client_name
                        c.id AS client_id
                    FROM payments p
                    LEFT JOIN invoices   i ON i.id = p.invoice_id
                    LEFT JOIN clients c ON c.id = i.client_id
                    WHERE i.mandoob_id = ?
                """;
        List<Map<String, Object>> result = query(sql, mandoobid);

        List<Payment> payments = new ArrayList<>();
        for (Map<String, Object> row : result) {
            payments.add(mapToPayment(row));
        }

        return payments;
    }

    /*
     * 
     * // ================= Get By ID =================
     * public Product getById(int id) {
     * List<Map<String, Object>> result =
     * query("SELECT * FROM products WHERE id = ?", id);
     * 
     * if (result.isEmpty()) return null;
     * 
     * return mapToProduct(result.get(0));
     * }
     * 
     * // ================= Get Count =================
     * public int getCount() {
     * return super.count("products");
     * }
     * 
     * // ================= Count Low Stock =================
     * public int countLowStock() {
     * List<Map<String, Object>> result =
     * query("SELECT COUNT(*) AS cnt FROM products WHERE quantity <= min_stock");
     * 
     * if (result.isEmpty()) return 0;
     * 
     * Object cnt = result.get(0).get("cnt");
     * return cnt != null ? ((Number) cnt).intValue() : 0;
     * }
     * 
     * // ================= Create =================
     * public boolean create(Product product) {
     * try {
     * Map<String, Object> data = new LinkedHashMap<>();
     * data.put("name", product.getName());
     * data.put("price_cost", product.getPricecost());
     * data.put("sell_price", product.getSellprice());
     * data.put("quantity", product.getQuantity());
     * data.put("min_stock", product.getMinStock());
     * 
     * return insert("products", data) > 0;
     * } catch (Exception e) {
     * System.out.println("DB ERROR: " + e.getMessage());
     * return false;
     * }
     * }
     * 
     * // ================= Update =================
     * public boolean update(Product product) {
     * try {
     * Map<String, Object> data = new LinkedHashMap<>();
     * data.put("name", product.getName());
     * data.put("price_cost", product.getPricecost());
     * data.put("sell_price", product.getSellprice());
     * data.put("quantity", product.getQuantity());
     * data.put("min_stock", product.getMinStock());
     * 
     * return update("products", data, "id", product.getId()) > 0;
     * } catch (Exception e) {
     * System.out.println("DB ERROR: " + e.getMessage());
     * return false;
     * }
     * }
     * 
     * // ================= Delete =================
     * public boolean delete(int id) {
     * try {
     * return delete("products", "id", id) > 0;
     * } catch (Exception e) {
     * e.printStackTrace();
     * return false;
     * }
     * }
     * 
     * // ================= Increase Quantity =================
     * public boolean increaseQuantity(int id, int qty) {
     * String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
     * try (PreparedStatement stmt = conn.prepareStatement(sql)) {
     * stmt.setInt(1, qty);
     * stmt.setInt(2, id);
     * return stmt.executeUpdate() > 0;
     * } catch (SQLException e) {
     * e.printStackTrace();
     * return false;
     * }
     * }
     */
    // ================= Mapper =================
    private Payment mapToPayment(Map<String, Object> row) {
        Payment product = new Payment();
        product.setId(((Number) row.get("id")).intValue());
        product.setClientid(((Number) row.get("client_id")).intValue());
        product.setMandoobid(((Number) row.get("mandoob_id")).intValue());
        product.setInvoiceId(((Number) row.get("invoice_id")).intValue());
        product.setAmount(((Number) row.get("amount")).doubleValue());
        product.setNotes((String) row.get("notes"));
        product.setClientname((String) row.get("client_name"));
        product.setMandoobname((String) row.get("mandoob_name"));
        product.setCreatedAt((Date) row.get("created_at"));
        return product;
    }
}