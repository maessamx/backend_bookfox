package com.Mahmoud.sales_backend.module.Dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Dashboard;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    // ================= GET STATS (admin) =================
    public ApiResponse getStats() {
        String role = UserContext.getRole();
        if (!"admin".equals(role))
            return ApiResponse.error("Not allowed");

        Dashboard dto = new Dashboard();

        dto.setTotalBooks(dashboardRepository.getTotalBooks());
        dto.setTotalSupplies(dashboardRepository.getTotalSupplies());
        dto.setTotalProducts(dashboardRepository.getTotalProducts());
        dto.setTotalOrders(dashboardRepository.getTotalOrders());
        dto.setTotalUsers(dashboardRepository.getTotalUsers());
        dto.setOrdersToday(dashboardRepository.getOrdersToday());
        dto.setRevenue(dashboardRepository.getTotalRevenue());
        dto.setRevenueToday(dashboardRepository.getRevenueToday());
        dto.setPendingOrders(dashboardRepository.getPendingOrders());
        dto.setLowStockProducts(dashboardRepository.getLowStockProducts());

        return ApiResponse.success(dto);
    }

    // ================= SALES ANALYTICS (admin) =================
    public ApiResponse getSalesAnalytics(int days, int topProducts) {
        String role = UserContext.getRole();
        if (!"admin".equals(role))
            return ApiResponse.error("Not allowed");

        List<Map<String, Object>> trend = dashboardRepository.getSalesTrend(days);
        List<Map<String, Object>> products = dashboardRepository.getTopProducts(topProducts);

        Map<String, Object> result = new HashMap<>();
        result.put("salesTrend", trend);
        result.put("topProducts", products);
        result.put("periodDays", days);

        return ApiResponse.success(result);
    }

    // ================= TOP SELLERS (admin) =================
    public ApiResponse getTopSellers(int limit) {
        String role = UserContext.getRole();
        if (!"admin".equals(role))
            return ApiResponse.error("Not allowed");

        List<Map<String, Object>> sellers = dashboardRepository.getTopSellers(limit);
        return ApiResponse.success(sellers);
    }
}
