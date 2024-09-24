package school.faang.user_service.service.userClient;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserClientServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserClientServiceImpl userClientService;

    private User user;
    private User user2;

    private UserDto userDto;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList();

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        users.add(user);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("username2");
        users.add(user2);

        userDto = new UserDto(1L, "username", "password");
    }

    @Test
    void testGetNonExistentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userClientService.getUser(1L));
    }

    @Test
    void testGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        userClientService.getUser(1L);

        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(users);

        userClientService.getUsersByIds(ids);

        verify(userMapper, times(2)).toDto(any());
    }

}