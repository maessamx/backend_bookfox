package com.Mahmoud.sales_backend.module.Invoice;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.Invoice;
import com.Mahmoud.sales_backend.model.InvoiceItem;
import com.Mahmoud.sales_backend.model.MandoobStock;
import com.Mahmoud.sales_backend.model.Product;
import com.Mahmoud.sales_backend.module.Mandoob.MandoobRepository;
import com.Mahmoud.sales_backend.module.MandoobStock.MandoobStockRepository;
import com.Mahmoud.sales_backend.module.Product.ProductRepository;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final MandoobStockRepository mandoobStockRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,MandoobStockRepository mandoobStockRepository,ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository =productRepository;
        this.mandoobStockRepository =mandoobStockRepository;
    }

    // ================= GetAll =================
    public ApiResponse getAll() {
        Integer userId = UserContext.getUserId();
        String role = UserContext.getRole();

        List<Invoice> invoices;

        if ("admin".equals(role)) {
            invoices = invoiceRepository.getAll();
        } else {
            invoices = invoiceRepository.getInvoicesByMandoob(userId);
        }

        return ApiResponse.success(invoices);
    }

    // ================= GetById =================
    public ApiResponse getById(int id) {
        if (id <= 0) {
            return ApiResponse.error("Invalid ID");
        }

        Invoice invoice = invoiceRepository.getById(id);

        if (invoice == null) {
            return ApiResponse.error("Invoice not found");
        }

        return ApiResponse.success(invoice);
    }

    // ================= GetByClient =================
    public ApiResponse getByClient(int clientId) {
        if (clientId <= 0) {
            return ApiResponse.error("Invalid client ID");
        }

        List<Invoice> invoices = invoiceRepository.getInvoicesByClient(clientId);

        return ApiResponse.success(invoices);
    }

    // ================= GetByMandoob =================
    public ApiResponse getByMandoob(int mandoobId) {
        String role = UserContext.getRole();

        if (!"admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        if (mandoobId <= 0) {
            return ApiResponse.error("Invalid mandoob ID");
        }

        List<Invoice> invoices = invoiceRepository.getInvoicesByMandoob(mandoobId);

        return ApiResponse.success(invoices);
    }

    // ================= GetWithItems =================
    public ApiResponse getWithItems(int id) {
        if (id <= 0) {
            return ApiResponse.error("Invalid ID");
        }

        Invoice invoice = invoiceRepository.getById(id);

        if (invoice == null) {
            return ApiResponse.error("Invoice not found");
        }

        List<InvoiceItem> items = invoiceRepository.getInvoiceItems(id);
        invoice.setItems(items);

        return ApiResponse.success(invoice);
    }

    // ================= Create =================
    public ApiResponse create(Invoice invoice) {
        String role = UserContext.getRole();

        if ("admin".equals(role)) {
            return ApiResponse.error("not allowed");
        }
        Integer userId = UserContext.getUserId();
        invoice.setMandoobId(userId);
        if (invoice.getClientId() <= 0)
            return ApiResponse.error("Invalid client ID");

        if (invoice.getItems() == null || invoice.getItems().isEmpty())
            return ApiResponse.error("Invoice must have items");

        double total = 0;

        // ================= PROCESS ITEMS =================
        for (InvoiceItem item : invoice.getItems()) {

            MandoobStock product = mandoobStockRepository.getByProductId(item.getProductId(),userId);
            if (item.getQuantity() <= 0) {
                return ApiResponse.error("Quantity must be greater than 0");
            }
            if (product == null){
                return ApiResponse.error("Product not found: " + item.getProductId());
            }
            if(product.getQuantity() < item.getQuantity()){
                return ApiResponse.error("Insufficient stock for product: " + product.getProductName());
            }
            double price = item.getSellPrice();
            double priceitem = product.getSellprice();
            if(price < priceitem){
                return ApiResponse.error("price must greater than: " + priceitem);
            }
            
            double itemTotal = price * item.getQuantity();

            item.setPricecost(product.getPricecost());
            item.setProfit((price - product.getPricecost())* item.getQuantity());
            item.setTotal(itemTotal);

            total += itemTotal;
        }

        // ================= SET TOTAL =================
        invoice.setTotal(total);

        double paid = invoice.getPaid();

        // ================= STATUS =================
        if (paid <= 0) {
            invoice.setStatus("unpaid");
        } else if (paid < total) {
            invoice.setStatus("partial");
        } else {
            invoice.setStatus("paid");
        }
    
        // ================= SAVE INVOICE =================
        boolean isCreated = invoiceRepository.create(invoice);
    
        if (!isCreated)
            return ApiResponse.error("Failed to create invoice");
    
    
        return ApiResponse.success("Invoice created successfully");
    }

    // ================= Delete =================
    public ApiResponse delete(int id) {
        if (id <= 0) {
            return ApiResponse.error("Invalid ID");
        }

        boolean isDeleted = invoiceRepository.delete(id);

        if (isDeleted) {
            return ApiResponse.success("Invoice deleted successfully");
        }

        return ApiResponse.error("Delete failed");
    }
}