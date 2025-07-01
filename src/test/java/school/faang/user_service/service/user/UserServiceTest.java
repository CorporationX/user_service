package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.authorization.UserUnauthorizedException;
import school.faang.user_service.exception.country.CountryNotFoundException;
import school.faang.user_service.exception.user.UserAlreadyExistsException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.resource.image.ImageService;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserValidator userValidator;
    @Mock
    private CountryService countryService;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageService imageService;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @InjectMocks
    private UserService userService;
    private User user;
    private Country country;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(5L);
        country = new Country();
        country.setId(7L);
    }

    @Test
    public void testgetUserByIdOrThrow_successfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User returnUser = userService.getUserByIdOrThrow(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        assertEquals(user.getId(), returnUser.getId());
    }

    @Test
    public void testgetUserByIdOrThrow_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByIdOrThrow(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetCurrentUser_successfully() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User returnUser = userService.getCurrentUser();

        verify(userRepository, times(1)).findById(eq(user.getId()));
        assertEquals(user.getId(), returnUser.getId());
    }

    @Test
    public void testGetCurrentUser_userInContextNotFound() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> userService.getCurrentUser());
        verify(userRepository, never()).findById(eq(user.getId()));
    }

    @Test
    public void testGetCurrentUser_userNotFound() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
        verify(userRepository, times(1)).findById(eq(user.getId()));
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(user.getId());
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user));

        List<User> returnUsers = userService.getUsersByIds(userIds);

        verify(userRepository, times(1)).findAllById(eq(userIds));
        assertEquals(userIds, returnUsers.stream().map(User::getId).toList());
        assertEquals(userIds.size(), returnUsers.size());
    }

    @Test
    public void testRegistrationUser_successfully() {
        String password = "plainPassword";
        user.setPassword(password);

        when(passwordEncoder.encode(password)).thenReturn(password);
        doNothing().when(userValidator).validateUser(user);
        when(countryService.getCountryById(country.getId())).thenReturn(country);
        when(userRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registrationUser(user, country.getId());

        verify(userRepository).save(userCaptor.capture());
        User captureUser = userCaptor.getValue();

        assertNotNull(result);
        assertEquals(result, captureUser);
        assertNotNull(result.getPassword());
        assertEquals(country, result.getCountry());
        assertTrue(result.isActive());

        verify(userValidator).validateUser(user);
        verify(passwordEncoder).encode(password);
        verify(countryService).getCountryById(country.getId());
        verify(userRepository).save(captureUser);
    }

    @Test
    public void testRegistrationUser_validationException() {
        doThrow(UserAlreadyExistsException.class).when(userValidator).validateUser(user);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registrationUser(user, country.getId()));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegistrationUser_countryNotFound() {
        when(countryService.getCountryById(country.getId())).thenThrow(CountryNotFoundException.class);

        assertThrows(CountryNotFoundException.class, () -> userService.registrationUser(user, country.getId()));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateAvatarUser_successfully() {
        Resource mockResource = mock(Resource.class);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(imageService.generateRandomUserAvatar(eq(user.getId()))).thenReturn(mockResource);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createAvatarUser(user.getId());

        User capturedUser = userCaptor.getValue();
        verify(userRepository).findById(user.getId());
        verify(imageService).generateRandomUserAvatar(user.getId());
        verify(userRepository).save(capturedUser);
        assertNotNull(capturedUser.getUserProfilePic());
        assertEquals(mockResource, capturedUser.getUserProfilePic().getSmallFile());
    }

    @Test
    public void testCreateAvatarUser_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.createAvatarUser(user.getId()));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateAvatarUser_generateAvatarException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(imageService.generateRandomUserAvatar(user.getId())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> userService.createAvatarUser(user.getId()));
        verify(userRepository, never()).save(any(User.class));
    }
}
