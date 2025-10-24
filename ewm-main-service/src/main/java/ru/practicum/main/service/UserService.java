package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.NewUserRequest;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.mapper.NewUserRequestMapper;
import ru.practicum.main.mapper.UserDtoMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
        return userRepository.findAll().stream()
                .filter(item -> {
                    return ids.isEmpty() ? true : ids.contains(item.getId());
                })
                .skip(from)
                .limit(size)
                .map(item -> UserDtoMapper.userToUserDto(item))
                .collect(Collectors.toList());
    }

    public UserDto registerUser(NewUserRequest newUserRequest) {
        log.info("Registering new user: {}", newUserRequest.getEmail());
        return UserDtoMapper.userToUserDto(userRepository.save(NewUserRequestMapper.toUser(newUserRequest)));
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        findUserById(userId);
        userRepository.deleteById(userId);
    }

    public User findUserById(Long userId) {
        log.info("Finding user by id: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
    }
}
