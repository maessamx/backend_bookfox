package com.Mahmoud.sales_backend.module.Dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse getStats() {
        return dashboardService.getStats();
    }

    // GET Dashboard/wallet-mandoob
    @GetMapping("/wallet-mandoob")
    public ApiResponse getwalletmandoob() {
        return dashboardService.getwalletmandoob();
    }

    // ===== أضف الـ endpoints دي جوا DashboardController =====

    /**
     * GET /dashboard/sales-analytics
     *
     * Query params (اختياري):
     * days = عدد الأيام للتطور اليومي (default: 30)
     * topProducts = عدد أكثر المنتجات مبيعاً (default: 10)
     *
     * Example: GET /dashboard/sales-analytics?days=7&topProducts=5
     */
    @GetMapping("/sales-analytics")
    public ApiResponse getSalesAnalytics(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int topProducts) {

        return dashboardService.getSalesAnalytics(days, topProducts);
    }

    /**
     * GET /dashboard/mandoobs-performance
     *
     * بيرجع لكل مندوب:
     * - عدد العملاء
     * - عدد الفواتير + إجمالي المبيعات + التحصيلات + الديون
     * - الربح
     * - المخزون اللي معاه
     * - طلبات السحب المعلقة + إجمالي المسحوب المعتمد
     * - صافي الرصيد
     */
    @GetMapping("/mandoobs-performance")
    public ApiResponse getMandoobsPerformance() {
        return dashboardService.getMandoobsPerformance();
    }
}