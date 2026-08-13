package com.Mahmoud.sales_backend.module.Supply;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Product;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class SupplyService {

    private final SupplyRepository supplyRepository;

    public SupplyService(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    // ================= GET ALL =================
    public ApiResponse getAll() {
        List<Product> supplies = supplyRepository.getAll();
        return ApiResponse.success(supplies);
    }

    // ================= GET BY ID =================
    public ApiResponse getById(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        Product supply = supplyRepository.getById(id);
        if (supply == null)
            return ApiResponse.error("Supply not found");

        return ApiResponse.success(supply);
    }

    // ================= GET COUNT =================
    public ApiResponse getCount() {
        return ApiResponse.success(supplyRepository.getCount());
    }

    // ================= CREATE (admin) =================
    public ApiResponse create(Product supply) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (supply.getTitle() == null || supply.getTitle().isBlank())
            return ApiResponse.error("Title is required");
        if (supply.getPrice() <= 0)
            return ApiResponse.error("Price must be greater than 0");

        boolean created = supplyRepository.create(supply);
        if (created)
            return ApiResponse.success("Supply created successfully");

        return ApiResponse.error("Failed to create supply");
    }

    // ================= UPDATE (admin) =================
    public ApiResponse update(Product supply) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (supply.getId() <= 0)
            return ApiResponse.error("Invalid ID");
        if (supply.getTitle() == null || supply.getTitle().isBlank())
            return ApiResponse.error("Title is required");

        Product existing = supplyRepository.getById(supply.getId());
        if (existing == null)
            return ApiResponse.error("Supply not found");

        boolean updated = supplyRepository.update(supply);
        if (updated)
            return ApiResponse.success("Supply updated successfully");

        return ApiResponse.error("Failed to update supply");
    }

    // ================= DELETE (admin) =================
    public ApiResponse delete(int id) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean deleted = supplyRepository.delete(id);
        if (deleted)
            return ApiResponse.success("Supply deleted successfully");

        return ApiResponse.error("Delete failed");
    }

    private boolean isAdmin() {
        return "admin".equals(UserContext.getRole());
    }
}
