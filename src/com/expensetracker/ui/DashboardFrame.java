package com.expensetracker.ui;

import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DashboardFrame - The main screen after login.
 * Shows summary cards, transaction table, and navigation.
 */
public class DashboardFrame extends JFrame {

    // ── Colors ─────────────────────────────────────────────────
    private static final Color BG          = new Color(15, 15, 30);
    private static final Color SIDEBAR_BG  = new Color(22, 22, 45);
    private static final Color CARD_BG     = new Color(30, 30, 55);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color SUCCESS     = new Color(34, 197, 94);
    private static final Color DANGER      = new Color(239, 68, 68);
    private static final Color WARNING     = new Color(251, 191, 36);
    private static final Color TEXT        = new Color(230, 230, 255);
    private static final Color SUBTLE      = new Color(140, 140, 180);
    private static final Color TABLE_ALT   = new Color(35, 35, 60);
    private static final Color TABLE_HDR   = new Color(50, 50, 85);

    private static final Font TITLE_FONT   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font CARD_FONT    = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font LABEL_FONT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BTN_FONT     = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT   = new Font("Segoe UI", Font.PLAIN, 13);

    private User currentUser;
    private TransactionDAO transactionDAO;

    // ── UI refs ────────────────────────────────────────────────
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JLabel greetingLabel;

    private static final String[] MONTHS = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public DashboardFrame(User user) {
        this.currentUser  = user;
        this.transactionDAO = new TransactionDAO();
        initUI();
        loadData();
    }

    // ── UI Setup ───────────────────────────────────────────────
    private void initUI() {
        setTitle("Expense Tracker — Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar ────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // App icon + name
        JLabel appIcon = new JLabel("💰");
        appIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        appIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(TEXT);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User info
        greetingLabel = new JLabel("Hi, " + currentUser.getFullName().split(" ")[0] + "!");
        greetingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        greetingLabel.setForeground(SUBTLE);
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(60, 60, 90));

        // Nav buttons
        JButton btnDashboard = sidebarBtn("📊  Dashboard");
        JButton btnAdd       = sidebarBtn("➕  Add Transaction");
        JButton btnLogout    = sidebarBtn("🚪  Logout");
        btnLogout.setForeground(DANGER);

        btnAdd.addActionListener(e -> openAddDialog());
        btnLogout.addActionListener(e -> logout());

        // Month / Year filter
        JLabel filterLabel = new JLabel("Filter by Period");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(SUBTLE);
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        monthCombo = new JComboBox<>(MONTHS);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        styleCombo(monthCombo);

        String[] years = {"2022","2023","2024","2025","2026"};
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        styleCombo(yearCombo);

        JButton applyFilter = createBtn("Apply Filter", ACCENT);
        applyFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        applyFilter.addActionListener(e -> loadData());

        sidebar.add(appIcon);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(greetingLabel);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnAdd);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(filterLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(monthCombo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(yearCombo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(applyFilter);

        return sidebar;
    }

    // ── Main Content ───────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        JLabel pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(TITLE_FONT);
        pageTitle.setForeground(TEXT);
        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setFont(LABEL_FONT);
        dateLabel.setForeground(SUBTLE);
        header.add(pageTitle, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        // Summary cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBackground(BG);
        cardsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        totalIncomeLabel  = new JLabel("₹0.00");
        totalExpenseLabel = new JLabel("₹0.00");
        balanceLabel      = new JLabel("₹0.00");

        cardsPanel.add(buildCard("Total Income", totalIncomeLabel, SUCCESS, "💚"));
        cardsPanel.add(buildCard("Total Expenses", totalExpenseLabel, DANGER, "🔴"));
        cardsPanel.add(buildCard("Net Balance", balanceLabel, WARNING, "💛"));

        // Table
        JPanel tablePanel = buildTablePanel();

        main.add(header, BorderLayout.NORTH);
        main.add(cardsPanel, BorderLayout.CENTER);
        main.add(tablePanel, BorderLayout.SOUTH);

        // Make table take remaining space
        main.setLayout(new BorderLayout());
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BG);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(cardsPanel, BorderLayout.CENTER);

        main.add(topSection, BorderLayout.NORTH);
        main.add(tablePanel, BorderLayout.CENTER);

        return main;
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color accentColor, String icon) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor.darker(), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(SUBTLE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(CARD_FONT);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        // Table header row
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(BG);
        tableHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel tableTitle = new JLabel("Recent Transactions");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(TEXT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG);
        JButton addBtn    = createBtn("+ Add", SUCCESS);
        JButton editBtn   = createBtn("✏ Edit", ACCENT);
        JButton deleteBtn = createBtn("🗑 Delete", DANGER);

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(btnPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"#", "Date", "Title", "Category", "Type", "Amount (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        transactionTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 80)));

        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable() {
        transactionTable.setBackground(CARD_BG);
        transactionTable.setForeground(TEXT);
        transactionTable.setFont(TABLE_FONT);
        transactionTable.setRowHeight(36);
        transactionTable.setShowGrid(false);
        transactionTable.setIntercellSpacing(new Dimension(0, 1));
        transactionTable.setSelectionBackground(ACCENT);
        transactionTable.setSelectionForeground(Color.WHITE);
        transactionTable.setFillsViewportHeight(true);

        JTableHeader header = transactionTable.getTableHeader();
        header.setBackground(TABLE_HDR);
        header.setForeground(TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Color rows by type
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? ACCENT : (row % 2 == 0 ? CARD_BG : TABLE_ALT));
                setForeground(sel ? Color.WHITE : TEXT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (col == 4 && v != null) {
                    if ("INCOME".equals(v.toString()))  setForeground(sel ? Color.WHITE : SUCCESS);
                    if ("EXPENSE".equals(v.toString())) setForeground(sel ? Color.WHITE : DANGER);
                }
                if (col == 5 && v != null) {
                    String type = (String) t.getValueAt(row, 4);
                    if ("INCOME".equals(type))  setForeground(sel ? Color.WHITE : SUCCESS);
                    if ("EXPENSE".equals(type)) setForeground(sel ? Color.WHITE : DANGER);
                }
                return this;
            }
        });
    }

    // ── Data Loading ───────────────────────────────────────────
    private void loadData() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year  = Integer.parseInt((String) yearCombo.getSelectedItem());

        // Summary
        double income  = transactionDAO.getTotalIncome(currentUser.getId(), month, year);
        double expense = transactionDAO.getTotalExpense(currentUser.getId(), month, year);
        double balance = income - expense;

        totalIncomeLabel.setText(String.format("₹%,.2f", income));
        totalExpenseLabel.setText(String.format("₹%,.2f", expense));
        balanceLabel.setText(String.format("₹%,.2f", balance));
        balanceLabel.setForeground(balance >= 0 ? SUCCESS : DANGER);

        // Table
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getByMonth(currentUser.getId(), month, year);
        int i = 1;
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                i++,
                t.getDate().toString(),
                t.getTitle(),
                t.getCategoryName() != null ? t.getCategoryName() : "—",
                t.getType().name(),
                String.format("%,.2f", t.getAmount())
            });
        }
    }

    // ── Actions ────────────────────────────────────────────────
    private void openAddDialog() {
        TransactionDialog dialog = new TransactionDialog(this, currentUser);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadData();
    }

    private void openEditDialog() {
        int row = transactionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int month = monthCombo.getSelectedIndex() + 1;
        int year  = Integer.parseInt((String) yearCombo.getSelectedItem());
        List<Transaction> list = transactionDAO.getByMonth(currentUser.getId(), month, year);
        if (row < list.size()) {
            TransactionDialog dialog = new TransactionDialog(this, currentUser, list.get(row));
            dialog.setVisible(true);
            if (dialog.isSaved()) loadData();
        }
    }

    private void deleteSelected() {
        int row = transactionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this transaction?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int month = monthCombo.getSelectedIndex() + 1;
            int year  = Integer.parseInt((String) yearCombo.getSelectedItem());
            List<Transaction> list = transactionDAO.getByMonth(currentUser.getId(), month, year);
            if (row < list.size()) {
                boolean ok = transactionDAO.deleteTransaction(list.get(row).getId(), currentUser.getId());
                if (ok) loadData();
                else JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // ── UI Helpers ─────────────────────────────────────────────
    private JButton sidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(40, 40, 70)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(SIDEBAR_BG); }
        });
        return btn;
    }

    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(new Color(40, 40, 70));
        combo.setForeground(TEXT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}