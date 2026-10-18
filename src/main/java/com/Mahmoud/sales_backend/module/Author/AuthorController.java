package com.Mahmoud.sales_backend.module.Author;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.Author;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // GET /authors
    @GetMapping
    public ApiResponse getAll() {
        return authorService.getAll();
    }

    // GET /authors/{id}
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable int id) {
        return authorService.getById(id);
    }

    // GET /authors/{id}/books
    @GetMapping("/{id}/books")
    public ApiResponse getBooks(@PathVariable int id) {
        return authorService.getBooks(id);
    }

    // GET /authors/count
    @GetMapping("/count")
    public ApiResponse getCount() {
        return authorService.getCount();
    }

    // POST /authors
    @PostMapping
    public ApiResponse create(@RequestBody Author author) {
        return authorService.create(author);
    }

    // PUT /authors/{id}
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable int id, @RequestBody Author author) {
        if (id <= 0)
            return ApiResponse.error("Invalid author id");
        author.setId(id);
        return authorService.update(author);
    }

    // DELETE /authors/{id}
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable int id) {
        return authorService.delete(id);
    }
}
