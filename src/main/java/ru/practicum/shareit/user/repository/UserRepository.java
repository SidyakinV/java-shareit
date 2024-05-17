package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User addUser(User user);

    User updateUser(User user);

    User getUser(Long userId);

    boolean deleteUser(Long userId);

    List<User> getAllUsers();

    List<User> findUserByEmail(String email);

}
