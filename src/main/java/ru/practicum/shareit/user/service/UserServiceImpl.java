package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        checkEmailExists(user.getEmail());
        User savedUser = userRepository.addUser(user);
        log.info("Добавлен новый пользователь: {}", savedUser);
        return savedUser;
    }

    @Override
    public User updateUser(User user) {
        // Из-за того, что в контроллер могут приходить структуры с неполным набором полей (Postman-тесты),
        // валидация на уровне контроллера отключена, а здесь проверяются только переданные (not null) поля
        checkNotBlankField(user.getName(), "Имя");
        checkNotBlankField(user.getEmail(), "EMail");
        checkValidEmail(user.getEmail());
        checkEmailExists(user.getEmail(), user.getId());

        User savedUser = userRepository.updateUser(user);
        checkUserExists(savedUser, user.getId());
        log.info("Информация о пользователе изменена: {}", savedUser);
        return savedUser;
    }

    @Override
    public User getUser(Long userId) {
        User user = userRepository.getUser(userId);
        checkUserExists(user, userId);
        log.info("Получена информация о пользователе c id={}: {}", userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.deleteUser(userId)) {
            log.info("Пользователь с id={} удален", userId);
        } else {
            log.info("Пользователь с id={} не найден", userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        log.info("Получен список всех пользователей");
        return users;
    }

    private void checkEmailExists(String email) {
        if (userRepository.findUserByEmail(email).size() > 0) {
            throw new ConflictException(
                    new Violation("email", "Пользователь с указанным email уже существует!"));
        }
    }

    private void checkEmailExists(String email, Long userId) {
        List<User> users = userRepository.findUserByEmail(email);
        for (User user : users) {
            if (user.getId().equals(userId)) {
                throw new ConflictException(
                        new Violation("email", "Пользователь с указанным email уже существует!"));
            }
        }
    }

    private void checkUserExists(User user, Long id) {
        if (user == null) {
            throw new NotFoundException(
                    new Violation("user", String.format("Пользователь с id=%d не найден!", id)));
        }
    }

    private void checkNotBlankField(String value, String name) {
        if (value != null && value.isBlank()) {
            throw new ValidationException(
                    new Violation(name, name + " пользователя не может быть пустым!"));
        }
    }

    // Проверка валидности адреса электронной почты по стандарту RFC 5322
    private void checkValidEmail(String email) {
        if (email != null) {
            boolean isValid = Pattern
                    .compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
                    .matcher(email)
                    .matches();
            if (!isValid) {
                throw new ValidationException(
                        new Violation("email", "Некорректный адрес email!"));
            }
        }
    }

}
