package com.abc.postpaid.user.controller;

import com.abc.postpaid.user.dto.AdminCreateUserRequest;
import com.abc.postpaid.user.dto.AdminUpdateUserRequest;
import com.abc.postpaid.user.dto.SetPasswordRequest;
import com.abc.postpaid.user.dto.UserResponse;
import com.abc.postpaid.user.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private com.abc.postpaid.user.controller.AdminUserController controller;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setUsername("newuser");
        req.setEmail("new@example.com");
        req.setPassword("P@ssword1");
        req.setRole("customer");

        when(adminUserService.createUser(any())).thenReturn(42L);

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value(42));

        verify(adminUserService, times(1)).createUser(any());
    }

    @Test
    void listUsers_returnsOk() throws Exception {
        UserResponse u = new UserResponse();
        u.setUserId(1L); u.setUsername("admin"); u.setEmail("admin@example.com"); u.setRole("admin"); u.setCreatedAt(OffsetDateTime.now());

        when(adminUserService.listUsers()).thenReturn(List.of(u));

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));

        verify(adminUserService, times(1)).listUsers();
    }

    @Test
    void getUser_returnsOk() throws Exception {
        UserResponse u = new UserResponse();
        u.setUserId(5L); u.setUsername("bob");

        when(adminUserService.getUser(5L)).thenReturn(u);

        mvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"));

        verify(adminUserService, times(1)).getUser(5L);
    }

    @Test
    void updateUser_callsService() throws Exception {
        AdminUpdateUserRequest req = new AdminUpdateUserRequest();
        req.setEmail("x@example.com");

        mvc.perform(put("/api/users/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(adminUserService, times(1)).updateUser(eq(7L), any());
    }

    @Test
    void setPassword_callsService() throws Exception {
        SetPasswordRequest req = new SetPasswordRequest();
        req.setPassword("NewP@ss1");

        mvc.perform(post("/api/users/8/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(adminUserService, times(1)).setPassword(eq(8L), any());
    }

    @Test
    void deleteUser_callsService() throws Exception {
        mvc.perform(delete("/api/users/9"))
                .andExpect(status().isNoContent());

        verify(adminUserService, times(1)).deleteUser(9L);
    }
}
