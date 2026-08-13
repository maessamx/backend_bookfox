package com.Mahmoud.sales_backend.module.Book;

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
public class BookRepository extends BaseRepository {

    public BookRepository(Connection conn) {
        super(conn);
    }

    // ================= GET ALL =================
    public List<Product> getAll() {
        List<Map<String, Object>> result = query("""
            SELECT p.*, a.name AS author_name
            FROM products p
            LEFT JOIN authors a ON a.id = p.author_id
            WHERE p.kind = 'book'
            ORDER BY p.created_at DESC
            """);

        List<Product> books = new ArrayList<>();
        for (Map<String, Object> row : result) {
            books.add(mapToProduct(row));
        }
        return books;
    }

    // ================= GET BY ID =================
    public Product getById(int id) {
        List<Map<String, Object>> result = query("""
            SELECT p.*, a.name AS author_name
            FROM products p
            LEFT JOIN authors a ON a.id = p.author_id
            WHERE p.id = ? AND p.kind = 'book'
            """, id);

        if (result.isEmpty())
            return null;

        return mapToProduct(result.get(0));
    }

    // ================= GET COUNT =================
    public int getCount() {
        List<Map<String, Object>> result = query(
                "SELECT COUNT(*) AS cnt FROM products WHERE kind = 'book'");
        if (result.isEmpty())
            return 0;
        Object cnt = result.get(0).get("cnt");
        return cnt != null ? ((Number) cnt).intValue() : 0;
    }

    // ================= GET BY AUTHOR =================
    public List<Map<String, Object>> getByAuthor(int authorId) {
        return query("""
            SELECT id, kind, title, category, price, cover, description, quantity
            FROM products
            WHERE kind = 'book' AND author_id = ?
            """, authorId);
    }

    // ================= CREATE =================
    public boolean create(Product book) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kind", "book");
            data.put("title", book.getTitle());
            data.put("author_id", book.getAuthorId() > 0 ? book.getAuthorId() : null);
            data.put("category", book.getCategory());
            data.put("price", book.getPrice());
            data.put("cover", book.getCover());
            data.put("description", book.getDescription());
            data.put("quantity", book.getQuantity());
            data.put("min_stock", book.getMinStock());

            return insert("products", data) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(Product book) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("title", book.getTitle());
            data.put("author_id", book.getAuthorId() > 0 ? book.getAuthorId() : null);
            data.put("category", book.getCategory());
            data.put("price", book.getPrice());
            data.put("cover", book.getCover());
            data.put("description", book.getDescription());
            data.put("quantity", book.getQuantity());
            data.put("min_stock", book.getMinStock());

            return update("products", data, "id", book.getId()) > 0;
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

        product.setId(((Number) row.get("id")).intValue());
        product.setKind((String) row.get("kind"));
        product.setTitle((String) row.get("title"));
        Object authorId = row.get("author_id");
        product.setAuthorId(authorId != null ? ((Number) authorId).intValue() : 0);
        product.setAuthorName((String) row.get("author_name"));
        product.setCategory((String) row.get("category"));
        product.setPrice(((Number) row.get("price")).doubleValue());
        product.setCover((String) row.get("cover"));
        product.setDescription((String) row.get("description"));
        product.setQuantity(((Number) row.get("quantity")).intValue());
        product.setMinStock(((Number) row.get("min_stock")).intValue());
        product.setCreatedAt((Date) row.get("created_at"));
        product.setUpdatedAt((Date) row.get("updated_at"));

        return product;
    }
}
