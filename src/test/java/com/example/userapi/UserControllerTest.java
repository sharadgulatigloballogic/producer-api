package com.example.userapi;

import com.example.userapi.controller.UserController;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void searchReturnsMatchingUsers() throws Exception {
        when(userService.searchUsers("john", null))
                .thenReturn(List.of(new User(2L, "john_doe", "john@example.com", "John Doe")));

        mockMvc.perform(get("/api/users/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("john_doe"));

        verify(userService, never()).getUserById(any());
    }

    @Test
    void searchPassesActiveFilter() throws Exception {
        when(userService.searchUsers("doe", false)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search").param("q", "doe").param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService).searchUsers(eq("doe"), eq(false));
    }

    @Test
    void searchWithNoMatchReturnsOkAndEmptyList() throws Exception {
        when(userService.searchUsers("zzz", null)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search").param("q", "zzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchWithMissingQReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).searchUsers(any(), any());
    }

    @Test
    void searchWithBlankQReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/search").param("q", " "))
                .andExpect(status().isBadRequest());

        verify(userService, never()).searchUsers(any(), any());
    }

    @Test
    void searchPathIsNotRoutedToGetUserById() throws Exception {
        when(userService.searchUsers("john", null)).thenReturn(List.of());

        // Would be 400 (type mismatch for Long id) if routed to /{id}
        mockMvc.perform(get("/api/users/search").param("q", "john"))
                .andExpect(status().isOk());

        verify(userService, never()).getUserById(any());
    }
}
