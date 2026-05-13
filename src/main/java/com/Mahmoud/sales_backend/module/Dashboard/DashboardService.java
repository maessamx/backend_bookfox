package com.Mahmoud.sales_backend.module.Dashboard;

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

    // ================= Get Stats =================
    public ApiResponse getStats() {
        int userId  = UserContext.getUserId();
        String role = UserContext.getRole();

        Dashboard dto= new Dashboard();

        if ("admin".equals(role)) {
            dto.setTotalClients(dashboardRepository.getTotalClients());
            dto.setTotalDebtsmandoob(dashboardRepository.getTotalMandoobNetBalance());
            dto.setTotalSalesToday(dashboardRepository.getTotalSalesToday());
            dto.setInvoicesToday(dashboardRepository.getInvoicesToday());
            dto.setCollectionsToday(dashboardRepository.getCollectionsToday());
            dto.setTotalDebts(dashboardRepository.getTotalDebts());
            dto.setTotalProducts(dashboardRepository.getTotalProducts());
            dto.setLowStockProducts(dashboardRepository.getLowStockProducts());
            dto.setPendingCashRequests(dashboardRepository.getPendingCashRequests());
            dto.setTotalProfitToday(dashboardRepository.getTotalProfitToday());
            dto.setTotalProfitAllTime(dashboardRepository.getTotalProfitAllTime());
            
        } else {
            dto.setTotalClients(dashboardRepository.getTotalClientsByMandoob(userId));
            dto.setTotalDebtsmandoob(dashboardRepository.getMandoobNetBalance(userId));
            dto.setTotalSalesToday(dashboardRepository.getMandoobSalesToday(userId));
            dto.setInvoicesToday(dashboardRepository.getMandoobInvoicesToday(userId));
            dto.setCollectionsToday(dashboardRepository.getMandoobCollectionsToday(userId));
            dto.setTotalDebts(dashboardRepository.getMandoobDebts(userId));
            dto.setTotalProducts(dashboardRepository.getMandoobStockTotalQuantity(userId));
        }

        return ApiResponse.success(dto);
    }

    public ApiResponse getwalletmandoob(){
        int userId  = UserContext.getUserId();
        String role = UserContext.getRole();
        if ("admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        return ApiResponse.success(dashboardRepository.getMandoobNetBalanceWithpending(userId));
    }

    // ===== أضف الـ methods دي جوا DashboardService =====

/**
 * GET /dashboard/sales-analytics?days=30&topProducts=10
 * إحصائيات المبيعات: تطور يومي + أكثر المنتجات مبيعاً
 */
public ApiResponse getSalesAnalytics(int days, int topProducts) {
    String role = UserContext.getRole();
    if (!"admin".equals(role)) {
        return ApiResponse.error("not allowed");
    }

    List<Map<String, Object>> trend    = dashboardRepository.getSalesTrend(days);
    List<Map<String, Object>> products = dashboardRepository.getTopProducts(topProducts);

    Map<String, Object> result = new java.util.HashMap<>();
    result.put("salesTrend",   trend);
    result.put("topProducts",  products);
    result.put("periodDays",   days);

    return ApiResponse.success(result);
}

/**
 * GET /dashboard/mandoobs-performance
 * أداء جميع المندوبين
 */
public ApiResponse getMandoobsPerformance() {
    String role = UserContext.getRole();
    if (!"admin".equals(role)) {
        return ApiResponse.error("not allowed");
    }

    List<Map<String, Object>> data = dashboardRepository.getMandoobsPerformance();
    return ApiResponse.success(data);
}
}