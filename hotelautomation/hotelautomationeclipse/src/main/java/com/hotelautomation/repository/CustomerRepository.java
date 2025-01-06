package com.hotelautomation.repository;

import com.hotelautomation.model.Customer;
import com.hotelautomation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    public void save(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, name, email, password, phone, is_admin) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());
            pstmt.setString(5, customer.getPhone());
            pstmt.setBoolean(6, customer.getIsAdmin());

            pstmt.executeUpdate();
            System.out.println("Müşteri başarıyla kaydedildi.");
        } catch (SQLException e) {
            System.out.println("Müşteri kaydetme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    public Customer findById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Müşteri arama hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return null;
    }

    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Email ile müşteri arama hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(createCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Müşterileri listelerken hata: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return customers;
    }

    public void update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, password = ?, phone = ?, is_admin = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPassword());
            pstmt.setString(4, customer.getPhone());
            pstmt.setBoolean(5, customer.getIsAdmin());
            pstmt.setString(6, customer.getCustomerId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Müşteri başarıyla güncellendi.");
            } else {
                throw new SQLException("Müşteri güncellenemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Müşteri güncelleme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    public void delete(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Müşteri başarıyla silindi.");
            } else {
                throw new SQLException("Müşteri silinemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Müşteri silme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    private Customer createCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("phone"),
                rs.getBoolean("is_admin")
        );
        customer.setCustomerId(rs.getString("customer_id"));
        return customer;
    }
}