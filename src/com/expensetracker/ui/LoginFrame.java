package com.expensetracker.ui;
 
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
 
/**
 * LoginFrame - GUI screen for user login and registration.
 * Built using Java Swing.
 */
public class LoginFrame extends JFrame {
 
    // ── Colors & Fonts ─────────────────────────────────────────
    private static final Color BG_COLOR      = new Color(18, 18, 35);
    private static final Color CARD_COLOR    = new Color(30, 30, 55);
    private static final Color ACCENT        = new Color(99, 102, 241);
    private static final Color ACCENT_HOVER  = new Color(79, 82, 221);
    private static final Color TEXT_COLOR    = new Color(230, 230, 255);
    private static final Color SUBTLE        = new Color(140, 140, 180);
    private static final Color SUCCESS       = new Color(34, 197, 94);
    private static final Color DANGER        = new Color(239, 68, 68);
    private static final Color FIELD_BG      = new Color(45, 45, 75);
    private static final Color FIELD_BORDER  = new Color(80, 80, 120);
 
    private static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font LABEL_FONT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INPUT_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BTN_FONT    = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font SMALL_FONT  = new Font("Segoe UI", Font.PLAIN, 12);
 
    // ── Components ─────────────────────────────────────────────
    private JTabbedPane tabbedPane;
 
    // Login tab
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JLabel loginStatusLabel;
 
    // Register tab
    private JTextField regFullNameField;
    private JTextField regEmailField;
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JButton registerButton;
    private JLabel regStatusLabel;
 
    private UserDAO userDAO;
 
    // ── Constructor ────────────────────────────────────────────
    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }
 
    private void initUI() {
        setTitle("Expense Tracker — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 580);
        setLocationRelativeTo(null);
        setResizable(false);
 
        // Main background panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
 
        // ── Header ──────────────────────────────────────────────
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
 
        JLabel iconLabel = new JLabel("💰", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel titleLabel = new JLabel("Expense Tracker", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel subtitleLabel = new JLabel("Manage your money smartly", SwingConstants.CENTER);
        subtitleLabel.setFont(SMALL_FONT);
        subtitleLabel.setForeground(SUBTLE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(24));
 
        // ── Tabbed Pane ─────────────────────────────────────────
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(CARD_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(BTN_FONT);
        tabbedPane.addTab("  Login  ", buildLoginPanel());
        tabbedPane.addTab("  Register  ", buildRegisterPanel());
 
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
 
        setContentPane(mainPanel);
    }
 
    // ── Login Panel ────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        loginUsernameField   = createTextField("Enter your username");
        loginPasswordField   = createPasswordField("Enter your password");
        loginButton          = createButton("Login", ACCENT);
        loginStatusLabel     = createStatusLabel();
 
        panel.add(createLabel("Username"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(loginUsernameField);
        panel.add(Box.createVerticalStrut(14));
        panel.add(createLabel("Password"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(loginPasswordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginStatusLabel);
 
        // Default credentials hint
        JLabel hintLabel = new JLabel("Default: admin / admin123");
        hintLabel.setFont(SMALL_FONT);
        hintLabel.setForeground(SUBTLE);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(hintLabel);
 
        // Login button action
        loginButton.addActionListener(e -> handleLogin());
 
        // Press Enter to login
        loginPasswordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
 
        return panel;
    }
 
    // ── Register Panel ─────────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        regFullNameField        = createTextField("Enter your full name");
        regEmailField           = createTextField("Enter your email");
        regUsernameField        = createTextField("Choose a username");
        regPasswordField        = createPasswordField("Choose a password");
        regConfirmPasswordField = createPasswordField("Confirm your password");
        registerButton          = createButton("Create Account", SUCCESS);
        regStatusLabel          = createStatusLabel();
 
        panel.add(createLabel("Full Name"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regFullNameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLabel("Email"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regEmailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLabel("Username"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regUsernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLabel("Password"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLabel("Confirm Password"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regConfirmPasswordField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(regStatusLabel);
 
        registerButton.addActionListener(e -> handleRegister());
 
        return panel;
    }
 
    // ── Logic ──────────────────────────────────────────────────
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
 
        if (username.isEmpty() || password.isEmpty()) {
            showStatus(loginStatusLabel, "Please fill in all fields.", DANGER);
            return;
        }
 
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
 
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            protected User doInBackground() {
                return userDAO.authenticate(username, password);
            }
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        showStatus(loginStatusLabel, "Login successful! Opening dashboard...", SUCCESS);
                        Timer timer = new Timer(800, ev -> {
                            dispose();
                            new DashboardFrame(user).setVisible(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus(loginStatusLabel, "Invalid username or password.", DANGER);
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                    }
                } catch (Exception ex) {
                    showStatus(loginStatusLabel, "Error: " + ex.getMessage(), DANGER);
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        worker.execute();
    }
 
    private void handleRegister() {
        String fullName  = regFullNameField.getText().trim();
        String email     = regEmailField.getText().trim();
        String username  = regUsernameField.getText().trim();
        String password  = new String(regPasswordField.getPassword());
        String confirm   = new String(regConfirmPasswordField.getPassword());
 
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showStatus(regStatusLabel, "Full name, username and password are required.", DANGER);
            return;
        }
        if (!password.equals(confirm)) {
            showStatus(regStatusLabel, "Passwords do not match.", DANGER);
            return;
        }
        if (password.length() < 6) {
            showStatus(regStatusLabel, "Password must be at least 6 characters.", DANGER);
            return;
        }
        if (userDAO.usernameExists(username)) {
            showStatus(regStatusLabel, "Username already taken. Choose another.", DANGER);
            return;
        }
 
        User newUser = new User(username, password, email, fullName);
        boolean success = userDAO.register(newUser);
 
        if (success) {
            showStatus(regStatusLabel, "Account created! You can now login.", SUCCESS);
            regFullNameField.setText("");
            regEmailField.setText("");
            regUsernameField.setText("");
            regPasswordField.setText("");
            regConfirmPasswordField.setText("");
            Timer t = new Timer(1200, e -> tabbedPane.setSelectedIndex(0));
            t.setRepeats(false);
            t.start();
        } else {
            showStatus(regStatusLabel, "Registration failed. Try again.", DANGER);
        }
    }
 
    // ── UI Helpers ─────────────────────────────────────────────
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(SUBTLE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
 
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        styleField(field, placeholder);
        return field;
    }
 
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        styleField(field, placeholder);
        return field;
    }
 
    private void styleField(JTextField field, String placeholder) {
        field.setFont(INPUT_FONT);
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        // Placeholder text
        field.setText(placeholder);
        field.setForeground(SUBTLE);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(SUBTLE);
                }
            }
        });
    }
 
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }
 
    private JLabel createStatusLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(SMALL_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
 
    private void showStatus(JLabel label, String message, Color color) {
        label.setText(message);
        label.setForeground(color);
    }
}
 