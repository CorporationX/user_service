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
        var userDtoFirst = createUserDto();
        var userDtoSecond = UserDto.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var userDtoThird = UserDto.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(userDtoFirst, userDtoSecond, userDtoThird);
    }

    public static List<User> createUsersList() {
        var firstUser = createUser();
        var secondUser = User.builder()
                .id(2L)
                .username("Incognito2")
                .email("incognito2@gmail.com")
                .build();
        var thirdUser = User.builder()
                .id(3L)
                .username("Incognito3")
                .email("incognito3@gmail.com")
                .build();

        return of(firstUser, secondUser, thirdUser);
    }

}
