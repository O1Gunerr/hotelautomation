package com.hotelautomation.gui;

import com.hotelautomation.model.Customer;
import com.hotelautomation.service.CustomerService;
import com.hotelautomation.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginFrame extends JFrame {
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final CustomerService customerService;
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);

    public LoginFrame() {
        this.customerService = new CustomerService();

        setTitle("Otel Yönetim Sistemi");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("Otel Yönetim Sistemi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        logoPanel.add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        styleTextField(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        styleTextField(passwordField);

        KeyListener enterListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };

        emailField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton loginButton = new JButton("Giriş Yap");
        JButton registerButton = new JButton("Kayıt Ol");

        styleButton(loginButton);
        styleButton(registerButton);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegisterDialog());

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(registerButton);

        mainPanel.add(logoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen email ve şifre alanlarını doldurun.",
                    "Hata",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (email.equals("admin@gmail.com") && password.equals("deepp123")) {
            Customer adminCustomer = new Customer("Admin","admin@gmail.com","deepp123","123456789",true);
            adminCustomer.setCustomerId("admin");
            adminCustomer.setName("Admin");
            adminCustomer.setEmail("admin@gmail.com");
            adminCustomer.setIsAdmin(true);
            new HotelManagementGUI(adminCustomer).setVisible(true);
            this.dispose();
            return;
        }

        try {
            Customer customer = customerService.login(email, password);
            if (customer != null) {
                if (customer.getIsAdmin()) {
                    new HotelManagementGUI(customer).setVisible(true);
                } else {
                    new CustomerPanel(customer).setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Geçersiz email veya şifre!",
                        "Giriş Hatası",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Giriş hatası: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Yeni Kullanıcı Kaydı", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(backgroundColor);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField phoneField = new JTextField();

        styleTextField(nameField);
        styleTextField(emailField);
        styleTextField(passwordField);
        styleTextField(phoneField);

        formPanel.add(new JLabel("Ad Soyad:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Şifre:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Telefon:"));
        formPanel.add(phoneField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        JButton saveButton = new JButton("Kaydet");
        JButton cancelButton = new JButton("İptal");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty() ||
                        emailField.getText().trim().isEmpty() ||
                        new String(passwordField.getPassword()).trim().isEmpty() ||
                        phoneField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Lütfen tüm alanları doldurun.",
                            "Hata",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                customerService.registerCustomer(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        new String(passwordField.getPassword()).trim(),
                        phoneField.getText().trim(),
                        false
                );
                JOptionPane.showMessageDialog(dialog,
                        "Kayıt başarıyla tamamlandı!",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Kayıt hatası: " + ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            DatabaseConnection.setupDatabase();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}