package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private UserFilterDto dto;

    @Mock
    private List<UserFilter> userFilters = new ArrayList<>();

    @InjectMocks
    private UserService userService;
    private static final long EXISTENT_USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = 100_000L;

    @BeforeEach
    public void init() {
        dto = new UserFilterDto();
    }

    @Test
    public void testGetExistingUserById_UserFound_ReturnsUser() {
        User user = new User();
        Mockito.when(userRepository.findById(EXISTENT_USER_ID)).thenReturn(Optional.of(user));
        User existingUserById = userService.getExistingUserById(EXISTENT_USER_ID);
        assertEquals(existingUserById, user);
    }

    @Test
    public void testGetExistingUserById_UserNotFound_ThrowsEntityNotFoundException() {
        Mockito.when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getExistingUserById(NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetPremiumUsers_Success() {
        List<User> createdUsers = List.of(
                User.builder().email("r123467@gmail.com").build(),
                User.builder().email("k2jsd@mail.ru").build(),
                User.builder().email("dsjfzn22222@yandex.ru").build()
        );

        Stream<User> usersS = createdUsers.stream();

        dto.setEmailPattern("@gmail.com");

        Mockito.when(userRepository.findPremiumUsers()).thenReturn(usersS);
        userService.getPremiumUsers(dto);

    }
}
