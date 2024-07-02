package com.example.clipease;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClipboardDB {

    private static final String DB_URL = "jdbc:sqlite:clipboard.db";

    public ClipboardDB() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS clipboard_history (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " content TEXT NOT NULL,\n"
                + " timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveClipboardContent(String content) {
        String sql = "INSERT INTO clipboard_history(content) VALUES(?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getClipboardHistory() {
        List<String> history = new ArrayList<>();
        String sql = "SELECT content FROM clipboard_history ORDER BY timestamp DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.add(rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}
