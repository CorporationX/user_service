package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserMapper userMapper;

    private long id;
    private List<Long> ids;
    private User user;
    private UserDto userDto;
    private List<UserDto> userDtoList;

    @BeforeEach
    public void setUp() {
        id = 1L;
        ids = List.of(id);

        user = new User();
        userDto = new UserDto();

        userDtoList = List.of(userDto);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test that getUser calls all methods correctly + return test")
    public void testGetUser() {
        doNothing().when(userValidator).validateUserId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(id);

        verify(userValidator).validateUserId(id);
        verify(userRepository).findById(id);
        verify(userMapper).toDto(user);

        assertEquals(result, userDto);
    }

    @Test
    @DisplayName("test that getUsersByIds calls all methods correctly + return test")
    public void testGetUsersByIds() {
        doNothing().when(userValidator).validateUserId(id);
        when(userRepository.findAllById(ids)).thenReturn(Collections.singleton(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getUsersByIds(ids);

        verify(userValidator).validateUserId(id);
        verify(userRepository).findAllById(ids);
        verify(userMapper).toDto(user);

        assertEquals(result, userDtoList);
    }
}