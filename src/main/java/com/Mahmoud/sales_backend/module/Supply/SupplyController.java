package com.Mahmoud.sales_backend.module.Supply;

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
@RequestMapping("/supplies")
public class SupplyController {

    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    // GET /supplies
    @GetMapping
    public ApiResponse getAll() {
        return supplyService.getAll();
    }

    // GET /supplies/{id}
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable int id) {
        return supplyService.getById(id);
    }

    // GET /supplies/count
    @GetMapping("/count")
    public ApiResponse getCount() {
        return supplyService.getCount();
    }

    // POST /supplies
    @PostMapping
    public ApiResponse create(@RequestBody Product supply) {
        supply.setKind("supply");
        return supplyService.create(supply);
    }

    // PUT /supplies/{id}
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable int id, @RequestBody Product supply) {
        if (id <= 0)
            return ApiResponse.error("Invalid supply id");
        supply.setId(id);
        supply.setKind("supply");
        return supplyService.update(supply);
    }

    // DELETE /supplies/{id}
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable int id) {
        return supplyService.delete(id);
    }
}
