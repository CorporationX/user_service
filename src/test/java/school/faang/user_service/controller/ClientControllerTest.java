package school.faang.user_service.controller;

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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ClientControllerTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private ClientController clientController;

    private User user;
    private User user2;

    private List<User> users;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");

        user2 = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");

        users = new ArrayList<>();
        users.add(user);
        users.add(user2);
    }

    @Test
    void testGetNonExistUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clientController.getUser(1L));
    }

    @Test
    void testGetUser() {

        UserDto userDto = new UserDto(1L, "username", "email@");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        clientController.getUser(1L);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUsersById() {
        List<Long> ids = new ArrayList<>();

        when(userRepository.findAllById(ids)).thenReturn(users);

        clientController.getUsersByIds(ids);

        verify(userMapper, times(2)).toDto(any());
    }
}