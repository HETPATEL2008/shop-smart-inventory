package com.shopsmart.dao;

import com.shopsmart.config.DatabaseConfig;

import com.shopsmart.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    // Core SQL queries
    private static final String INSERT_USER = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
    private static final String SELECT_BY_EMAIL = "SELECT id, name, email, password_hash, created_at FROM users WHERE email = ?";
    private static final String SELECT_BY_ID = "SELECT id, name, email, password_hash, created_at FROM users WHERE id = ?";

    // Method for register new user
    @Override
    public boolean registerUser(User user) {

        try(Connection connection = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Method for find user by email
    @Override
    public Optional<User> getUserByEmail(String email) {

        try(Connection connection = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_EMAIL)) {

            preparedStatement.setString(1, email);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    User user = new User(resultSet.getInt("id"),
                                         resultSet.getString("name"),
                                         resultSet.getString("email"),
                                         resultSet.getString("password_hash"),
                                         resultSet.getTimestamp("created_at"));

                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

    // Method for find user by id
    @Override
    public Optional<User> getUserById(int id) {

        try(Connection connection = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID)) {

            preparedStatement.setInt(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    User user = new User(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getTimestamp("created_at"));

                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }
}
