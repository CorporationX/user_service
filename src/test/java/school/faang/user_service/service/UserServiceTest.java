package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl();

    @Mock
    private PremiumRepository premiumRepository;

    private List<UserFilter> userFilters = List.of(new UserNameFilter());

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, premiumRepository, userFilters);
    }

    @Test
    void getUser_whenUserExists_thenReturnUserDto() {
        long userId = 1L;
        User user = new User();
        user.setUsername("Bob");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals("Bob", result.getUsername());
    }

    @Test
    void getUser_whenUserNotExists_getUserThrowsException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> userService.getUser(userId));

        assertEquals("Пользователя не существует", dataValidationException.getMessage());
    }

    void getUserById_whenUserIdExist_thenReturnUser() {
        // Arrange
        long userId = 1;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.getUserById(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        assertEquals(user.getId(), userId);
    }

    @Test
    void getPremiumUsers_WhenCalled_ShouldApplyFilters() {
        UserFilterDto filters = new UserFilterDto();
        filters.setNamePattern("Ivan");
        filters.setEmailPattern("ivan@example.com");

        User user1 = new User();
        user1.setUsername("Ivan");
        user1.setEmail("ivan@example.com");
        User user2 = new User();
        user2.setUsername("Ivan");
        user2.setEmail("ivan@example.com");
        User user3 = new User();
        user3.setUsername("Anna");
        user3.setEmail("ivan@example.com");
        List<User> premiumUsers = List.of(user1, user2, user3);

        when(userRepository.findPremiumUsers()).thenReturn(premiumUsers.stream());

        List<UserDto> actualDtos = userService.getPremiumUsers(filters);

        assertEquals(2, actualDtos.size());
        assertEquals("Ivan", actualDtos.get(0).getUsername());
        assertEquals("Ivan", actualDtos.get(1).getUsername());
        assertEquals("ivan@example.com", actualDtos.get(0).getEmail());
        assertEquals("ivan@example.com", actualDtos.get(1).getEmail());
    }


    @Test
    void testGetUserById_whenUserIdNotExist_thenThrowEntityNotFoundException() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }
}
