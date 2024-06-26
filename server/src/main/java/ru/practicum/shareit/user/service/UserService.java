package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    List<User> getAllUsers();

}
