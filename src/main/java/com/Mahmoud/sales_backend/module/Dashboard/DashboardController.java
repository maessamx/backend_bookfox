package com.Mahmoud.sales_backend.module.Dashboard;

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

    // GET /dashboard/stats
    @GetMapping("/stats")
    public ApiResponse getStats() {
        return dashboardService.getStats();
    }

    // GET /dashboard/sales-analytics?days=30&topProducts=10
    @GetMapping("/sales-analytics")
    public ApiResponse getSalesAnalytics(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int topProducts) {
        return dashboardService.getSalesAnalytics(days, topProducts);
    }

    // GET /dashboard/top-sellers?limit=5
    @GetMapping("/top-sellers")
    public ApiResponse getTopSellers(@RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getTopSellers(limit);
    }
}
