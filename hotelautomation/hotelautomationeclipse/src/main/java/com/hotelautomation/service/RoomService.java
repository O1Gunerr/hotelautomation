package com.hotelautomation.service;

import com.hotelautomation.model.Room;
import com.hotelautomation.repository.RoomRepository;
import java.util.List;

public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService() {
        this.roomRepository = new RoomRepository();
    }

    public void createRoom(String roomNumber, String roomPrice) {
        try {
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Oda numarası boş olamaz!");
            }
            if (roomPrice == null || roomPrice.trim().isEmpty()) {
                throw new IllegalArgumentException("Oda fiyatı boş olamaz!");
            }

            try {
                double price = Double.parseDouble(roomPrice);
                if (price <= 0) {
                    throw new IllegalArgumentException("Oda fiyatı sıfırdan büyük olmalıdır!");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Geçersiz fiyat formatı!");
            }

            Room existingRoom = roomRepository.findByRoomNumber(roomNumber);
            if (existingRoom != null) {
                throw new IllegalArgumentException("Bu oda numarası zaten kullanımda!");
            }

            Room room = new Room(roomNumber, roomPrice);
            roomRepository.save(room);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Oda oluşturma işlemi başarısız: " + e.getMessage());
        }
    }
    public List<Room> getAllRooms() {
        try {
            return roomRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Odaları getirme başarısız: " + e.getMessage());
        }
    }

    public void updateRoom(Room room) {
        try {
            if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Oda numarası boş olamaz!");
            }
            if (room.getRoomPrice() == null || room.getRoomPrice().trim().isEmpty()) {
                throw new IllegalArgumentException("Oda fiyatı boş olamaz!");
            }

            Room existingRoom = getRoomByNumber(room.getRoomNumber());
            if (existingRoom != null && !existingRoom.getRoomId().equals(room.getRoomId())) {
                throw new IllegalArgumentException("Bu oda numarası başka bir oda tarafından kullanılıyor!");
            }

            roomRepository.update(room);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Oda güncelleme başarısız: " + e.getMessage());
        }
    }

    public void deleteRoom(String roomId) {
        try {
            Room room = getRoomByNumber(roomId);
            if (room == null) {
                throw new RuntimeException("Oda bulunamadı!");
            }
            roomRepository.delete(roomId);
        } catch (Exception e) {
            throw new RuntimeException("Oda silme başarısız: " + e.getMessage());
        }
    }

    public Room getRoomByNumber(String roomNumber) {
        try {
            return roomRepository.findByRoomNumber(roomNumber);
        } catch (Exception e) {
            throw new RuntimeException("Oda getirme başarısız: " + e.getMessage());
        }
    }

    private void validateRoomData(String roomNumber, String roomPrice) {
        try {
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new RuntimeException("Oda numarası boş olamaz!");
            }

            if (roomPrice == null || roomPrice.trim().isEmpty()) {
                throw new RuntimeException("Oda fiyatı boş olamaz!");
            }

            try {
                Double.parseDouble(roomPrice);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Geçersiz fiyat formatı!");
            }

            if (Double.parseDouble(roomPrice) <= 0) {
                throw new RuntimeException("Oda fiyatı sıfırdan büyük olmalıdır!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Veri doğrulama başarısız: " + e.getMessage());
        }
    }

    private boolean isRoomNumberTaken(String roomNumber) {
        try {
            return getAllRooms().stream()
                    .anyMatch(r -> r.getRoomNumber().equals(roomNumber));
        } catch (Exception e) {
            throw new RuntimeException("Oda numarası kontrolü başarısız: " + e.getMessage());
        }
    }
}