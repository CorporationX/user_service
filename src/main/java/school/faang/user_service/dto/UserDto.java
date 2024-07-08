package school.faang.user_service.dto;

import school.faang.user_service.entity.User;

public record UserDto(Long id, String username, String password) {
    public static UserDto usetToUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }
}
