package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserCreatedAfterFilter;
import school.faang.user_service.filter.user.UserCreatedBeforeFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.DeactivateUserService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private DeactivateUserService deactivateUserService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private List<UserFilter> userFilters;

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

    @Test
    void testGetPremiumUsers_positiveWithoutFilters() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2, user3));

        UserFilterDto filterDto = new UserFilterDto();
        List<UserDto> users = userService.getPremiumUsers(filterDto);

        assertEquals(3, users.size());
    }

    @Test
    void testGetPremiumUsers_positiveWithFilters() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPhone("123123");
        user1.setCreatedAt(LocalDateTime.now());
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPhone("111111");
        user2.setCreatedAt(LocalDateTime.now());
        User user3 = new User();
        user3.setUsername("user3");
        user3.setPhone("222222");
        user3.setCreatedAt(LocalDateTime.now().minusMonths(2));
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2, user3));

        List<UserFilter> filters = new ArrayList<>();
        filters.add(new UserNameFilter());
        filters.add(new UserPhoneFilter());
        filters.add(new UserCreatedAfterFilter());
        filters.add(new UserCreatedBeforeFilter());
        Mockito.when(userFilters.stream()).thenReturn(filters.stream());

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("user");
        filterDto.setPhone("123123");
        filterDto.setCreatedAfter(LocalDateTime.now().minusMonths(1));
        filterDto.setCreatedBefore(LocalDateTime.now().plusMonths(1));
        List<UserDto> users = userService.getPremiumUsers(filterDto);

        assertEquals(1, users.size());
    }
}