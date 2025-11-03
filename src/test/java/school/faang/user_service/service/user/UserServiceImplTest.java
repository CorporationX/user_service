package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.amazon_s3.S3Service;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.UserRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private S3Service s3Service;

    @Mock
    private MockMultipartFile anyMultipartFile;

    private Long anyLong;
    private Long anyUserId;
    private Long normalFileSize;
    private User anyUser;
    private String anyString;
    private int firstRequiredMaxImageWidthAndLength;
    private int secondRequiredMaxImageWidthAndLength;
    private UserProfilePic defaultUserProfilePic;
    private String anyFolderName;

    @BeforeEach
    public void setUp() {
        anyLong = 1L;
        anyUserId = anyLong;
        normalFileSize = 3_000L;
        anyUser = new User();
        anyString = "anyString";
        anyFolderName = "1anyString";
        anyUser.setUsername(anyString);
        firstRequiredMaxImageWidthAndLength = 1080;
        secondRequiredMaxImageWidthAndLength = 170;
        defaultUserProfilePic = new UserProfilePic(anyString, anyString);
        anyUser.setUserProfilePic(defaultUserProfilePic);
    }

    @Test
    public void setUserAvatarSetTooBigFile() {
        long tooBigFileSize = 6_000_000L;

        when(anyMultipartFile.getSize()).thenReturn(tooBigFileSize);

        assertThrows(DataValidationException.class, () -> userServiceImpl.setUserAvatar(anyUserId, anyMultipartFile));
    }

    @Test
    public void setUserAvatarSetSuccessfully() throws IOException {
        when(anyMultipartFile.getSize()).thenReturn(normalFileSize);
        when(userRepository.getByIdOrThrow(anyUserId)).thenReturn(anyUser);

        userServiceImpl.setUserAvatar(anyUserId, anyMultipartFile);

        verify(userRepository, times(1)).getByIdOrThrow(anyUserId);
        verify(s3Service, times(1))
                .uploadFile(anyUserId, anyMultipartFile, anyFolderName, firstRequiredMaxImageWidthAndLength);
        verify(s3Service, times(1))
                .uploadFile(anyUserId, anyMultipartFile, anyFolderName, secondRequiredMaxImageWidthAndLength);
        verify(userMapper, times(1)).toUserDto(anyUser);
    }

    @Test
    public void getUserAvatarSuccessfullyReturns() {
        when(userRepository.getByIdOrThrow(anyUserId)).thenReturn(anyUser);

        userServiceImpl.getUserAvatar(anyUserId);

        verify(userRepository, times(1)).getByIdOrThrow(anyUserId);
        verify(s3Service, times(1)).downloadFile(anyUser.getUserProfilePic().getFileId());
    }

    @Test
    public void deleteUserAvatarSuccessfullyDeletes() {
        when(userRepository.getByIdOrThrow(anyUserId)).thenReturn(anyUser);

        userServiceImpl.deleteUserAvatar(anyUserId);

        verify(userRepository, times(1)).getByIdOrThrow(anyUserId);
        verify(s3Service, times(2)).deleteFile(anyString);

        assertNull(anyUser.getUserProfilePic().getFileId());
        assertNull(anyUser.getUserProfilePic().getSmallFileId());
    }
}
