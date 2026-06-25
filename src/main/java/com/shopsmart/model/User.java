package com.shopsmart.model;

import java.sql.Timestamp;

public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    public User() {}

    // Constructor for creating a user BEFORE saving to the DB
    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Constructor for retrieving a user complete with database generated metadata
    public User(int id, String name, String email, String passwordHash, Timestamp createdAt) {
        this(name, email, passwordHash);
        this.id = id;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" + "Id = " + id + ", Name = " + name + ", Email = " + email + ", CreatedAt = " + createdAt + "}";
    }
}
