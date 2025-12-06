package com.abc.postpaid.user.dto;

public class AdminUpdateUserRequest {
    private String email;
    private String role;

    public AdminUpdateUserRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
