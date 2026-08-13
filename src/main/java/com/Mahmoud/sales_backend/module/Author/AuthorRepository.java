package com.Mahmoud.sales_backend.module.Author;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.Author;

@Repository
public class AuthorRepository extends BaseRepository {

    public AuthorRepository(Connection conn) {
        super(conn);
    }

    // ================= GET ALL =================
    public List<Author> getAll() {
        List<Map<String, Object>> result =
                query("SELECT * FROM authors ORDER BY name ASC");

        List<Author> authors = new ArrayList<>();
        for (Map<String, Object> row : result) {
            authors.add(mapToAuthor(row));
        }
        return authors;
    }

    // ================= GET BY ID =================
    public Author getById(int id) {
        List<Map<String, Object>> result = query("SELECT * FROM authors WHERE id = ?", id);
        if (result.isEmpty())
            return null;
        return mapToAuthor(result.get(0));
    }

    // ================= GET COUNT =================
    public int getCount() {
        return super.count("authors");
    }

    // ================= GET BOOKS BY AUTHOR =================
    public List<Map<String, Object>> getBooksByAuthor(int authorId) {
        return query("""
            SELECT id, kind, title, category, price, cover, description, quantity
            FROM products
            WHERE author_id = ?
            ORDER BY created_at DESC
            """, authorId);
    }

    // ================= CREATE =================
    public boolean create(Author author) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", author.getName());
            data.put("type", author.getType());
            data.put("country", author.getCountry());
            data.put("bio", author.getBio());
            data.put("joined", author.getJoined());

            return insert("authors", data) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(Author author) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", author.getName());
            data.put("type", author.getType());
            data.put("country", author.getCountry());
            data.put("bio", author.getBio());
            data.put("joined", author.getJoined());

            return update("authors", data, "id", author.getId()) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {
        try {
            return delete("authors", "id", id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= MAPPER =================
    private Author mapToAuthor(Map<String, Object> row) {
        Author author = new Author();

        author.setId(((Number) row.get("id")).intValue());
        author.setName((String) row.get("name"));
        author.setType((String) row.get("type"));
        author.setCountry((String) row.get("country"));
        author.setBio((String) row.get("bio"));
        author.setJoined((String) row.get("joined"));
        author.setCreatedAt((Date) row.get("created_at"));
        author.setUpdatedAt((Date) row.get("updated_at"));

        return author;
    }
}
