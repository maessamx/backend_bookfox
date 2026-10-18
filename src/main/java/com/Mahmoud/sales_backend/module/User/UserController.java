package com.Mahmoud.sales_backend.module.User;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users
    @GetMapping
    public ApiResponse getAll() {
        return userService.getAll();
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // GET /users/count
    @GetMapping("/count")
    public ApiResponse getCount() {
        return userService.getCount();
    }

    // GET /users/me/profile
    @GetMapping("/me/profile")
    public ApiResponse getMyProfile() {
        return userService.getMyProfile();
    }

    // POST /users
    @PostMapping
    public ApiResponse create(@RequestBody User user) {
        return userService.create(user);
    }

    // PUT /users/{id}
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable int id, @RequestBody User user) {
        if (id <= 0)
            return ApiResponse.error("Invalid user id");
        user.setId(id);
        return userService.update(user);
    }

    // PATCH /users/{id}/role
    @PatchMapping("/{id}/role")
    public ApiResponse changeRole(@PathVariable int id, @RequestParam String role) {
        return userService.changeRole(id, role);
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable int id) {
        return userService.delete(id);
    }
}
