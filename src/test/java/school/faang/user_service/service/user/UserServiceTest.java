package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCityFilter;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final long EXISTENT_USER_ID = 1L;

    private static final long NON_EXISTENT_USER_ID = 100_000L;

    private static final UserEmailFilter userEmailFilter = new UserEmailFilter();

    private static final UserCityFilter userCityFilter = new UserCityFilter();

    private static UserFilterDto dtoFilter = new UserFilterDto();

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private List<User> users;

    private List<UserFilter> filters = new ArrayList<>();

    @BeforeEach
    public void init() {
        userService = new UserService(userRepository, filters);
    }

    @Test
    public void testGetExistingUserById_UserFound_ReturnsUser() {
        User user = new User();
        Mockito.when(userRepository.findById(EXISTENT_USER_ID)).thenReturn(Optional.of(user));
        User existingUserById = userService.getUserById(EXISTENT_USER_ID);
        assertEquals(existingUserById, user);
    }

    @Test
    public void testGetExistingUserById_UserNotFound_ThrowsEntityNotFoundException() {
        Mockito.when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(NON_EXISTENT_USER_ID)
        );
    }

    @Test
    public void testGetPremiumUsersEmailFilter_Success() {
        users = List.of(
                User.builder().email("r123467@gmail.com").build(),
                User.builder().email("k2jsd@mail.ru").build(),
                User.builder().email("dsjfzn22222@yandex.ru").build()
        );
        dtoFilter.setEmailPattern("mail");
        filters.add(userEmailFilter);
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(users.stream());
        List<User> premiumUsers = userService.getPremiumUsers(dtoFilter);
        Assertions.assertEquals(2, premiumUsers.size());
        Assertions.assertEquals(premiumUsers.get(0).getEmail(), "r123467@gmail.com");
        Assertions.assertEquals(premiumUsers.get(1).getEmail(), "k2jsd@mail.ru");

    }

    @Test
    public void testGetPremiumUsersNameFilter_Success() {
        users = List.of(
                User.builder().city("Moscow").build(),
                User.builder().city("Montreal").build(),
                User.builder().city("Florida").build()
        );
        dtoFilter.setCityPattern("Moscow");
        filters.add(userCityFilter);
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(users.stream());
        List<User> premiumUsers = userService.getPremiumUsers(dtoFilter);
        Assertions.assertEquals(1, premiumUsers.size());
        Assertions.assertEquals(premiumUsers.get(0).getCity(), "Moscow");
    }

    @Test
    public void testGetUserByIdFailed() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, userService.getUserById(1L));
    }
}
