package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserMapperTests {
    @Spy
    private UserMapperImpl userMapper;
    private User user;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        userDto = userMapper.toDto(user);
        users = List.of(user);
        userDtos = userMapper.toDto(users);
    }

    @Test
    void testToDto() {
        Assertions.assertEquals(userDto, userMapper.toDto(user));
    }

    @Test
    void testToEntity() {
        Assertions.assertEquals(user, userMapper.toEntity(userDto));
    }

    @Test
    void testToDtoList() {
        Assertions.assertEquals(userDtos, userMapper.toDto(users));
    }
}
