package com.abc.postpaid.user.service;

import com.abc.postpaid.user.dto.AdminCreateUserRequest;
import com.abc.postpaid.user.dto.AdminUpdateUserRequest;
import com.abc.postpaid.user.dto.SetPasswordRequest;
import com.abc.postpaid.user.dto.UserResponse;

import java.util.List;

public interface AdminUserService {
    Long createUser(AdminCreateUserRequest req);
    UserResponse getUser(Long id);
    List<UserResponse> listUsers();
    void updateUser(Long id, AdminUpdateUserRequest req);
    void setPassword(Long id, SetPasswordRequest req);
    // `disableUser` removed since `enabled` field removed from data model
    void deleteUser(Long id);
}
