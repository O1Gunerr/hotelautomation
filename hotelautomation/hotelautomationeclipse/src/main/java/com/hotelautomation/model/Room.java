package com.hotelautomation.model;

import java.util.UUID;

public class Room {
    private String roomId;
    private String roomNumber;
    private String roomPrice;

    public Room( String roomNumber, String roomPrice) {
        this.roomId = UUID.randomUUID().toString();
        this.roomNumber = roomNumber;
        this.roomPrice = roomPrice;
    }
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    public String getRoomPrice() {
        return roomPrice;
    }
    public void setRoomPrice(String roomPrice) {
        this.roomPrice = roomPrice;
    }

}
