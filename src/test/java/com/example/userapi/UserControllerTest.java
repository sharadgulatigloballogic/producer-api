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
import static org.mockito.Mockito.verifyNoInteractions;
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
    void searchWithoutQReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void searchWithBlankQReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/search").param("q", " "))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void searchWithMatchReturnsUsersAndIsNotTreatedAsId() throws Exception {
        when(userService.searchUsers("john", null))
                .thenReturn(List.of(new User(2L, "john_doe", "john@example.com", "John Doe")));

        mockMvc.perform(get("/api/users/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("john_doe"));
    }

    @Test
    void searchWithNoMatchReturnsEmptyListWith200() throws Exception {
        when(userService.searchUsers(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search").param("q", "zzz").param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
