package com.Mahmoud.sales_backend.module.Client;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.module.Client.ClientRepository;
import com.Mahmoud.sales_backend.model.Client;
import com.Mahmoud.sales_backend.shared.ApiResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.Mahmoud.sales_backend.security.UserContext;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // ================= GetAll =================
    public ApiResponse getAll() {
        Integer userId = UserContext.getUserId();
        String role = UserContext.getRole();
        List<Client> clients;
        if ("admin".equals(role)) {
            clients = clientRepository.getAll();
        }else{
            clients = clientRepository.getClientsByMandoob(userId);
        }

        System.out.println("USER ID: " + userId);
        System.out.println("ROLE: " + role);
        return ApiResponse.success(clients);
    }
    // ================= GetByid =================
    public ApiResponse getById(int id) {
        if (id <= 0 || id > 5000) {
            return ApiResponse.error("Invalid ID");
        }
        Client client = clientRepository.getById(id);
        if (client == null) {
            return ApiResponse.error("Client not found");
        }

        return ApiResponse.success(client);
    }
    // ================= Create Client =================
    public ApiResponse create(Client client) {
        if (client.getName() == null || client.getName().isEmpty()) {
        return ApiResponse.error("Name is required");
        }
        if (client.getMandoobId() <= 0) {
            return ApiResponse.error("Invalid mandoob id");
        }
        boolean isCreated = clientRepository.create(client);
        if (isCreated) {
            return ApiResponse.success("Client created successfully");
        }
        return ApiResponse.error("Failed to create client");
    }
    // ================= delete client =================

    public ApiResponse delete(int id) {
        if (id <= 0 || id > 5000) {
            return ApiResponse.error("Invalid ID");
        }
        boolean isDeleted = clientRepository.delete(id);
        if (isDeleted) {
            return ApiResponse.success("Deleted successfully");
        }
        return ApiResponse.error("Delete failed");
    }
    // ================= Get By Mandoob =================
    public ApiResponse getByMandoob(int mandoobId) {
        String role = UserContext.getRole();
        if ("admin".equals(role)) {
            List<Client> clients = clientRepository.getClientsByMandoob(mandoobId);
            return ApiResponse.success(clients);
        }
        return ApiResponse.error("not allowed");
    }
}