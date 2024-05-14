package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;

    @Override
    public User addUser(User user) {
        User savedUser = userRepository.save(user);
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

        User oldUser = getUserById(user.getId());
        User savedUser = userRepository.save(UserMapper.patchUser(oldUser, user));
        log.info("Информация о пользователе изменена: {}", savedUser);
        return savedUser;
    }

    @Override
    public User getUser(Long userId) {
        User user = getUserById(userId);
        log.info("Получена информация о пользователе c id={}: {}", userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id={} удален", userId);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Получен список всех пользователей");
        return users;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        new Violation("user", String.format("Пользователь с id=%d не найден!", userId))));
    }

    private void checkEmailExists(String email) {
        if (userRepository.findByEmailIgnoreCase(email).size() > 0) {
            throw new ConflictException(
                    new Violation("email", "Пользователь с указанным email уже существует!"));
        }
    }

    private void checkEmailExists(String email, Long userId) {
        List<User> users = userRepository.findByEmailIgnoreCase(email);
        for (User user : users) {
            if (!user.getId().equals(userId)) {
                throw new ConflictException(
                        new Violation("email", "Пользователь с указанным email уже существует!"));
            }
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
