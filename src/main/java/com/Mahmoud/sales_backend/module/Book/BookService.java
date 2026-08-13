package com.Mahmoud.sales_backend.module.Book;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Author;
import com.Mahmoud.sales_backend.model.Product;
import com.Mahmoud.sales_backend.module.Author.AuthorRepository;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // ================= GET ALL =================
    public ApiResponse getAll() {
        List<Product> books = bookRepository.getAll();
        return ApiResponse.success(books);
    }

    // ================= GET BY ID =================
    public ApiResponse getById(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        Product book = bookRepository.getById(id);
        if (book == null)
            return ApiResponse.error("Book not found");

        return ApiResponse.success(book);
    }

    // ================= GET COUNT =================
    public ApiResponse getCount() {
        return ApiResponse.success(bookRepository.getCount());
    }

    // ================= GET BY AUTHOR =================
    public ApiResponse getByAuthor(int authorId) {
        if (authorId <= 0)
            return ApiResponse.error("Invalid author ID");

        List<Map<String, Object>> books = bookRepository.getByAuthor(authorId);
        return ApiResponse.success(books);
    }

    // ================= CREATE (admin) =================
    public ApiResponse create(Product book) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (book.getTitle() == null || book.getTitle().isBlank())
            return ApiResponse.error("Title is required");
        if (book.getPrice() <= 0)
            return ApiResponse.error("Price must be greater than 0");

        resolveAuthor(book);
        boolean created = bookRepository.create(book);
        if (created)
            return ApiResponse.success("Book created successfully");

        return ApiResponse.error("Failed to create book");
    }

    // ================= UPDATE (admin) =================
    public ApiResponse update(Product book) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (book.getId() <= 0)
            return ApiResponse.error("Invalid ID");
        if (book.getTitle() == null || book.getTitle().isBlank())
            return ApiResponse.error("Title is required");
        if (book.getPrice() <= 0)
            return ApiResponse.error("Price must be greater than 0");

        Product existing = bookRepository.getById(book.getId());
        if (existing == null)
            return ApiResponse.error("Book not found");

        resolveAuthor(book);
        boolean updated = bookRepository.update(book);
        if (updated)
            return ApiResponse.success("Book updated successfully");

        return ApiResponse.error("Failed to update book");
    }

    // ================= DELETE (admin) =================
    public ApiResponse delete(int id) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean deleted = bookRepository.delete(id);
        if (deleted)
            return ApiResponse.success("Book deleted successfully");

        return ApiResponse.error("Delete failed");
    }

    private void resolveAuthor(Product book) {
        if (book.getAuthorId() > 0) {
            return;
        }
        String name = book.getAuthor();
        if (name == null || name.isBlank()) {
            return;
        }
        Author author = authorRepository.findByName(name.trim());
        if (author != null) {
            book.setAuthorId(author.getId());
        }
    }

    private boolean isAdmin() {
        return "admin".equals(UserContext.getRole());
    }
}
