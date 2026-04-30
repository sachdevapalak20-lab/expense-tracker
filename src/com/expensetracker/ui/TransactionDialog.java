package com.expensetracker.ui;
 
import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
 
/**
 * TransactionDialog - Modal dialog for adding or editing a transaction.
 */
public class TransactionDialog extends JDialog {
 
    private static final Color BG        = new Color(25, 25, 45);
    private static final Color FIELD_BG  = new Color(40, 40, 70);
    private static final Color ACCENT    = new Color(99, 102, 241);
    private static final Color SUCCESS   = new Color(34, 197, 94);
    private static final Color DANGER    = new Color(239, 68, 68);
    private static final Color TEXT      = new Color(230, 230, 255);
    private static final Color SUBTLE    = new Color(140, 140, 180);
 
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BTN_FONT   = new Font("Segoe UI", Font.BOLD, 14);
 
    private JTextField titleField;
    private JTextField amountField;
    private JTextField dateField;
    private JTextArea descField;
    private JComboBox<Category> categoryCombo;
    private JComboBox<String> typeCombo;
    private JButton saveButton;
    private JLabel statusLabel;
 
    private User currentUser;
    private Transaction existingTransaction; // null = new, non-null = edit
    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;
    private boolean saved = false;
 
    // Constructor for NEW transaction
    public TransactionDialog(Frame parent, User user) {
        super(parent, "Add Transaction", true);
        this.currentUser = user;
        init();
    }
 
    // Constructor for EDITING existing transaction
    public TransactionDialog(Frame parent, User user, Transaction transaction) {
        super(parent, "Edit Transaction", true);
        this.currentUser = user;
        this.existingTransaction = transaction;
        init();
        populateFields(transaction);
    }
 
    private void init() {
        transactionDAO = new TransactionDAO();
        categoryDAO    = new CategoryDAO();
 
        setSize(460, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);
 
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
 
        // Type selector
        typeCombo = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        styleCombo(typeCombo);
        typeCombo.addActionListener(e -> refreshCategories());
 
        // Category selector
        categoryCombo = new JComboBox<>();
        styleCombo(categoryCombo);
        refreshCategories();
 
        // Fields
        titleField  = createField("e.g. Lunch at restaurant");
        amountField = createField("e.g. 250.00");
        dateField   = createField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        descField   = new JTextArea(3, 20);
        descField.setFont(INPUT_FONT);
        descField.setBackground(FIELD_BG);
        descField.setForeground(TEXT);
        descField.setCaretColor(TEXT);
        descField.setLineWrap(true);
        descField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
 
        saveButton  = createBtn("Save Transaction", ACCENT);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // Layout
        panel.add(makeRow("Type", typeCombo));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeRow("Category", categoryCombo));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeRow("Title", titleField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeRow("Amount (₹)", amountField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(makeRow("Date (YYYY-MM-DD)", dateField));
        panel.add(Box.createVerticalStrut(12));
 
        JLabel descLabel = new JLabel("Description (optional)");
        descLabel.setFont(LABEL_FONT);
        descLabel.setForeground(SUBTLE);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(5));
        JScrollPane descScroll = new JScrollPane(descField);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descScroll);
        panel.add(Box.createVerticalStrut(20));
        panel.add(saveButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(statusLabel);
 
        setContentPane(panel);
 
        saveButton.addActionListener(e -> handleSave());
    }
 
    private void refreshCategories() {
        String selectedType = (String) typeCombo.getSelectedItem();
        Category.Type cType = "INCOME".equals(selectedType) ? Category.Type.INCOME : Category.Type.EXPENSE;
        List<Category> cats = categoryDAO.getByType(currentUser.getId(), cType);
        categoryCombo.removeAllItems();
        for (Category c : cats) categoryCombo.addItem(c);
    }
 
    private void populateFields(Transaction t) {
        typeCombo.setSelectedItem(t.getType().name());
        refreshCategories();
        titleField.setText(t.getTitle());
        amountField.setText(String.valueOf(t.getAmount()));
        dateField.setText(t.getDate().toString());
        descField.setText(t.getDescription() != null ? t.getDescription() : "");
        // Select matching category
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getId() == t.getCategoryId()) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
    }
 
    private void handleSave() {
        String title  = titleField.getText().trim();
        String amtStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        Category cat   = (Category) categoryCombo.getSelectedItem();
 
        if (title.isEmpty() || amtStr.isEmpty() || dateStr.isEmpty() || cat == null) {
            status("Please fill in all required fields.", DANGER);
            return;
        }
 
        double amount;
        try { amount = Double.parseDouble(amtStr); }
        catch (NumberFormatException e) { status("Amount must be a number.", DANGER); return; }
 
        if (amount <= 0) { status("Amount must be greater than 0.", DANGER); return; }
 
        LocalDate date;
        try { date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException e) { status("Date format must be YYYY-MM-DD.", DANGER); return; }
 
        Transaction t = existingTransaction != null ? existingTransaction : new Transaction();
        t.setUserId(currentUser.getId());
        t.setCategoryId(cat.getId());
        t.setTitle(title);
        t.setAmount(amount);
        t.setType(Transaction.Type.valueOf((String) typeCombo.getSelectedItem()));
        t.setDate(date);
        t.setDescription(descField.getText().trim());
 
        boolean success;
        if (existingTransaction != null) {
            success = transactionDAO.updateTransaction(t);
        } else {
            success = transactionDAO.addTransaction(t) > 0;
        }
 
        if (success) {
            saved = true;
            status("Saved successfully!", SUCCESS);
            Timer timer = new Timer(600, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        } else {
            status("Failed to save. Please try again.", DANGER);
        }
    }
 
    public boolean isSaved() { return saved; }
 
    // ── UI Helpers ─────────────────────────────────────────────
    private JPanel makeRow(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setBackground(BG);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(SUBTLE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(Box.createVerticalStrut(4));
        row.add(field);
        return row;
    }
 
    private JTextField createField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(INPUT_FONT);
        f.setBackground(FIELD_BG);
        f.setForeground(new Color(140, 140, 180));
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(SUBTLE); }
            }
        });
        return f;
    }
 
    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(INPUT_FONT);
        combo.setBackground(FIELD_BG);
        combo.setForeground(TEXT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }
 
    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }
 
    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}
 