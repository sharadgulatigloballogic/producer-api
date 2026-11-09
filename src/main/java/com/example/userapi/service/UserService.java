package com.example.userapi.service;

import com.example.userapi.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        createUser(new User(null, "admin", "admin@example.com", "Administrator"));
        createUser(new User(null, "john_doe", "john@example.com", "John Doe"));
        createUser(new User(null, "jane_smith", "jane@example.com", "Jane Smith"));
    }

    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> getUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<User> getActiveUsers() {
        return users.values().stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }

    public List<User> searchUsers(String q, Boolean active) {
        if (q == null || q.isBlank()) {
            return new ArrayList<>();
        }
        String needle = q.toLowerCase();
        return users.values().stream()
                .filter(u -> active == null || u.isActive() == active)
                .filter(u -> contains(u.getUsername(), needle)
                        || contains(u.getEmail(), needle)
                        || contains(u.getFullName(), needle))
                .collect(Collectors.toList());
    }

    private boolean contains(String field, String needle) {
        return field != null && field.toLowerCase().contains(needle);
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = users.get(id);
        if (existing == null) {
            return null;
        }
        
        updatedUser.setId(id);
        updatedUser.setCreatedAt(existing.getCreatedAt());
        users.put(id, updatedUser);
        return updatedUser;
    }

    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    public boolean deactivateUser(Long id) {
        User user = users.get(id);
        if (user != null) {
            user.setActive(false);
            return true;
        }
        return false;
    }

    public boolean activateUser(Long id) {
        User user = users.get(id);
        if (user != null) {
            user.setActive(true);
            return true;
        }
        return false;
    }
}
