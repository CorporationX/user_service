package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private UserMapper userMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        user = User.builder().id(1L).username("user").email("email@email").build();
        userDto = UserDto.builder().id(1L).username("user").email("email@email").build();
    }

    @Test
    void test_user_mapper_user_to_user_dto() {
        UserDto result = userMapper.toDto(user);
        Assertions.assertAll(
                () -> assertEquals(result.getId(), userDto.getId()),
                () -> assertEquals(result.getUsername(), userDto.getUsername()),
                () -> assertEquals(result.getEmail(), userDto.getEmail())
        );
    }

    @Test
    void test_user_mapper_user_dto_to_user() {
        User result = userMapper.toEntity(userDto);
        Assertions.assertAll(
                () -> assertEquals(result.getId(), user.getId()),
                () -> assertEquals(result.getUsername(), user.getUsername()),
                () -> assertEquals(result.getEmail(), user.getEmail())
        );
    }
}