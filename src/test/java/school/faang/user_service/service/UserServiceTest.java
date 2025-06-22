package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void getUserById() {
        long id = 1L;
        User user = User.builder()
                .id(id)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto result = userMapper.toDto(userService.getUserById(id));

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void getUserByIdException() {
        long id = -1L;
        when(userRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The Requester with id =" + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userRepository.findById(id));
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .username("Alice")
                        .email("alice@example.com")
                        .build(),

                User.builder()
                        .id(2L)
                        .username("Bob")
                        .email("bob@example.com")
                        .build()
        );

        List<UserDto> expectedDtos = List.of(
                new UserDto(1L, "Alice", "alice@example.com"),
                new UserDto(2L, "Bob", "bob@example.com")
        );

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(users.get(0))).thenReturn(expectedDtos.get(0));
        when(userMapper.toDto(users.get(1))).thenReturn(expectedDtos.get(1));

        List<UserDto> result = userService.getUsersByIds(ids);
        assertEquals(expectedDtos, result);
    }
}