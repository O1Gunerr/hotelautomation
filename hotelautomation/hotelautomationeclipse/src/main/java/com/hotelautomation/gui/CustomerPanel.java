package com.hotelautomation.gui;

import com.hotelautomation.model.Customer;
import com.hotelautomation.model.Reservation;
import com.hotelautomation.model.Room;
import com.hotelautomation.service.ReservationService;
import com.hotelautomation.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerPanel extends JFrame {
    private final Customer currentUser;
    private final ReservationService reservationService;
    private final RoomService roomService;
    private final SimpleDateFormat dateFormat;
    private JTable roomTable;
    private JTable reservationsTable;

    public CustomerPanel(Customer customer) {
        this.currentUser = customer;
        this.reservationService = new ReservationService();
        this.roomService = new RoomService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setTitle("Müşteri Paneli - " + customer.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
    }

    private void initializeComponents() {
        try {
            JMenuBar menuBar = new JMenuBar();
            JMenu userMenu = new JMenu("Kullanıcı");
            JMenuItem logoutItem = new JMenuItem("Çıkış Yap");
            logoutItem.addActionListener(e -> logout());
            userMenu.add(logoutItem);
            menuBar.add(userMenu);
            setJMenuBar(menuBar);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Müsait Odalar", createRoomsPanel());
            tabbedPane.addTab("Rezervasyonlarım", createReservationsPanel());

            add(tabbedPane);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Arayüz oluşturulurken hata: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            new LoginFrame().setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Çıkış yapılırken hata: " + e.getMessage());
        }
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            String[] columnNames = {"Oda No", "Fiyat"};
            roomTable = new JTable(new DefaultTableModel(columnNames, 0));
            JScrollPane scrollPane = new JScrollPane(roomTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton reserveButton = new JButton("Rezervasyon Yap");
            buttonPanel.add(reserveButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            reserveButton.addActionListener(e -> handleReservationButton());

            refreshRoomTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Oda paneli oluşturulurken hata: " + e.getMessage());
        }
        return panel;
    }

    private void handleReservationButton() {
        try {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow != -1) {
                showMakeReservationDialog((String) roomTable.getValueAt(selectedRow, 0));
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir oda seçin!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon işlemi sırasında hata: " + e.getMessage());
        }
    }

    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            String[] columnNames = {"Rezervasyon ID", "Oda No", "Giriş Tarihi", "Çıkış Tarihi", "Fiyat", "Misafir Sayısı"};
            reservationsTable = new JTable(new DefaultTableModel(columnNames, 0));
            JScrollPane scrollPane = new JScrollPane(reservationsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton cancelButton = new JButton("Rezervasyonu İptal Et");
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            cancelButton.addActionListener(e -> handleCancelReservation());

            refreshReservationsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon paneli oluşturulurken hata: " + e.getMessage());
        }
        return panel;
    }

    private void handleCancelReservation() {
        try {
            int selectedRow = reservationsTable.getSelectedRow();
            if (selectedRow != -1) {
                String reservationId = (String) reservationsTable.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(this,
                        "Bu rezervasyonu iptal etmek istediğinizden emin misiniz?",
                        "Rezervasyon İptali",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    reservationService.cancelReservation(reservationId);
                    refreshReservationsTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal edilecek rezervasyonu seçin!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon iptal edilirken hata: " + e.getMessage());
        }
    }

    private void showMakeReservationDialog(String roomNumber) {
        JDialog dialog = new JDialog(this, "Rezervasyon Yap", true);
        try {
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextField checkInField = new JTextField();
            JTextField checkOutField = new JTextField();
            JTextField guestsField = new JTextField();

            panel.add(new JLabel("Giriş Tarihi (dd/MM/yyyy):"));
            panel.add(checkInField);
            panel.add(new JLabel("Çıkış Tarihi (dd/MM/yyyy):"));
            panel.add(checkOutField);
            panel.add(new JLabel("Misafir Sayısı:"));
            panel.add(guestsField);

            JButton saveButton = new JButton("Rezervasyon Yap");
            JButton cancelButton = new JButton("İptal");

            saveButton.addActionListener(e -> handleReservationSave(dialog, roomNumber, checkInField, checkOutField, guestsField));
            cancelButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel);

            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon formu oluşturulurken hata: " + e.getMessage());
            dialog.dispose();
        }
    }

    private void handleReservationSave(JDialog dialog, String roomNumber, JTextField checkInField,
                                       JTextField checkOutField, JTextField guestsField) {
        try {
            if (checkInField.getText().isEmpty() || checkOutField.getText().isEmpty() || guestsField.getText().isEmpty()) {
                throw new Exception("Tüm alanları doldurunuz!");
            }

            Date checkIn = dateFormat.parse(checkInField.getText());
            Date checkOut = dateFormat.parse(checkOutField.getText());

            if (!checkOut.after(checkIn)) {
                throw new Exception("Çıkış tarihi giriş tarihinden sonra olmalıdır!");
            }

            Room room = roomService.getRoomByNumber(roomNumber);
            int guestCount = Integer.parseInt(guestsField.getText());

            if (guestCount <= 0) {
                throw new Exception("Misafir sayısı 0'dan büyük olmalıdır!");
            }

            reservationService.createReservation(
                    currentUser.getCustomerId(),
                    roomNumber,
                    checkIn,
                    checkOut,
                    Double.parseDouble(room.getRoomPrice()),
                    guestCount
            );

            JOptionPane.showMessageDialog(dialog, "Rezervasyon başarıyla oluşturuldu!");
            dialog.dispose();
            refreshReservationsTable();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(dialog, "Geçersiz tarih formatı! Lütfen dd/MM/yyyy formatında girin.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Geçersiz misafir sayısı!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
        }
    }

    private void refreshRoomTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) roomTable.getModel();
            model.setRowCount(0);

            List<Room> rooms = roomService.getAllRooms();
            for (Room room : rooms) {
                Object[] row = {
                        room.getRoomNumber(),
                        room.getRoomPrice()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Oda listesi güncellenirken hata: " + e.getMessage());
        }
    }

    private void refreshReservationsTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) reservationsTable.getModel();
            model.setRowCount(0);

            List<Reservation> reservations = reservationService.getCustomerReservations(currentUser.getCustomerId());
            for (Reservation reservation : reservations) {
                Object[] row = {
                        reservation.getReservationId(),
                        reservation.getRoomNumber(),
                        dateFormat.format(reservation.getCheckInDate()),
                        dateFormat.format(reservation.getCheckOutDate()),
                        reservation.getPrice(),
                        reservation.getNumberOfGuests()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon listesi güncellenirken hata: " + e.getMessage());
        }
    }
}