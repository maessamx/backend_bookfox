package com.Mahmoud.sales_backend.module.Order;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.Order;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /orders
    @GetMapping
    public ApiResponse getAll() {
        return orderService.getAll();
    }

    // GET /orders/{id}
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable int id) {
        return orderService.getById(id);
    }

    // GET /orders/{id}/items
    @GetMapping("/{id}/items")
    public ApiResponse getWithItems(@PathVariable int id) {
        return orderService.getWithItems(id);
    }

    // GET /orders/user/{userId}
    @GetMapping("/user/{userId}")
    public ApiResponse getByUser(@PathVariable int userId) {
        return orderService.getByUser(userId);
    }

    // GET /orders/count
    @GetMapping("/count")
    public ApiResponse getCount() {
        return orderService.getCount();
    }

    // POST /orders
    @PostMapping
    public ApiResponse create(@RequestBody Order order) {
        return orderService.create(order);
    }

    // PATCH /orders/{id}/status?status=shipped
    @PatchMapping("/{id}/status")
    public ApiResponse changeStatus(@PathVariable int id, @RequestParam String status) {
        return orderService.changeStatus(id, status);
    }

    // DELETE /orders/{id}
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable int id) {
        return orderService.delete(id);
    }
}
