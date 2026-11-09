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
    void searchByUsernameFragmentReturnsJohnDoe() {
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
        List<User> result = userService.searchUsers("jane SMITH", null);

        assertEquals(1, result.size());
        assertEquals("jane_smith", result.get(0).getUsername());
    }

    @Test
    void activeFalseReturnsEmptyUntilUserIsDeactivated() {
        assertTrue(userService.searchUsers("doe", false).isEmpty());

        User john = userService.getUserByUsername("john_doe").orElseThrow();
        userService.deactivateUser(john.getId());

        List<User> result = userService.searchUsers("doe", false);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
    }

    @Test
    void activeTrueExcludesDeactivatedUsers() {
        User john = userService.getUserByUsername("john_doe").orElseThrow();
        userService.deactivateUser(john.getId());

        assertTrue(userService.searchUsers("doe", true).isEmpty());
    }

    @Test
    void noMatchReturnsEmptyList() {
        assertTrue(userService.searchUsers("zzz", null).isEmpty());
    }

    @Test
    void userWithNullFullNameDoesNotCauseErrorAndNullFieldNeverMatches() {
        userService.createUser(new User(null, "nofullname", "nofull@other.org", null));

        List<User> byUsername = userService.searchUsers("nofullname", null);
        assertEquals(1, byUsername.size());
        assertTrue(userService.searchUsers("null", null).isEmpty());
        assertEquals(3, userService.searchUsers("example.com", null).size());
    }
}
