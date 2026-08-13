package com.Mahmoud.sales_backend.module.Order;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Order;
import com.Mahmoud.sales_backend.model.OrderItem;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ================= GET ALL (admin) =================
    public ApiResponse getAll() {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");

        List<Order> orders = orderRepository.getAll();
        return ApiResponse.success(orders);
    }

    // ================= GET BY ID =================
    public ApiResponse getById(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        Order order = orderRepository.getById(id);
        if (order == null)
            return ApiResponse.error("Order not found");

        return ApiResponse.success(order);
    }

    // ================= GET WITH ITEMS =================
    public ApiResponse getWithItems(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        Order order = orderRepository.getById(id);
        if (order == null)
            return ApiResponse.error("Order not found");

        List<OrderItem> items = orderRepository.getOrderItems(id);
        order.setItems(items);

        return ApiResponse.success(order);
    }

    // ================= GET BY USER =================
    public ApiResponse getByUser(int userId) {
        if (userId <= 0)
            return ApiResponse.error("Invalid user ID");

        List<Order> orders = orderRepository.getOrdersByUser(userId);
        return ApiResponse.success(orders);
    }

    // ================= GET COUNT =================
    public ApiResponse getCount() {
        return ApiResponse.success(orderRepository.getCount());
    }

    // ================= CREATE =================
    public ApiResponse create(Order order) {
        if (order.getCustomer() == null || order.getCustomer().isBlank())
            return ApiResponse.error("Customer name is required");

        if (order.getItems() == null || order.getItems().isEmpty())
            return ApiResponse.error("Order must have at least one item");

        if (order.getStatus() == null || order.getStatus().isBlank())
            order.setStatus("pending");

        Order created = orderRepository.createWithItems(order);
        if (created == null)
            return ApiResponse.error("Failed to create order");

        return ApiResponse.success(created);
    }

    // ================= CHANGE STATUS (admin) =================
    public ApiResponse changeStatus(int id, String status) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean valid = status.equals("pending") || status.equals("processing")
                || status.equals("shipped") || status.equals("delivered")
                || status.equals("cancelled");
        if (!valid)
            return ApiResponse.error("Invalid status");

        Order existing = orderRepository.getById(id);
        if (existing == null)
            return ApiResponse.error("Order not found");

        boolean updated = orderRepository.changeStatus(id, status);
        if (updated)
            return ApiResponse.success("Order status updated successfully");

        return ApiResponse.error("Failed to update status");
    }

    // ================= DELETE (admin) =================
    public ApiResponse delete(int id) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean deleted = orderRepository.delete(id);
        if (deleted)
            return ApiResponse.success("Order deleted successfully");

        return ApiResponse.error("Delete failed");
    }

    private boolean isAdmin() {
        return "admin".equals(UserContext.getRole());
    }
}
