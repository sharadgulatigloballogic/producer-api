package com.example.userapi;

import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void searchByUsernameReturnsJohnDoe() {
        List<User> result = userService.searchUsers("john", null);

        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
    }

    @Test
    void searchIsCaseInsensitiveAndMatchesEmail() {
        List<User> result = userService.searchUsers("EXAMPLE.COM", null);

        assertEquals(3, result.size());
    }

    @Test
    void searchMatchesFullName() {
        List<User> result = userService.searchUsers("SMITH", null);

        assertEquals(1, result.size());
        assertEquals("jane_smith", result.get(0).getUsername());
    }

    @Test
    void activeFilterReturnsUserOnlyWhenStatusMatches() {
        assertTrue(userService.searchUsers("doe", false).isEmpty());
        assertEquals(1, userService.searchUsers("doe", true).size());

        Long johnId = userService.getUserByUsername("john_doe").orElseThrow().getId();
        userService.deactivateUser(johnId);

        List<User> inactive = userService.searchUsers("doe", false);
        assertEquals(1, inactive.size());
        assertEquals("john_doe", inactive.get(0).getUsername());
        assertTrue(userService.searchUsers("doe", true).isEmpty());
    }

    @Test
    void searchWithNoMatchReturnsEmptyList() {
        assertTrue(userService.searchUsers("zzz", null).isEmpty());
    }

    @Test
    void searchHandlesUserWithNullFullName() {
        userService.createUser(new User(null, "nofullname", "nfn@test.org", null));

        List<User> byUsername = userService.searchUsers("nofull", null);
        assertEquals(1, byUsername.size());
        assertEquals("nofullname", byUsername.get(0).getUsername());

        assertTrue(userService.searchUsers("zzz", null).isEmpty());
        assertEquals(3, userService.searchUsers("example.com", null).size());
    }
}
