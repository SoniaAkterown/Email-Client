package com.example.emailwrapper.service;

import com.example.emailwrapper.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final String USERS_FILE_PATH = "users.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void register(String email, String password) throws IOException {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("User already exists");
            }
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        users.add(new User(email, passwordHash));

        saveUsers(users);
    }

    public boolean login(String email, String password) {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return BCrypt.checkpw(password, user.getPasswordHash());
            }
        }
        return false;
    }

    private List<User> loadUsers() {
        File file = new File(USERS_FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            Map<String, List<User>> data = objectMapper.readValue(file, new TypeReference<Map<String, List<User>>>() {
            });
            return data.getOrDefault("users", new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) throws IOException {
        Map<String, List<User>> data = Map.of("users", users);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE_PATH), data);
    }
}
