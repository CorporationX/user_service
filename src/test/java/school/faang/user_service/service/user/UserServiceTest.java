package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazonS3.S3Service;
import school.faang.user_service.service.user.image.AvatarGeneratorService;
import school.faang.user_service.service.user.image.ImageProcessor;
import school.faang.user_service.testData.TestData;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    @Spy
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private S3Service s3Service;
    @Mock
    private AvatarGeneratorService avatarGeneratorService;

    private TestData testData;
    private User user;
    private UserDto userDto;


    @BeforeEach
    void SetUp() {
        testData = new TestData();
        user = testData.getUser();
        userDto = testData.getUserDto();
    }

    @Nested
    class PositiveTests {
        @DisplayName("should create user and upload random avatar when such user doesn't exists already")
        @Test
        void createUserTest() {
            when(userRepository.existsById(anyLong())).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toEntity(userDto)).thenReturn(user);
            when(countryRepository.findById(anyLong())).thenReturn(Optional.of(user.getCountry()));
            when(avatarGeneratorService.getRandomAvatar()).thenReturn(testData.getBufferedImage());
            doReturn(null).when(userService).uploadUserAvatar(anyLong(), any(BufferedImage.class));

            assertDoesNotThrow(() -> userService.createUser(userDto));

            verify(userRepository).save(user);
            verify(userService).uploadUserAvatar(anyLong(), any(BufferedImage.class));
        }

        @DisplayName("should call s3Service.downloadFile and read content when user has userProfilePic")
        @Test
        void downloadUserAvatarTest() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(s3Service.downloadFile(anyString())).thenReturn(InputStream.nullInputStream());

            assertDoesNotThrow(() -> userService.downloadUserAvatar(user.getId()));
        }

        @DisplayName("should delete user's avatars when such exist")
        @Test
        void deleteUserAvatarTest() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> userService.deleteUserAvatar(anyLong()));

            verify(s3Service, times(2)).deleteFile(anyString());
            assertNull(user.getUserProfilePic());
        }

        @Nested
        class UploadUserAvatarsTests {
            @BeforeEach
            void setUp() {
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
                doReturn(testData.getBufferedImagesHolder()).when(imageProcessor).scaleImage(any());
                when(userRepository.save(any(User.class))).thenReturn(user);
                when(userMapper.toDto(any(User.class))).thenReturn(userDto);
            }

            @DisplayName("should upload user avatar")
            @Test
            void uploadUserAvatarTest() {
                user.setUserProfilePic(null);

                assertDoesNotThrow(() -> userService.uploadUserAvatar(anyLong(), any(BufferedImage.class)));

                verify(s3Service, times(2)).uploadFile(any(), anyString());
                verify(s3Service, times(0)).deleteFile(anyString());
                verify(userRepository).save(any(User.class));
            }

            @DisplayName("should upload user avatar and delete previous avatar when such exists")
            @Test
            void uploadUserAvatarAndDeletePreviousTest() {
                doNothing().when(s3Service).deleteFile(anyString());

                assertDoesNotThrow(() -> userService.uploadUserAvatar(anyLong(), any(BufferedImage.class)));

                verify(s3Service, times(2)).uploadFile(any(), anyString());
                verify(s3Service, times(2)).deleteFile(anyString());
                verify(userRepository).save(any(User.class));
            }
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw exception when such user exists already")
        @Test
        void createUserTest() {
            when(userRepository.existsById(anyLong())).thenReturn(true);

            assertThrows(DataValidationException.class, () -> userService.createUser(userDto));

            verifyNoMoreInteractions(userRepository);
            verify(userService, times(0)).uploadUserAvatar(anyLong(), any(BufferedImage.class));
        }

        @DisplayName("should throw DataGettingException when user hasn't userProfilePic")
        @Test
        void downloadUserAvatarTest() {
            user.setUserProfilePic(null);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThrows(DataGettingException.class, () -> userService.downloadUserAvatar(user.getId()));
        }

        @DisplayName("should throw DataGettingException when user hasn't userProfilePic")
        @Test
        void deleteUserAvatarTest() {
            user.setUserProfilePic(null);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThrows(DataGettingException.class, () -> userService.deleteUserAvatar(user.getId()));
        }
    }
}