package com.expensetracker.dao;

import com.expensetracker.model.Category;
import com.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO - CRUD for transaction categories.
 */
public class CategoryDAO {

    private Connection conn;

    public CategoryDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /** Returns all categories visible to a user (global + user-specific). */
    public List<Category> getAllForUser(int userId) {
        String sql = "SELECT * FROM categories WHERE user_id IS NULL OR user_id = ? ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] getAllForUser error: " + e.getMessage());
        }
        return list;
    }

    /** Returns categories filtered by type. */
    public List<Category> getByType(int userId, Category.Type type) {
        String sql = "SELECT * FROM categories WHERE (user_id IS NULL OR user_id = ?) AND type = ? ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] getByType error: " + e.getMessage());
        }
        return list;
    }

    /** Adds a custom category for a user. */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (name, type, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getType().name());
            if (category.getUserId() != null) ps.setInt(3, category.getUserId());
            else ps.setNull(3, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] addCategory error: " + e.getMessage());
            return false;
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setType(Category.Type.valueOf(rs.getString("type")));
        int uid = rs.getInt("user_id");
        c.setUserId(rs.wasNull() ? null : uid);
        return c;
    }
}