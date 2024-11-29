package org.example;

import java.sql.*;

public class DatabaseHelper {
    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:connect4db.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(String name) {
        try {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE name = ?");
            checkStatement.setString(1, name);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO players (name, wins) VALUES (?, 0)");
                insertStatement.setString(1, name);
                insertStatement.executeUpdate();
                System.out.println("Player '" + name + "' added to the database.");}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWins(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE players SET wins = wins + 1 WHERE name = ?");
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getWins(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT wins FROM players WHERE name = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("wins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 if player not found
    }
}
