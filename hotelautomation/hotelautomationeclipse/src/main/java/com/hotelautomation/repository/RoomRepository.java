package com.hotelautomation.repository;

import com.hotelautomation.model.Room;
import com.hotelautomation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        rs.getString("room_price")
                );
                room.setRoomId(rs.getString("room_id"));
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oda arama hatası: " + e.getMessage());
        }
        return null;
    }

    public void save(Room room) {
        String sql = "INSERT INTO rooms (room_id, room_number, room_price) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomId());
            pstmt.setString(2, room.getRoomNumber());
            pstmt.setString(3, room.getRoomPrice());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Oda kaydedilemedi.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Oda kaydetme hatası: " + e.getMessage());
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        rs.getString("room_price")
                );
                room.setRoomId(rs.getString("room_id"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Odaları listelerken hata: " + e.getMessage());
        }
        return rooms;
    }

    public void update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_price = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomPrice());
            pstmt.setString(3, room.getRoomId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Oda güncellenemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Oda güncelleme hatası: " + e.getMessage());
        }
    }

    public void delete(String roomId) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Oda başarıyla silindi.");
            } else {
                System.out.println("Oda silinemedi: ID bulunamadı.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oda silme hatası: " + e.getMessage());
        }
    }

    public Room findById(String roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        rs.getString("room_price")
                );
                room.setRoomId(rs.getString("room_id"));
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Oda arama hatası: " + e.getMessage());
        }
        return null;
    }
}