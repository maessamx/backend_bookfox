package com.Mahmoud.sales_backend.module.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.Order;
import com.Mahmoud.sales_backend.model.OrderItem;

@Repository
public class OrderRepository extends BaseRepository {

    public OrderRepository(Connection conn) {
        super(conn);
    }

    // ================= GET ALL =================
    public List<Order> getAll() {
        List<Map<String, Object>> result = query("""
            SELECT o.*, u.name AS user_name
            FROM orders o
            LEFT JOIN users u ON u.id = o.user_id
            ORDER BY o.created_at DESC
            """);

        List<Order> orders = new ArrayList<>();
        for (Map<String, Object> row : result) {
            orders.add(mapToOrder(row));
        }
        return orders;
    }

    // ================= GET BY ID =================
    public Order getById(int id) {
        List<Map<String, Object>> result = query("""
            SELECT o.*, u.name AS user_name
            FROM orders o
            LEFT JOIN users u ON u.id = o.user_id
            WHERE o.id = ?
            """, id);

        if (result.isEmpty())
            return null;

        return mapToOrder(result.get(0));
    }

    // ================= GET BY USER =================
    public List<Order> getOrdersByUser(int userId) {
        List<Map<String, Object>> result = query("""
            SELECT o.*, u.name AS user_name
            FROM orders o
            LEFT JOIN users u ON u.id = o.user_id
            WHERE o.user_id = ?
            ORDER BY o.created_at DESC
            """, userId);

        List<Order> orders = new ArrayList<>();
        for (Map<String, Object> row : result) {
            orders.add(mapToOrder(row));
        }
        return orders;
    }

    // ================= GET COUNT =================
    public int getCount() {
        return super.count("orders");
    }

    // ================= GET ORDER ITEMS =================
    public List<OrderItem> getOrderItems(int orderId) {
        List<Map<String, Object>> result =
                query("SELECT * FROM order_items WHERE order_id = ?", orderId);

        List<OrderItem> items = new ArrayList<>();
        for (Map<String, Object> row : result) {
            items.add(mapToOrderItem(row));
        }
        return items;
    }

    // ================= CREATE ORDER + ITEMS =================
    public Order createWithItems(Order order) {
        Connection conn = this.conn;

        try {
            conn.setAutoCommit(false);

            int orderId = insertOrder(conn, order);
            if (orderId <= 0) {
                conn.rollback();
                return null;
            }

            for (OrderItem item : order.getItems()) {
                boolean ok = insertOrderItem(conn, orderId, item);
                if (!ok) {
                    conn.rollback();
                    return null;
                }
            }

            conn.commit();
            return getById(orderId);

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int insertOrder(Connection conn, Order order) throws SQLException {
        String sql = """
            INSERT INTO orders (order_code, user_id, customer, email,
                subtotal, shipping, total, status, governorate, city, address, notes, payment)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String code = order.getOrderCode();
            if (code == null || code.isBlank()) {
                code = "ORD-" + System.currentTimeMillis() % 1000000;
            }

            stmt.setString(1, code);
            stmt.setObject(2, order.getUserId() > 0 ? order.getUserId() : null);
            stmt.setString(3, order.getCustomer());
            stmt.setString(4, order.getEmail());
            stmt.setDouble(5, order.getSubtotal());
            stmt.setDouble(6, order.getShipping());
            stmt.setDouble(7, order.getTotal());
            stmt.setString(8, order.getStatus());
            stmt.setString(9, order.getGovernorate());
            stmt.setString(10, order.getCity());
            stmt.setString(11, order.getAddress());
            stmt.setString(12, order.getNotes());
            stmt.setString(13, order.getPayment());

            int rows = stmt.executeUpdate();
            if (rows == 0)
                return 0;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next())
                    return keys.getInt(1);
            }
            return 0;
        }
    }

    private boolean insertOrderItem(Connection conn, int orderId, OrderItem item) throws SQLException {
        String sql = """
            INSERT INTO order_items (order_id, product_id, title, price, quantity, kind, total)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setObject(2, item.getProductId() > 0 ? item.getProductId() : null);
            stmt.setString(3, item.getTitle());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getQuantity());
            stmt.setString(6, item.getKind() == null ? "book" : item.getKind());
            stmt.setDouble(7, item.getPrice() * item.getQuantity());

            return stmt.executeUpdate() > 0;
        }
    }

    // ================= CHANGE STATUS =================
    public boolean changeStatus(int id, String status) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("status", status);
            return update("orders", data, "id", id) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {
        try {
            return delete("orders", "id", id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= MAPPER =================
    private Order mapToOrder(Map<String, Object> row) {
        Order order = new Order();

        order.setId(((Number) row.get("id")).intValue());
        order.setOrderCode((String) row.get("order_code"));
        Object userId = row.get("user_id");
        order.setUserId(userId != null ? ((Number) userId).intValue() : 0);
        order.setCustomer((String) row.get("customer"));
        order.setEmail((String) row.get("email"));
        order.setSubtotal(((Number) row.get("subtotal")).doubleValue());
        order.setShipping(((Number) row.get("shipping")).doubleValue());
        order.setTotal(((Number) row.get("total")).doubleValue());
        order.setStatus((String) row.get("status"));
        order.setGovernorate((String) row.get("governorate"));
        order.setCity((String) row.get("city"));
        order.setAddress((String) row.get("address"));
        order.setNotes((String) row.get("notes"));
        order.setPayment((String) row.get("payment"));
        order.setCreatedAt((Date) row.get("created_at"));

        return order;
    }

    private OrderItem mapToOrderItem(Map<String, Object> row) {
        OrderItem item = new OrderItem();

        item.setId(((Number) row.get("id")).intValue());
        item.setOrderId(((Number) row.get("order_id")).intValue());
        Object productId = row.get("product_id");
        item.setProductId(productId != null ? ((Number) productId).intValue() : 0);
        item.setTitle((String) row.get("title"));
        item.setPrice(((Number) row.get("price")).doubleValue());
        item.setQuantity(((Number) row.get("quantity")).intValue());
        item.setKind((String) row.get("kind"));
        item.setTotal(((Number) row.get("total")).doubleValue());

        return item;
    }
}
