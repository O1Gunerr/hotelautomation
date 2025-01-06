package com.hotelautomation.repository;

import com.hotelautomation.model.Customer;
import com.hotelautomation.model.Reservation;
import com.hotelautomation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_id, room_number, check_in_date, check_out_date, price, number_of_guests, is_empty, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reservation.getReservationId());
            pstmt.setInt(2, reservation.getRoomNumber());
            pstmt.setTimestamp(3, new Timestamp(reservation.getCheckInDate().getTime()));
            pstmt.setTimestamp(4, new Timestamp(reservation.getCheckOutDate().getTime()));
            pstmt.setDouble(5, reservation.getPrice());
            pstmt.setInt(6, reservation.getNumberOfGuests());
            pstmt.setBoolean(7, reservation.getReservationStatus());
            pstmt.setString(8, reservation.getCustomers().getCustomerId());

            pstmt.executeUpdate();
            System.out.println("Rezervasyon başarıyla kaydedildi.");
        } catch (SQLException e) {
            System.out.println("Rezervasyon kaydetme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    public Reservation findById(String reservationId) {
        String sql = "SELECT r.*, c.* FROM reservations r JOIN customers c ON r.customer_id = c.customer_id WHERE r.reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reservationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createReservationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Rezervasyon arama hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return null;
    }

    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.* FROM reservations r JOIN customers c ON r.customer_id = c.customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Rezervasyonları listelerken hata: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> findByCustomerId(String customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.* FROM reservations r JOIN customers c ON r.customer_id = c.customer_id WHERE r.customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Müşteri rezervasyonlarını listelerken hata: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return reservations;
    }

    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET room_number = ?, check_in_date = ?, check_out_date = ?, price = ?, number_of_guests = ?, is_empty = ?, customer_id = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getRoomNumber());
            pstmt.setTimestamp(2, new Timestamp(reservation.getCheckInDate().getTime()));
            pstmt.setTimestamp(3, new Timestamp(reservation.getCheckOutDate().getTime()));
            pstmt.setDouble(4, reservation.getPrice());
            pstmt.setInt(5, reservation.getNumberOfGuests());
            pstmt.setBoolean(6, reservation.getReservationStatus());
            pstmt.setString(7, reservation.getCustomers().getCustomerId());
            pstmt.setString(8, reservation.getReservationId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Rezervasyon başarıyla güncellendi.");
            } else {
                throw new SQLException("Rezervasyon güncellenemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Rezervasyon güncelleme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    public void delete(String reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reservationId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Rezervasyon başarıyla silindi.");
            } else {
                throw new SQLException("Rezervasyon silinemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Rezervasyon silme hatası: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("phone"),
                rs.getBoolean("is_admin")
        );
        customer.setCustomerId(rs.getString("customer_id"));

        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getString("reservation_id"));
        reservation.setRoomNumber(rs.getInt("room_number"));
        reservation.setCheckInDate(rs.getTimestamp("check_in_date"));
        reservation.setCheckOutDate(rs.getTimestamp("check_out_date"));
        reservation.setPrice(rs.getDouble("price"));
        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setReservationStatus(rs.getBoolean("is_empty"));
        reservation.setCustomer(customer);

        return reservation;
    }

    public List<Reservation> findByRoomNumber(int roomNumber) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.* FROM reservations r JOIN customers c ON r.customer_id = c.customer_id WHERE r.room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Oda rezervasyonlarını listelerken hata: " + e.getMessage());
            throw new RuntimeException("Veritabanı hatası: " + e.getMessage());
        }
        return reservations;
    }
}