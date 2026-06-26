package com.shopsmart.dto;

import java.sql.Timestamp;

public class UserResponseDTO {

    private int id;
    private String name;
    private String email;
    private Timestamp createdAt;

    public UserResponseDTO(int id, String name, String email, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" + "Id = " + id + ", Name = " + name + ", Email = " + email + ", CreatedAt = " + createdAt + "}";
    }
}
