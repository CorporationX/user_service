package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private UserService userService;


    private final String username = "alice.example";
    private final String countryTitle = "Japan";
    private Country country;
    private final String email = "user@example.com";
    private final String password = "secret123";
    private final String passwordHash = "hashedSecret";
    private User userEntity;
    private User savedEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(1L);
        country.setTitle(countryTitle);

        userEntity = new User(username, country, email, passwordHash);
        savedEntity = new User(username, country, email, passwordHash);
        savedEntity.setId(2L);

        userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUsername(username);
        userDto.setEmail(email);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(savedEntity));

        User result = userService.getUserById(2L);

        assertThat(result).isSameAs(savedEntity);
        verify(userRepository).findById(2L);
    }

    @Test
    void testGetUserByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void testCreateUser() {
        when(countryRepository.findByTitle(countryTitle)).thenReturn(Optional.of(country));
        when(passwordEncoder.encode(password)).thenReturn(passwordHash);
        when(userMapper.toEntity(eq(username), eq(country), eq(email), eq(passwordHash)))
                .thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(userDto);

        UserDto result = userService.create(username, countryTitle, email, password);

        assertThat(result).isEqualTo(userDto);
        verify(countryRepository).findByTitle(countryTitle);
        verify(passwordEncoder).encode(password);
        verify(userMapper).toEntity(username, country, email, passwordHash);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(savedEntity);
        verify(avatarService).generateRandomAvatar(savedEntity.getId());
    }

    @Test
    void testCreateUserWhenCountryNotFound() {
        when(countryRepository.findByTitle(countryTitle)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(
                DataValidationException.class,
                () -> userService.create(username, countryTitle, email, password)
        );

        assertThat(ex.getMessage()).contains("Unknown country");
        verify(countryRepository).findByTitle(countryTitle);
    }

    @Test
    void testCreateUserWhenEmailIsInvalid() {
        String badEmail = "invalid-email";
        assertThrows(DataValidationException.class,
                () -> userService.create(username, countryTitle, badEmail, password));
    }

    @Test
    void testCreateWhenPasswordIsInvalid() {
        String shortPass = "123";
        assertThrows(DataValidationException.class,
                () -> userService.create(username, countryTitle, email, shortPass));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(savedEntity));

        userService.delete(2L);

        verify(userRepository).delete(savedEntity);
    }

    @Test
    void testDeleteUserWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.delete(99L));
    }
}
