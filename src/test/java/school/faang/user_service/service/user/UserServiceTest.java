package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.dto.register.UserProfileDto;
import school.faang.user_service.dto.register.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.AvatarGenerationException;
import school.faang.user_service.exception.S3UploadException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private DiceBearClient diceBearClient;
    @Mock private S3Service s3Service;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private CountryRepository countryRepository;

    @InjectMocks private UserService userService;

    private UserRegistrationDto registrationDto;
    private Country country;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setCountryId(1L);

        country = Country.builder()
                .id(1L)
                .title("TestCountry")
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.of(country));

        byte[] avatarData = "avatar".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(avatarData);
        when(diceBearClient.generateAvatar(anyString())).thenReturn(responseEntity);

        when(s3Service.uploadFile(any(InputStream.class), eq(Long.valueOf(avatarData.length)), eq("image/svg+xml"))).thenReturn("avatar-file-id");

        User user = new User();
        when(userMapper.userRegistrationDtoToUser(registrationDto)).thenReturn(user);
        when(userMapper.userToUserProfileDto(user)).thenReturn(new UserProfileDto());

        UserProfileDto result = userService.registerUser(registrationDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
        verify(s3Service, times(1)).uploadFile(any(InputStream.class), eq(Long.valueOf(avatarData.length)), eq("image/svg+xml"));

    }

    @Test
    void shouldThrowExceptionWhenCountryNotFound() {
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registrationDto));
    }

    @Test
    void shouldThrowAvatarGenerationExceptionWhenAvatarGenerationFails() {
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.of(country));

        when(diceBearClient.generateAvatar(anyString())).thenThrow(new RuntimeException("Avatar generation failed"));

        User user = new User();
        when(userMapper.userRegistrationDtoToUser(registrationDto)).thenReturn(user);
        when(userMapper.userToUserProfileDto(user)).thenReturn(new UserProfileDto());

        AvatarGenerationException exception = assertThrows(AvatarGenerationException.class, () -> userService.registerUser(registrationDto));
        assertEquals("Error generating avatar.", exception.getMessage());
    }

    @Test
    void shouldThrowS3UploadExceptionWhenS3UploadFails() {
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.of(country));

        byte[] avatarData = "avatar".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok(avatarData);
        when(diceBearClient.generateAvatar(anyString())).thenReturn(responseEntity);

        doThrow(new S3UploadException("Upload failed")).when(s3Service).uploadFile(any(InputStream.class), anyLong(), anyString());

        User user = new User();
        when(userMapper.userRegistrationDtoToUser(registrationDto)).thenReturn(user);
        when(userMapper.userToUserProfileDto(user)).thenReturn(new UserProfileDto());

        assertThrows(S3UploadException.class, () -> userService.registerUser(registrationDto));
    }
}
