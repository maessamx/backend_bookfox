package com.Mahmoud.sales_backend.module.Author;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Author;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // ================= GET ALL =================
    public ApiResponse getAll() {
        List<Author> authors = authorRepository.getAll();
        return ApiResponse.success(authors);
    }

    // ================= GET BY ID =================
    public ApiResponse getById(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        Author author = authorRepository.getById(id);
        if (author == null)
            return ApiResponse.error("Author not found");

        return ApiResponse.success(author);
    }

    // ================= GET BOOKS BY AUTHOR =================
    public ApiResponse getBooks(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        List<Map<String, Object>> books = authorRepository.getBooksByAuthor(id);

        Map<String, Object> response = new HashMap<>();
        response.put("author", authorRepository.getById(id));
        response.put("books", books);
        response.put("count", books.size());

        return ApiResponse.success(response);
    }

    // ================= GET COUNT =================
    public ApiResponse getCount() {
        return ApiResponse.success(authorRepository.getCount());
    }

    // ================= CREATE (admin) =================
    public ApiResponse create(Author author) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (author.getName() == null || author.getName().isBlank())
            return ApiResponse.error("Name is required");

        if (author.getType() == null || author.getType().isBlank())
            author.setType("author");

        boolean created = authorRepository.create(author);
        if (created)
            return ApiResponse.success("Author created successfully");

        return ApiResponse.error("Failed to create author");
    }

    // ================= UPDATE (admin) =================
    public ApiResponse update(Author author) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (author.getId() <= 0)
            return ApiResponse.error("Invalid ID");
        if (author.getName() == null || author.getName().isBlank())
            return ApiResponse.error("Name is required");

        Author existing = authorRepository.getById(author.getId());
        if (existing == null)
            return ApiResponse.error("Author not found");

        boolean updated = authorRepository.update(author);
        if (updated)
            return ApiResponse.success("Author updated successfully");

        return ApiResponse.error("Failed to update author");
    }

    // ================= DELETE (admin) =================
    public ApiResponse delete(int id) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean deleted = authorRepository.delete(id);
        if (deleted)
            return ApiResponse.success("Author deleted successfully");

        return ApiResponse.error("Delete failed");
    }

    private boolean isAdmin() {
        return "admin".equals(UserContext.getRole());
    }
}
