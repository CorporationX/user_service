package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.DeactivateUserService;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DeactivateUserService deactivateUserService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);

        userDto = new UserDto();
        userDto.setId(user.getId());
    }

    @Test
    void deactivateUserTest_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.deactivateUser(userDto);
        assertFalse(user.isActive());
        verify(deactivateUserService).stopUserActivities(user);
        verify(mentorshipService).stopMentorship(user);
        verify(userRepository).save(user);
        assertEquals(userDto, result);
    }

    @Test
    void deactivateUserTest_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            userService.deactivateUser(userDto);
        });
        assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
    }
}
