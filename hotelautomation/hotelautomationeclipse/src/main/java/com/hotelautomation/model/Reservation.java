package com.hotelautomation.model;

import java.util.Date;
import java.util.UUID;

public class Reservation {
    private String reservationId = UUID.randomUUID().toString();
    private Boolean isEmpty = true;
    private Integer roomNumber;
    private Date checkInDate;
    private Double price;
    private int numberOfGuests;
    private Date checkOutDate;
    private Customer customer;

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomers() {
        return customer;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getCustomerId() {
        return customer.getCustomerId();
    }

    public void setReservationStatus(Boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public Boolean getReservationStatus() {
        return isEmpty;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }
}
