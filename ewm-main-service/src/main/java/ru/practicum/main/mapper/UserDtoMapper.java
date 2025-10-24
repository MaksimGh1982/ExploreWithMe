package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.model.User;

@UtilityClass
public class UserDtoMapper {
    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return userDto;
    }
}
