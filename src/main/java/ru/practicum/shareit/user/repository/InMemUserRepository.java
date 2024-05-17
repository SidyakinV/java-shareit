package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemUserRepository implements UserRepository {

    // Для скорости выборки данных по id лучше было бы использовать Map'у, однако тесты в Postman'е
    // предполагают оригинальную сортировку записей (в порядке их занесения)
    private final List<User> usersList = new ArrayList<>();
    private long lastId = 0;

    @Override
    public User addUser(User user) {
        user.setId(++lastId);
        usersList.add(user);
        return getUser(user.getId());
    }

    @Override
    public User updateUser(User user) {
        // Т.к. в контроллер могут приходить структуры с неполным набором полей, будем обновлять только
        // переданные поля
        User savedUser = findUserById(user.getId());
        if (savedUser != null) {
            if (user.getName() != null) {
                savedUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                savedUser.setEmail(user.getEmail());
            }
        }
        return savedUser;
    }

    @Override
    public User getUser(Long userId) {
        return findUserById(userId);
    }

    @Override
    public boolean deleteUser(Long userId) {
        int index = findUserIndexById(userId);
        if (index >= 0) {
            usersList.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersList);
    }

    @Override
    public List<User> findUserByEmail(String email) {
        return usersList.stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList());
    }

    private int findUserIndexById(long userId) {
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == userId) {
                return i;
            }
        }
        return -1;
    }

    private User findUserById(long userId) {
        int index = findUserIndexById(userId);
        return index < 0 ? null : usersList.get(index);
    }

}
