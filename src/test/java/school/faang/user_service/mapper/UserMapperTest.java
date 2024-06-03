package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.testData.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        TestData testData = new TestData();

        user = testData.getUser();

        userDto = testData.getUserDto();
    }

    @DisplayName("should map user to userDto")
    @Test
    void shouldMapUserEntityToUserDto() {
        UserDto actualDto = userMapper.toDto(user);

        assertEquals(userDto, actualDto);
    }

    @DisplayName("should map list of users to list of userDtos")
    @Test
    void shouldMapUserEntityListToUserDtoList() {
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        List<UserDto> actualDtos = userMapper.toDto(users);

        assertEquals(userDtos, actualDtos);
    }
}