package com.Mahmoud.sales_backend.module.MandoobStock;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.MandoobStock;
import com.Mahmoud.sales_backend.model.Product;
import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.module.Mandoob.MandoobRepository;
import com.Mahmoud.sales_backend.module.Product.ProductRepository;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class MandoobStockService {

    private final MandoobStockRepository mandoobstockRepository;
    private final ProductRepository productRepository;
    private final MandoobRepository mandoobRepository;

    public MandoobStockService(MandoobStockRepository mandoobstockRepository, MandoobRepository mandoobRepository,
            ProductRepository productRepository) {
        this.mandoobstockRepository = mandoobstockRepository;
        this.productRepository = productRepository;
        this.mandoobRepository = mandoobRepository;
    }

    // ================= GetAll =================
    public ApiResponse getAll() {
        Integer userId = UserContext.getUserId();
        String role = UserContext.getRole();

        List<MandoobStock> stock;

        if ("admin".equals(role)) {
            stock = mandoobstockRepository.getAll();
        } else {
            stock = mandoobstockRepository.getByMandoob(userId);
        }

        return ApiResponse.success(stock);
    }

    // ================= GetByMandoob =================
    public ApiResponse getByMandoob(int mandoobId) {
        if (mandoobId <= 0) {
            return ApiResponse.error("Invalid mandoob ID");
        }
        String role = UserContext.getRole();
        if (!"admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        List<MandoobStock> stock = mandoobstockRepository.getByMandoob(mandoobId);

        return ApiResponse.success(stock);
    }

    // ================= Create =================
    public ApiResponse add(MandoobStock mandoobStock) {

        if (mandoobStock.getMandoobId() <= 0)
            return ApiResponse.error("Invalid mandoob ID");

        if (mandoobStock.getProductid() <= 0)
            return ApiResponse.error("Invalid product ID");

        if (mandoobStock.getQuantity() <= 0)
            return ApiResponse.error("Invalid  qty");
        String role = UserContext.getRole();
        if (!"admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        User mandoob = mandoobRepository.getById(mandoobStock.getMandoobId());
        if (mandoob == null)
            return ApiResponse.error("Mandoob not found");

        Product product = productRepository.getById(mandoobStock.getProductid());
        if (product == null)
            return ApiResponse.error("Product not found");

        if (product.getQuantity() < mandoobStock.getQuantity()) {
            return ApiResponse.error(
                    "Quantity exceeds available stock. Available quantity: "
                            + product.getQuantity());
        }

        boolean isCreated = mandoobstockRepository.add(mandoobStock);

        if (!isCreated)
            return ApiResponse.error("Failed to add stokcs");

        return ApiResponse.success("added successfully");
    }

    // ================= Create =================
    public ApiResponse reduce(MandoobStock mandoobStock) {

        if (mandoobStock.getMandoobId() <= 0)
            return ApiResponse.error("Invalid mandoob ID");

        if (mandoobStock.getProductid() <= 0)
            return ApiResponse.error("Invalid product ID");

        if (mandoobStock.getQuantity() <= 0)
            return ApiResponse.error("Invalid  qty");
        String role = UserContext.getRole();
        if (!"admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        User mandoob = mandoobRepository.getById(mandoobStock.getMandoobId());
        if (mandoob == null)
            return ApiResponse.error("Mandoob not found");

        Product product = productRepository.getById(mandoobStock.getProductid());
        if (product == null)
            return ApiResponse.error("Product not found");

        MandoobStock stock = mandoobstockRepository
                .getByMandoob(mandoobStock.getMandoobId())
                .stream()
                .filter(s -> s.getProductid() == mandoobStock.getProductid())
                .findFirst()
                .orElse(null);

        if (stock == null) {
            return ApiResponse.error("Product not found in mandoob stock");
        }
        if (stock.getQuantity() < mandoobStock.getQuantity()) {
            return ApiResponse.error(
                    "Insufficient stock in mandoob inventory. Available: "
                            + stock.getQuantity());
        }

        boolean isCreated = mandoobstockRepository.reduce(mandoobStock);

        if (!isCreated)
            return ApiResponse.error("Failed to return stocks");

        return ApiResponse.success("returned successfully");
    }

}