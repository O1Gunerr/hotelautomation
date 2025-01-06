package com.hotelautomation.service;

import com.hotelautomation.model.Customer;
import com.hotelautomation.repository.CustomerRepository;

import java.util.List;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepository();
    }

    public void registerCustomer(String name, String email, String password, String phone, Boolean isAdmin) {
        try {
            validateCustomerData(name, email, password, phone);

            if (isEmailTaken(email)) {
                throw new RuntimeException("Bu email adresi zaten kullanımda!");
            }

            Customer customer = new Customer(name, email, password, phone, isAdmin);
            customerRepository.save(customer);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri kaydı başarısız: " + e.getMessage());
        }
    }

    public void updateCustomer(Customer customer) {
        try {
            Customer existingCustomer = customerRepository.findById(customer.getCustomerId());
            if (existingCustomer == null) {
                throw new RuntimeException("Müşteri bulunamadı!");
            }

            if (!existingCustomer.getEmail().equals(customer.getEmail()) && isEmailTaken(customer.getEmail())) {
                throw new RuntimeException("Bu email adresi zaten kullanımda!");
            }

            validateCustomerData(customer.getName(), customer.getEmail(), customer.getPassword(), customer.getPhone());

            existingCustomer.setName(customer.getName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setPassword(customer.getPassword());
            existingCustomer.setPhone(customer.getPhone());
            existingCustomer.setIsAdmin(customer.getIsAdmin());

            customerRepository.update(existingCustomer);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri güncelleme başarısız: " + e.getMessage());
        }
    }

    public void deleteCustomer(String customerId) {
        try {
            Customer customer = customerRepository.findById(customerId);
            if (customer == null) {
                throw new RuntimeException("Müşteri bulunamadı!");
            }
            customerRepository.delete(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri silme başarısız: " + e.getMessage());
        }
    }

    public Customer getCustomer(String customerId) {
        try {
            Customer customer = customerRepository.findById(customerId);
            if (customer == null) {
                throw new RuntimeException("Müşteri bulunamadı!");
            }
            return customer;
        } catch (Exception e) {
            throw new RuntimeException("Müşteri getirme başarısız: " + e.getMessage());
        }
    }

    public Customer getCustomerById(String customerId) {
        try {
            return customerRepository.findById(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri ID ile getirme başarısız: " + e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Müşteri listesi getirme başarısız: " + e.getMessage());
        }
    }

    private Customer findByEmail(String email) {
        try {
            return getAllCustomers().stream()
                    .filter(c -> c.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Email ile müşteri arama başarısız: " + e.getMessage());
        }
    }

    private boolean isEmailTaken(String email) {
        try {
            return findByEmail(email) != null;
        } catch (Exception e) {
            throw new RuntimeException("Email kontrolü başarısız: " + e.getMessage());
        }
    }

    private void validateCustomerData(String name, String email, String password, String phone) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("İsim boş olamaz!");
            }

            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("Geçersiz email formatı!");
            }

            if (password == null || password.length() < 6) {
                throw new RuntimeException("Şifre en az 6 karakter olmalıdır!");
            }

            if (phone == null || !phone.matches("\\d{10,11}")) {
                throw new RuntimeException("Geçersiz telefon numarası!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Veri doğrulama başarısız: " + e.getMessage());
        }
    }

    public Customer login(String email, String password) {
        try {
            Customer customer = findByEmail(email);
            if (customer == null || !customer.getPassword().equals(password)) {
                throw new RuntimeException("Geçersiz email veya şifre!");
            }
            return customer;
        } catch (Exception e) {
            throw new RuntimeException("Giriş başarısız: " + e.getMessage());
        }
    }
}