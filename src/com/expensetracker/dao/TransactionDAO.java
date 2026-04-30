package com.expensetracker.dao;

import com.expensetracker.model.Transaction;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAO - CRUD operations for income/expense transactions.
 */
public class TransactionDAO {

    private Connection conn;

    public TransactionDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /** Inserts a new transaction. Returns generated ID or -1. */
    public int addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (user_id, category_id, title, amount, type, date, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getUserId());
            ps.setInt(2, t.getCategoryId());
            ps.setString(3, t.getTitle());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getType().name());
            ps.setDate(6, Date.valueOf(t.getDate()));
            ps.setString(7, t.getDescription());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] addTransaction error: " + e.getMessage());
        }
        return -1;
    }

    /** Updates an existing transaction. */
    public boolean updateTransaction(Transaction t) {
        String sql = "UPDATE transactions SET category_id=?, title=?, amount=?, type=?, date=?, description=? WHERE id=? AND user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getCategoryId());
            ps.setString(2, t.getTitle());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getType().name());
            ps.setDate(5, Date.valueOf(t.getDate()));
            ps.setString(6, t.getDescription());
            ps.setInt(7, t.getId());
            ps.setInt(8, t.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] updateTransaction error: " + e.getMessage());
            return false;
        }
    }

    /** Deletes a transaction by ID. */
    public boolean deleteTransaction(int transactionId, int userId) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] deleteTransaction error: " + e.getMessage());
            return false;
        }
    }

    /** Returns all transactions for a user, newest first. */
    public List<Transaction> getAllByUser(int userId) {
        return query("SELECT t.*, c.name AS category_name FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? ORDER BY t.date DESC, t.created_at DESC", userId);
    }

    /** Returns transactions filtered by month and year. */
    public List<Transaction> getByMonth(int userId, int month, int year) {
        String sql = "SELECT t.*, c.name AS category_name FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND MONTH(t.date) = ? AND YEAR(t.date) = ? " +
                     "ORDER BY t.date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            return extractList(ps.executeQuery());
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] getByMonth error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Returns total income for a user in a given month/year. */
    public double getTotalIncome(int userId, int month, int year) {
        return getTotal(userId, month, year, "INCOME");
    }

    /** Returns total expense for a user in a given month/year. */
    public double getTotalExpense(int userId, int month, int year) {
        return getTotal(userId, month, year, "EXPENSE");
    }

    private double getTotal(int userId, int month, int year, String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id=? AND MONTH(date)=? AND YEAR(date)=? AND type=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setString(4, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] getTotal error: " + e.getMessage());
        }
        return 0.0;
    }

    /** Returns category-wise expense totals for pie chart. */
    public List<Object[]> getCategoryTotals(int userId, int month, int year) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT c.name, SUM(t.amount) AS total FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id=? AND t.type='EXPENSE' AND MONTH(t.date)=? AND YEAR(t.date)=? " +
                     "GROUP BY c.name ORDER BY total DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{rs.getString("name"), rs.getDouble("total")});
            }
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] getCategoryTotals error: " + e.getMessage());
        }
        return result;
    }

    // ── Internal helpers ───────────────────────────────────────
    private List<Transaction> query(String sql, int userId) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return extractList(ps.executeQuery());
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Transaction> extractList(ResultSet rs) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        while (rs.next()) {
            Transaction t = new Transaction();
            t.setId(rs.getInt("id"));
            t.setUserId(rs.getInt("user_id"));
            t.setCategoryId(rs.getInt("category_id"));
            t.setCategoryName(rs.getString("category_name"));
            t.setTitle(rs.getString("title"));
            t.setAmount(rs.getDouble("amount"));
            t.setType(Transaction.Type.valueOf(rs.getString("type")));
            t.setDate(rs.getDate("date").toLocalDate());
            t.setDescription(rs.getString("description"));
            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) t.setCreatedAt(ts.toLocalDateTime());
            list.add(t);
        }
        return list;
    }
}