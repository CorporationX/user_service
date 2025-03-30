package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceRegistrationTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserAvatarService userAvatarService;

    @Test
    void testGetCurrentUserId() {
        long expectedUserId = 123L;
        when(userContext.getUserId()).thenReturn(expectedUserId);

        long actualUserId = userService.getCurrentUserId();

        assertEquals(expectedUserId, actualUserId);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    void testUpdateUser() {
        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john.doe@example.com")
                .build();

        userService.updateUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUserSuccess() {
        String username = "jane_doe";
        String email = "jane.doe@example.com";
        String password = "password123";
        Long countryId = 1L;
        String telegramUserName = "joe32";

        Country country = new Country();
        country.setId(countryId);

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));

        User userToSave = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .country(country)
                .active(true)
                .telegramUsername(telegramUserName)
                .experience(0)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(username, email, password, countryId, telegramUserName);

        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        assertEquals(email, registeredUser.getEmail());
        assertEquals(country, registeredUser.getCountry());
        assertTrue(registeredUser.isActive());
        assertEquals(0, registeredUser.getExperience());

        verify(countryRepository, times(1)).findById(countryId);
        verify(userAvatarService, times(1)).generateAvatarForNewUser(eq(userToSave), eq(AvatarType.JPEG));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserCountryNotFound() {
        String username = "jane_doe";
        String email = "jane.doe@example.com";
        String password = "password123";
        String telegramUserName = "joe32";
        Long countryId = 1L;

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(username, email, password, countryId, telegramUserName)
        );

        assertEquals("Country not found with id: " + countryId, exception.getMessage());
        verify(countryRepository, times(1)).findById(countryId);
        verifyNoInteractions(userAvatarService, userRepository);
    }

    @Test
    void testRegisterUserSaveFails() {
        String username = "jane_doe";
        String email = "jane.doe@example.com";
        String password = "password123";
        String telegramUserName = "joe32";
        Long countryId = 1L;

        Country country = new Country();
        country.setId(countryId);

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(userRepository.save(any(User.class))).thenThrow(
                new DataIntegrityViolationException("Unique constraint violated"));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () ->
                userService.registerUser(username, email, password, countryId, telegramUserName)
        );

        assertEquals("Unique constraint violated", exception.getMessage());
        verify(countryRepository, times(1)).findById(countryId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userAvatarService, times(1)).generateAvatarForNewUser(any(User.class), eq(AvatarType.JPEG));
    }
}