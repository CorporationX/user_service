package school.faang.user_service.util;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static java.util.List.*;

@UtilityClass
public final class TestDataFactory {

    public static UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("Incognito1")
                .email("incognito1@gmail.com")
                .build();
    }

    public static User createUser() {
        return User.builder()
                .id(1L)
                .username("Incognito")
                .email("Incognito@gmail.com")
                .build();
    }

    public static List<UserDto> createUserDtosList() {
        var userDto1 = createUserDto();
        var userDto2 = UserDto.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var userDto3 = UserDto.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(userDto1, userDto2, userDto3);
    }

    public static List<User> createUsersList() {
        var user1 = createUser();
        var user2 = User.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var user3 = User.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(user1, user2, user3);
    }

}
