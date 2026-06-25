package com.shopsmart.dao;

import com.shopsmart.model.User;

import java.util.Optional;

public interface UserDAO {

    boolean registerUser(User user);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(int id);
}
