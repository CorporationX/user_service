package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.message.event.ProfileViewEvent;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.message.producer.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.Integrations.avatar.AvatarService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private List<UserFilter> filters;
    @Mock
    private PremiumRepository premiumRepo;
    @Mock
    private UserMapper userMapper;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageUtils imageUtils;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Mock
    private CountryService countryService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private AvatarService avatarService;

    @Spy
    private UserProfilePicMapper mapper = Mappers.getMapper(UserProfilePicMapper.class);

    private User user;
    private MultipartFile multipartFile;
    private BufferedImage bufferedImage;
    private UserSubResponseDto userDto;
    private UserFilterDto userFilterDto;
    private List<User> userList = new ArrayList<>();
    private UserRegistrationDto registrationDto;
    private Country country;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserSubResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userFilterDto = UserFilterDto.builder()
                .namePattern("testuser")
                .build();

        userList.add(user);
        UserProfilePic profilePic = UserProfilePic.builder()
                .fileId("file1")
                .smallFileId("smallFile1")
                .build();
        user.setUserProfilePic(profilePic);
        multipartFile = new MockMultipartFile("file", "test.jpg",
                "image/jpeg", new byte[5000]);
        bufferedImage = new BufferedImage(5000, 5000, BufferedImage.TYPE_INT_RGB);

        country = new Country(1L, "USA",new ArrayList<>());

        registrationDto = new UserRegistrationDto(
                "JohnDoe",
                "john.doe@example.com",
                "12345678",
                "password123",
                1L
        );
    }

    @Test
    public void testBanUsers() {
        // arrange
        List<Long> userIds = List.of(1L, 2L);
        List<User> users = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()
        );
        when(userRepository.findAllById(userIds)).thenReturn(users);

        // act
        userService.banUsers(userIds);

        // assert
        users.forEach(user -> assertTrue(user.isBanned()));
    }

    @Test
    void testGetUserByIdWithExistingUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetPremiumUsers() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserSubResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userFilterDto = UserFilterDto.builder()
                .namePattern("testuser")
                .build();
        userList.add(user);
        when(premiumRepo.findPremiumUsers()).thenReturn(userList.stream());

        userService.getPremiumUsers(userFilterDto);

        verify(premiumRepo, times(1)).findPremiumUsers();
    }

    @Test
    void testUpdateUserProfilePic() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(s3Service.uploadImage(any(), any(), any(), any())).thenReturn("key");
        when(userRepository.save(user)).thenReturn(user);
        when(imageUtils.resizeImage(any(), anyInt())).thenReturn(bufferedImage);
        when(imageUtils.convertMultiPartFileToBufferedImage(multipartFile)).thenReturn(bufferedImage);

        userService.updateUserProfilePicture(user.getId(), multipartFile);

        verify(imageUtils, times(1)).convertMultiPartFileToBufferedImage(multipartFile);
        verify(imageUtils, times(2)).resizeImage(any(), anyInt());
        verify(userRepository).save(user);
        verify(s3Service, times(2)).uploadImage(any(), any(), any(), any());
        verify(userRepository).findById(any());
    }

    @Test
    void testUpdateUserProfilePicWithFileNotImage() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        MultipartFile file = new MockMultipartFile("file", "test.jpg",
                "text/plain", new byte[10000]);

        assertThrows(DataValidationException.class, () ->
                userService.updateUserProfilePicture(user.getId(), file));

        verify(userRepository).findById(any());
    }

    @Test
    void testGetUserAvatar() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(s3Service.getFile(any())).thenReturn(new InputStreamResource(new ByteArrayInputStream(new byte[0])));

        userService.getUserAvatar(1L);

        verify(userRepository).findById(any());
        verify(s3Service).getFile(any());
    }

    @Test
    void testDeleteUserAvatarPic() {
        String fileId = user.getUserProfilePic().getFileId();
        String smallFileId = user.getUserProfilePic().getSmallFileId();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUserAvatar(user.getId());

        verify(userRepository).findById(any());
        verify(userRepository).save(user);
        verify(s3Service).deleteFiles(fileId, smallFileId);
        assertNull(user.getUserProfilePic());
    }

    @Test
    public void testPublishProfileViewEvent() {
        // arrange
        long actorId = 5L;
        long receiverId = 2L;

        // act
        ArgumentCaptor<ProfileViewEvent> profileViewEventCaptor
                = ArgumentCaptor.forClass(ProfileViewEvent.class);
        userService.publishProfileViewEvent(receiverId, actorId);

        // assert
        verify(profileViewEventPublisher).publish(profileViewEventCaptor.capture());
        ProfileViewEvent profileViewEvent = profileViewEventCaptor.getValue();

        assertEquals(actorId, profileViewEvent.actorId());
        assertEquals(receiverId, profileViewEvent.receiverId());
    }

    @Test
    void registerUser_successfulRegistration() {

        user.setEmail("john.doe@example.com");
        user.setUsername("JohnDoe");

        UserProfilePic profilePic = new UserProfilePic("1","1");

        when(userRepository.existsByEmail(registrationDto.email())).thenReturn(false);
        when(countryService.getCountryById(registrationDto.countryId())).thenReturn(country);
        when(userMapper.toEntity(registrationDto)).thenReturn(user);
        when(passwordService.encodePassword("password123")).thenReturn("encodedPassword");
        when(avatarService.generateAndUploadUserAvatars(anyString())).thenReturn(profilePic);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserSubResponseDto response = userService.registerUser(registrationDto);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.username()).isEqualTo("JohnDoe");

        verify(userRepository, times(2)).save(user);
        verify(passwordService).encodePassword("password123");
        verify(avatarService).generateAndUploadUserAvatars(anyString());
    }

    @Test
    void registerUser_userAlreadyExists_throwsException() {
        when(userRepository.existsByEmail(registrationDto.email())).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> userService.registerUser(registrationDto)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordService, never()).encodePassword(anyString());
    }

    @Test
    void registerUser_avatarGenerationFails_logsWarning() {
        user.setEmail("john.doe@example.com");
        user.setUsername("JohnDoe");
        user.setCountry(country);

        when(userRepository.existsByEmail(registrationDto.email())).thenReturn(false);
        when(countryService.getCountryById(registrationDto.countryId())).thenReturn(country);
        when(userMapper.toEntity(registrationDto)).thenReturn(user);
        when(passwordService.encodePassword("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(avatarService.generateAndUploadUserAvatars(anyString()))
                .thenThrow(new RuntimeException("Avatar generation failed"));

        UserSubResponseDto response = userService.registerUser(registrationDto);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.username()).isEqualTo("JohnDoe");

        verify(userRepository, times(1)).save(user);
        verify(passwordService).encodePassword("password123");
        verify(avatarService).generateAndUploadUserAvatars(anyString());
    }
}