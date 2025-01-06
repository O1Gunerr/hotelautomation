package com.hotelautomation.model;

import java.util.UUID;

public class Customer extends User {
    private String customerId;
    private Boolean isAdmin;

    public Customer(String name, String email, String password, String phone, Boolean isAdmin) {
        super(name, email, password, phone);
        this.customerId = UUID.randomUUID().toString();
        this.isAdmin = isAdmin;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
