package com.fitsync.service;

import com.fitsync.dao.UserDao;
import com.fitsync.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public boolean register(String name, String email, String password,
                            int age, String gender,
                            double heightCm, double weightKg) {

        if (userDao.emailExists(email)) {
            return false;
        }

        String createdAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        User user = new User(name, email, password, age, gender,
                heightCm, weightKg, createdAt);

        return userDao.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}