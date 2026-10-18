package com.Mahmoud.sales_backend.module.Supply;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.Product;

@Repository
public class SupplyRepository extends BaseRepository {

    public SupplyRepository(Connection conn) {
        super(conn);
    }

    // ================= GET ALL =================
    public List<Product> getAll() {
        List<Map<String, Object>> result = query("""
            SELECT p.*, a.name AS author_name
            FROM products p
            LEFT JOIN authors a ON a.id = p.author_id
            WHERE p.kind = 'supply'
            ORDER BY p.created_at DESC
            """);

        List<Product> supplies = new ArrayList<>();
        for (Map<String, Object> row : result) {
            supplies.add(mapToProduct(row));
        }
        return supplies;
    }

    // ================= GET BY ID =================
    public Product getById(int id) {
        List<Map<String, Object>> result = query("""
            SELECT p.*, a.name AS author_name
            FROM products p
            LEFT JOIN authors a ON a.id = p.author_id
            WHERE p.id = ? AND p.kind = 'supply'
            """, id);

        if (result.isEmpty())
            return null;

        return mapToProduct(result.get(0));
    }

    // ================= GET COUNT =================
    public int getCount() {
        List<Map<String, Object>> result = query(
                "SELECT COUNT(*) AS cnt FROM products WHERE kind = 'supply'");
        if (result.isEmpty())
            return 0;
        Object cnt = result.get(0).get("cnt");
        return cnt != null ? ((Number) cnt).intValue() : 0;
    }

    // ================= CREATE =================
    public boolean create(Product supply) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kind", "supply");
            data.put("title", supply.getTitle());
            data.put("category", supply.getCategory());
            data.put("price", supply.getPrice());
            data.put("description", supply.getDescription());
            data.put("quantity", supply.getQuantity());
            data.put("min_stock", supply.getMinStock());

            return insert("products", data) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(Product supply) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("title", supply.getTitle());
            data.put("category", supply.getCategory());
            data.put("price", supply.getPrice());
            data.put("description", supply.getDescription());
            data.put("quantity", supply.getQuantity());
            data.put("min_stock", supply.getMinStock());

            return update("products", data, "id", supply.getId()) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {
        try {
            return delete("products", "id", id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= MAPPER =================
    private Product mapToProduct(Map<String, Object> row) {
        Product product = new Product();

        product.setId(toInt(row.get("id")));
        product.setKind((String) row.get("kind"));
        product.setTitle((String) row.get("title"));
        Object authorId = row.get("author_id");
        product.setAuthorId(authorId != null ? ((Number) authorId).intValue() : 0);
        product.setAuthorName((String) row.get("author_name"));
        product.setAuthor((String) row.get("author_name"));
        product.setCategory((String) row.get("category"));
        product.setPrice(toDouble(row.get("price")));
        product.setCover((String) row.get("cover"));
        product.setDescription((String) row.get("description"));
        product.setQuantity(toInt(row.get("quantity")));
        product.setMinStock(toInt(row.get("min_stock")));
        product.setCreatedAt((Date) row.get("created_at"));
        product.setUpdatedAt((Date) row.get("updated_at"));

        return product;
    }
}
