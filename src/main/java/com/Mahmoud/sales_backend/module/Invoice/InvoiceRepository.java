package com.Mahmoud.sales_backend.module.Invoice;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.Invoice;
import com.Mahmoud.sales_backend.model.InvoiceItem;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceRepository extends BaseRepository {

    public InvoiceRepository(Connection conn) {
        super(conn);
    }

    // ================= Get All =================
    public List<Invoice> getAll() {
        List<Map<String, Object>> result = super.getAll("invoices");

        List<Invoice> invoices = new ArrayList<>();
        for (Map<String, Object> row : result) {
            invoices.add(mapToInvoice(row));
        }

        return invoices;
    }

    // ================= Get By ID =================
    public Invoice getById(int id) {
        Map<String, Object> result =
            getRowByColumn("invoices", "id", id);

        if (result.isEmpty()) return null;

        return mapToInvoice(result);
    }

    // ================= Get By Client =================
    public List<Invoice> getInvoicesByClient(int clientId) {
        List<Map<String, Object>> result =
            getByColumn("invoices", "client_id", clientId);
        List<Invoice> invoices = new ArrayList<>();
        for (Map<String, Object> row : result) {
            invoices.add(mapToInvoice(row));
        }
        return invoices;
    }

    // ================= Get By Mandoob =================
    public List<Invoice> getInvoicesByMandoob(int mandoobId) {
        List<Map<String, Object>> result =
            getByColumn("invoices", "mandoob_id", mandoobId);
        List<Invoice> invoices = new ArrayList<>();
        for (Map<String, Object> row : result) {
            invoices.add(mapToInvoice(row));
        }

        return invoices;
    }

    // ================= Get Today Invoices Count =================
    public int getTodayInvoicesCount() {
        List<Map<String, Object>> result =
                query("SELECT COUNT(*) AS cnt FROM invoices WHERE DATE(created_at) = CURDATE()");

        if (result.isEmpty()) return 0;

        Object cnt = result.get(0).get("cnt");
        return cnt != null ? ((Number) cnt).intValue() : 0;
    }

    // ================= Get Invoice Items =================
    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        List<Map<String, Object>> result =
            getByColumn("invoice_items", "invoice_id", invoiceId);
        List<InvoiceItem> items = new ArrayList<>();
        for (Map<String, Object> row : result) {
            items.add(mapToInvoiceItem(row));
        }

        return items;
    }

    // ================= Count =================
    public int count() {
        return super.count("invoices");
    }

    // ================= Create =================
    public boolean create(Invoice invoice) {
        try {
            Map<String, Object> data = Map.of(
                    "mandoob_id", invoice.getMandoobId(),
                    "client_id",  invoice.getClientId(),
                    "total",      invoice.getTotal(),
                    "paid",       invoice.getPaid(),
                    "status",     invoice.getStatus()
            );
            return insert("invoices", data) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= Delete =================
    public boolean delete(int id) {
        try {
            return delete("invoices", "id", id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= Invoice Mapper =================
    private Invoice mapToInvoice(Map<String, Object> row) {
        Invoice invoice = new Invoice();

        invoice.setId(((Number) row.get("id")).intValue());
        invoice.setMandoobId(((Number) row.get("mandoob_id")).intValue());
        invoice.setClientId(((Number) row.get("client_id")).intValue());
        invoice.setTotal(((Number) row.get("total")).doubleValue());
        invoice.setPaid(((Number) row.get("paid")).doubleValue());
        invoice.setStatus((String) row.get("status"));
        invoice.setCreatedAt((Date) row.get("created_at"));

        return invoice;
    }

    // ================= InvoiceItem Mapper =================
    private InvoiceItem mapToInvoiceItem(Map<String, Object> row) {
        InvoiceItem item = new InvoiceItem();

        item.setId(((Number) row.get("id")).intValue());
        item.setInvoiceId(((Number) row.get("invoice_id")).intValue());
        item.setProductId(((Number) row.get("product_id")).intValue());
        item.setQuantity(((Number) row.get("quantity")).intValue());
        item.setPricecost(((Number) row.get("price_cost")).doubleValue());
        item.setSellprice(((Number) row.get("sell_price")).doubleValue());
        item.setTotal(((Number) row.get("total")).doubleValue());
        item.setProfit(((Number) row.get("profit")).doubleValue());

        return item;
    }
}