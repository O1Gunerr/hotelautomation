package com.hotelautomation.service;

import com.hotelautomation.model.Customer;
import com.hotelautomation.model.Reservation;
import com.hotelautomation.repository.ReservationRepository;

import java.util.Date;
import java.util.List;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CustomerService customerService;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.customerService = new CustomerService();
    }

    public void createReservation(String customerId, String roomNumber, Date checkInDate,
                                  Date checkOutDate, Double price, int numberOfGuests) {
        try {
            Customer customer = customerService.getCustomer(customerId);
            validateDates(checkInDate, checkOutDate);

            if (!isRoomAvailable(roomNumber, checkInDate, checkOutDate)) {
                throw new RuntimeException("Bu oda seçilen tarihler için müsait değil!");
            }

            if (price <= 0) {
                throw new RuntimeException("Geçersiz fiyat!");
            }

            if (numberOfGuests <= 0) {
                throw new RuntimeException("Geçersiz misafir sayısı!");
            }

            Reservation reservation = new Reservation();
            reservation.setCustomer(customer);
            reservation.setRoomNumber(Integer.valueOf(roomNumber));
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setPrice(price);
            reservation.setNumberOfGuests(numberOfGuests);
            reservation.setReservationStatus(false);

            reservationRepository.save(reservation);
        } catch (Exception e) {
            throw new RuntimeException("Rezervasyon oluşturma başarısız: " + e.getMessage());
        }
    }

    public void updateReservation(String reservationId, String roomNumber, Date checkInDate,
                                  Date checkOutDate, Double price, int numberOfGuests) {
        try {
            Reservation existingReservation = getReservation(reservationId);
            validateDates(checkInDate, checkOutDate);

            if (!existingReservation.getRoomNumber().equals(roomNumber) &&
                    !isRoomAvailable(roomNumber, checkInDate, checkOutDate)) {
                throw new RuntimeException("Yeni oda seçilen tarihler için müsait değil!");
            }

            existingReservation.setRoomNumber(Integer.valueOf(roomNumber));
            existingReservation.setCheckInDate(checkInDate);
            existingReservation.setCheckOutDate(checkOutDate);
            existingReservation.setPrice(price);
            existingReservation.setNumberOfGuests(numberOfGuests);

            reservationRepository.update(existingReservation);
        } catch (Exception e) {
            throw new RuntimeException("Rezervasyon güncelleme başarısız: " + e.getMessage());
        }
    }

    public void cancelReservation(String reservationId) {
        try {
            Reservation reservation = getReservation(reservationId);
            reservationRepository.delete(reservationId);
        } catch (Exception e) {
            throw new RuntimeException("Rezervasyon iptal işlemi başarısız: " + e.getMessage());
        }
    }

    public Reservation getReservation(String reservationId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId);
            if (reservation == null) {
                throw new RuntimeException("Rezervasyon bulunamadı!");
            }
            return reservation;
        } catch (Exception e) {
            throw new RuntimeException("Rezervasyon getirme başarısız: " + e.getMessage());
        }
    }

    public List<Reservation> getCustomerReservations(String customerId) {
        try {
            return reservationRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Müşteri rezervasyonlarını getirme başarısız: " + e.getMessage());
        }
    }

    public List<Reservation> getAllReservations() {
        try {
            return reservationRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Tüm rezervasyonları getirme başarısız: " + e.getMessage());
        }
    }

    private void validateDates(Date checkIn, Date checkOut) {
        try {
            Date now = new Date();
            if (checkIn == null || checkOut == null) {
                throw new RuntimeException("Tarihler boş olamaz!");
            }
            if (checkIn.before(now)) {
                throw new RuntimeException("Geçmiş tarihli rezervasyon yapılamaz!");
            }
            if (checkOut.before(checkIn)) {
                throw new RuntimeException("Çıkış tarihi giriş tarihinden önce olamaz!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Tarih doğrulama başarısız: " + e.getMessage());
        }
    }

    private boolean isRoomAvailable(String roomNumber, Date checkIn, Date checkOut) {
        try {
            List<Reservation> allReservations = getAllReservations();

            return allReservations.stream()
                    .filter(r -> r.getRoomNumber().equals(roomNumber))
                    .noneMatch(r -> (checkIn.before(r.getCheckOutDate()) &&
                            checkOut.after(r.getCheckInDate())));
        } catch (Exception e) {
            throw new RuntimeException("Oda müsaitlik kontrolü başarısız: " + e.getMessage());
        }
    }
}