package school.faang.user_service.service.user;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AvatarService avatarService;

    @Mock
    private CountryService countryService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private S3Service s3Service;
    @InjectMocks
    UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPhone("1234567890");

        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPhone("1234567890");
        Country country = new Country();
        country.setTitle("Belarus");
        user.setCountry(country);
    }

    @Test
    public void testFindNotExistingUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> userService.findUserById(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testFindExistingUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User expected = userService.findUserById(1L);
        assertEquals(user, expected);
    }

    @Test
    void createUserTest() throws IOException, TranscoderException {
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(countryService.getCountryOrCreate(anyString())).thenReturn(user.getCountry());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(avatarService.getRandomAvatarUrl()).thenReturn("https://example.com/avatar.svg");
        when(avatarService.downloadSvgAsMultipartFile(anyString())).thenReturn(null);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto createdUser = userService.createUser(userDto);

        verify(userValidator).uniqueUsername(userDto.getUsername());
        verify(userValidator).uniqueEmail(userDto.getEmail());
        verify(userValidator).uniquePhone(userDto.getPhone());
        verify(userMapper).toEntity(userDto);
        verify(countryService).getCountryOrCreate(user.getCountry().getTitle());
        verify(userRepository).save(user);
        verify(s3Service).uploadAvatar(eq(user.getId()), any());
        verify(userMapper).toDto(user);

        assertEquals(userDto, createdUser);
    }

    @Test
    void createUserValidationExceptionTest() throws IOException {
        doThrow(new DataValidationException("Username already exists")).when(userValidator).uniqueUsername(anyString());

        assertThrows(DataValidationException.class, () -> {
            userService.createUser(userDto);
        });

        verify(userValidator).uniqueUsername(userDto.getUsername());
        verify(userValidator, never()).uniqueEmail(anyString());
        verify(userValidator, never()).uniquePhone(anyString());
        verify(userMapper, never()).toEntity(any(UserDto.class));
        verify(countryService, never()).getCountryOrCreate(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(s3Service, never()).uploadAvatar(anyLong(), any());
        verify(userMapper, never()).toDto(any(User.class));
    }
}
