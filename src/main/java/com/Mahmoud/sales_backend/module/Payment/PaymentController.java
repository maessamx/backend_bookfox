package com.Mahmoud.sales_backend.module.Payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.Payment;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET /payments
    @GetMapping
    public ApiResponse getAll() {
        return paymentService.getAll();
    }
    // GET /payments/invoice/{invoiceId}
    @GetMapping("/invoice/{invoiceId}")
    public ApiResponse getByInvoice(@PathVariable int invoiceId) {
        return paymentService.getByInvoice(invoiceId);
    }
    // POST /payments
    @PostMapping
    public ApiResponse create(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }
}