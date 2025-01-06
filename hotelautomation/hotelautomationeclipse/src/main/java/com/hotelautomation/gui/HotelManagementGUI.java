package com.hotelautomation.gui;

import com.hotelautomation.model.Customer;
import com.hotelautomation.model.Reservation;
import com.hotelautomation.model.Room;
import com.hotelautomation.service.CustomerService;
import com.hotelautomation.service.ReservationService;
import com.hotelautomation.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HotelManagementGUI extends JFrame {
    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final RoomService roomService;
    private final SimpleDateFormat dateFormat;
    private JTable customerTable;
    private JTable roomTable;
    private JTable reservationTable;
    private final Customer currentUser;

    public HotelManagementGUI(Customer admin) {
        this.currentUser = admin;
        this.customerService = new CustomerService();
        this.reservationService = new ReservationService();
        this.roomService = new RoomService();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setTitle("Otel Yönetim Sistemi - Admin Panel: " + admin.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("Menü");
        JMenuItem logoutItem = new JMenuItem("Çıkış Yap");
        logoutItem.addActionListener(e -> logout());
        userMenu.add(logoutItem);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Müşteriler", createCustomerPanel());
        tabbedPane.addTab("Odalar", createRoomPanel());
        tabbedPane.addTab("Rezervasyonlar", createReservationPanel());

        add(tabbedPane);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Ad Soyad", "Email", "Telefon"};
        customerTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Yeni Müşteri");
        JButton editButton = new JButton("Düzenle");
        JButton deleteButton = new JButton("Sil");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddCustomerDialog());

        editButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String customerId = (String) customerTable.getValueAt(selectedRow, 0);
                showEditCustomerDialog(customerId);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen düzenlenecek müşteriyi seçin!");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String customerId = (String) customerTable.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(this,
                        "Bu müşteriyi silmek istediğinizden emin misiniz?",
                        "Müşteri Silme",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    customerService.deleteCustomer(customerId);
                    refreshCustomerTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek müşteriyi seçin!");
            }
        });

        refreshCustomerTable();
        return panel;
    }

    private void showEditCustomerDialog(String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Müşteri bulunamadı!");
            return;
        }

        JDialog dialog = new JDialog(this, "Müşteri Düzenle", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(customer.getName());
        JTextField emailField = new JTextField(customer.getEmail());
        JPasswordField passwordField = new JPasswordField(customer.getPassword());
        JTextField phoneField = new JTextField(customer.getPhone());
        JCheckBox adminCheckBox = new JCheckBox("Admin Kullanıcı", customer.getIsAdmin());

        panel.add(new JLabel("Ad Soyad:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Şifre:"));
        panel.add(passwordField);
        panel.add(new JLabel("Telefon:"));
        panel.add(phoneField);
        panel.add(new JLabel("Admin:"));
        panel.add(adminCheckBox);

        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        saveButton.addActionListener(e -> {
            try {
                customer.setName(nameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPassword(new String(passwordField.getPassword()));
                customer.setPhone(phoneField.getText());
                customer.setIsAdmin(adminCheckBox.isSelected());

                customerService.updateCustomer(customer);
                JOptionPane.showMessageDialog(dialog, "Müşteri başarıyla güncellendi!");
                dialog.dispose();
                refreshCustomerTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditRoomDialog(String roomNumber) {
        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null) {
            JOptionPane.showMessageDialog(this,
                    "Oda bulunamadı!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Oda Düzenle", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField roomNumberField = new JTextField(room.getRoomNumber());
        JTextField roomPriceField = new JTextField(room.getRoomPrice());

        panel.add(new JLabel("Oda No:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Fiyat:"));
        panel.add(roomPriceField);

        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        saveButton.addActionListener(e -> {
            try {
                String newRoomNumber = roomNumberField.getText().trim();
                String newRoomPrice = roomPriceField.getText().trim();

                if (newRoomNumber.isEmpty() || newRoomPrice.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Lütfen tüm alanları doldurun!",
                            "Hata",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                room.setRoomNumber(newRoomNumber);
                room.setRoomPrice(newRoomPrice);

                roomService.updateRoom(room);

                JOptionPane.showMessageDialog(dialog,
                        "Oda başarıyla güncellendi!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshRoomTable();
                dialog.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Doğrulama Hatası",
                        JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Güncelleme hatası: " + ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Oda No", "Fiyat"};
        roomTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Yeni Oda");
        JButton editButton = new JButton("Düzenle");
        JButton deleteButton = new JButton("Sil");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddRoomDialog());
        editButton.addActionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow != -1) {
                String roomNumber = (String) roomTable.getValueAt(selectedRow, 0);
                showEditRoomDialog(roomNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen düzenlenecek odayı seçin!");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow != -1) {
                String roomNumber = (String) roomTable.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(this,
                        "Bu odayı silmek istediğinizden emin misiniz?",
                        "Oda Silme",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    roomService.deleteRoom(roomNumber);
                    refreshRoomTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek odayı seçin!");
            }
        });

        refreshRoomTable();
        return panel;
    }


    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Yeni Oda", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField roomNumberField = new JTextField();
        JTextField roomPriceField = new JTextField();

        panel.add(new JLabel("Oda No:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Fiyat:"));
        panel.add(roomPriceField);

        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        saveButton.addActionListener(e -> {
            try {
                String roomNum = roomNumberField.getText().trim();
                String roomPrice = roomPriceField.getText().trim();

                roomService.createRoom(roomNum, roomPrice);

                JOptionPane.showMessageDialog(dialog,
                        "Oda başarıyla eklendi!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshRoomTable();
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void refreshRoomTable() {
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
    }


    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Rezervasyon ID", "Müşteri", "Oda No", "Giriş Tarihi", "Çıkış Tarihi", "Fiyat", "Misafir Sayısı"};
        reservationTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Yeni Rezervasyon");
        JButton cancelButton = new JButton("Rezervasyonu İptal Et");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddReservationDialog());
        cancelButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow != -1) {
                String reservationId = (String) reservationTable.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(this,
                        "Bu rezervasyonu iptal etmek istediğinizden emin misiniz?",
                        "Rezervasyon İptali",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    reservationService.cancelReservation(reservationId);
                    refreshReservationTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal edilecek rezervasyonu seçin!");
            }
        });

        refreshReservationTable();
        return panel;
    }


    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Yeni Müşteri", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();
        JCheckBox adminCheckBox = new JCheckBox("Admin Kullanıcı");

        panel.add(new JLabel("Ad Soyad:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Şifre:"));
        panel.add(passwordField);
        panel.add(new JLabel("Telefon:"));
        panel.add(phoneField);
        panel.add(new JLabel("Admin:"));
        panel.add(adminCheckBox);

        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        saveButton.addActionListener(e -> {
            try {
                customerService.registerCustomer(
                        nameField.getText(),
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        phoneField.getText(),
                        adminCheckBox.isSelected()
                );
                JOptionPane.showMessageDialog(dialog, "Müşteri başarıyla kaydedildi!");
                dialog.dispose();
                refreshCustomerTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddReservationDialog() {
        JDialog dialog = new JDialog(this, "Yeni Rezervasyon", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Müşteri seçimi için özel bir model oluştur
        DefaultComboBoxModel<Customer> customerModel = new DefaultComboBoxModel<>();
        JComboBox<Customer> customerComboBox = new JComboBox<>(customerModel);
        for (Customer customer : customerService.getAllCustomers()) {
            customerModel.addElement(customer);
        }
        // ComboBox'ta nasıl görüneceğini ayarla
        customerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Customer) {
                    Customer customer = (Customer) value;
                    value = customer.getName() + " - " + customer.getPhone();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Oda seçimi için özel bir model oluştur
        DefaultComboBoxModel<Room> roomModel = new DefaultComboBoxModel<>();
        JComboBox<Room> roomComboBox = new JComboBox<>(roomModel);
        for (Room room : roomService.getAllRooms()) {
            roomModel.addElement(room);
        }
        // ComboBox'ta nasıl görüneceğini ayarla
        roomComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Room) {
                    Room room = (Room) value;
                    value = "Oda " + room.getRoomNumber() + " - " + room.getRoomPrice() + " TL";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        JTextField checkInField = new JTextField();
        JTextField checkOutField = new JTextField();
        JTextField guestsField = new JTextField();

        panel.add(new JLabel("Müşteri:"));
        panel.add(customerComboBox);
        panel.add(new JLabel("Oda:"));
        panel.add(roomComboBox);
        panel.add(new JLabel("Giriş Tarihi (dd/MM/yyyy):"));
        panel.add(checkInField);
        panel.add(new JLabel("Çıkış Tarihi (dd/MM/yyyy):"));
        panel.add(checkOutField);
        panel.add(new JLabel("Misafir Sayısı:"));
        panel.add(guestsField);

        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        saveButton.addActionListener(e -> {
            try {
                Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
                Room selectedRoom = (Room) roomComboBox.getSelectedItem();

                if (selectedCustomer == null || selectedRoom == null) {
                    throw new RuntimeException("Lütfen müşteri ve oda seçiniz!");
                }

                Date checkIn = dateFormat.parse(checkInField.getText());
                Date checkOut = dateFormat.parse(checkOutField.getText());
                int guests = Integer.parseInt(guestsField.getText());

                reservationService.createReservation(
                        selectedCustomer.getCustomerId(),
                        selectedRoom.getRoomNumber(),
                        checkIn,
                        checkOut,
                        Double.parseDouble(selectedRoom.getRoomPrice()),
                        guests
                );

                JOptionPane.showMessageDialog(dialog, "Rezervasyon başarıyla oluşturuldu!");
                refreshReservationTable();
                dialog.dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Geçersiz tarih formatı! Lütfen dd/MM/yyyy formatında giriniz.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Geçersiz misafir sayısı! Lütfen sayısal bir değer giriniz.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void refreshCustomerTable() {
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0);

        List<Customer> customers = customerService.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone()
            };
            model.addRow(row);
        }
    }

    private void refreshReservationTable() {
        DefaultTableModel model = (DefaultTableModel) reservationTable.getModel();
        model.setRowCount(0);

        List<Reservation> reservations = reservationService.getAllReservations();

        for (Reservation reservation : reservations) {
            Customer customer = customerService.getCustomerById(reservation.getCustomerId());
            Object[] row = {
                    reservation.getReservationId(),
                    customer.getName(),
                    reservation.getRoomNumber(),
                    dateFormat.format(reservation.getCheckInDate()),
                    dateFormat.format(reservation.getCheckOutDate()),
                    reservation.getPrice(),
                    reservation.getNumberOfGuests()
            };
            model.addRow(row);
        }
    }
}
