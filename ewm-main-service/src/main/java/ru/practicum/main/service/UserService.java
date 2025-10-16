package ru.practicum.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.NewUserRequest;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.storage.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Getting users with ids: {}, from: {}, size: {}", ids, from, size);
        // TODO: реализовать логику пагинации и фильтрации
        return List.of();
    }

    public UserDto registerUser(NewUserRequest newUserRequest) {
        log.info("Registering new user: {}", newUserRequest.getEmail());
        // TODO: проверить уникальность email, сохранить пользователя
        return null;
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        // TODO: проверить существование пользователя, удалить
    }

    public UserDto findUserById(Long userId) {
        log.info("Finding user by id: {}", userId);
        // TODO: найти пользователя или выбросить исключение
        return null;
    }
}
