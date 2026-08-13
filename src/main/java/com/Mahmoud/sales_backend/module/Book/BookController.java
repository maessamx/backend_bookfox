package com.Mahmoud.sales_backend.module.Book;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.Product;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /books
    @GetMapping
    public ApiResponse getAll() {
        return bookService.getAll();
    }

    // GET /books/{id}
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable int id) {
        return bookService.getById(id);
    }

    // GET /books/count
    @GetMapping("/count")
    public ApiResponse getCount() {
        return bookService.getCount();
    }

    // GET /books/author/{authorId}
    @GetMapping("/author/{authorId}")
    public ApiResponse getByAuthor(@PathVariable int authorId) {
        return bookService.getByAuthor(authorId);
    }

    // POST /books
    @PostMapping
    public ApiResponse create(@RequestBody Product book) {
        book.setKind("book");
        return bookService.create(book);
    }

    // PUT /books/{id}
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable int id, @RequestBody Product book) {
        if (id <= 0)
            return ApiResponse.error("Invalid book id");
        book.setId(id);
        book.setKind("book");
        return bookService.update(book);
    }

    // DELETE /books/{id}
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable int id) {
        return bookService.delete(id);
    }
}
