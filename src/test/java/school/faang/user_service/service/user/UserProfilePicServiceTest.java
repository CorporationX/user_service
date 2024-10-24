package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.event.profile.ProfilePicEvent;
import school.faang.user_service.publisher.profile.ProfilePicEventPublisher;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.picture.PictureValidator;
import school.faang.user_service.validator.picture.ScaleChanger;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfilePicServiceTest {

    @InjectMocks
    private UserProfilePicService userProfilePicService;
    @Mock
    private UserService userService;
    @Mock
    private PictureValidator pictureValidator;
    @Mock
    private ScaleChanger scaleChanger;
    @Mock
    private S3Service s3Service;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private ProfilePicEventPublisher profilePicEventPublisher;

    private static final long ID = 1L;
    private static final String KEY = "key";

    private User user;
    private UserProfilePic userProfilePic = new UserProfilePic();
    private User updatedUser;
    private InputStream inputStream;
    private List<ResponseEntity<byte[]>> images;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(ID)
                .userProfilePic(UserProfilePic.builder()
                        .fileId(KEY)
                        .build())
                .build();

        userProfilePic.setFileId(KEY);
        userProfilePic.setSmallFileId(KEY);

        updatedUser = User.builder()
                .id(ID)
                .userProfilePic(userProfilePic)
                .build();

        inputStream = InputStream.nullInputStream();

        images = Arrays.asList(
                ResponseEntity.ok(new byte[]{1}),
                ResponseEntity.ok(new byte[]{2}));
    }

    @Test
    @DisplayName("Успешное сохранение аватара")
    public void whenUploadUserAvatarThenSuccess() {
        when(userService.getUserById(ID)).thenReturn(user);
        when(scaleChanger.changeFileScale(multipartFile)).thenReturn(images);
        when(s3Service.uploadHttpData(any(), anyString())).thenReturn(KEY);
        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);
        when(s3Service.getFullAvatarLinkByFileName(anyString())).thenReturn(anyString());

        userProfilePicService.uploadUserAvatar(ID, multipartFile);

        verify(userService, times(2)).getUserById(ID);
        verify(pictureValidator).checkPictureSizeExceeded(multipartFile);
        verify(scaleChanger).changeFileScale(multipartFile);
        verify(s3Service, times(2)).uploadHttpData(any(), anyString());
        verify(userService).saveUser(any(User.class));
        verify(s3Service).getFullAvatarLinkByFileName(anyString());
        verify(profilePicEventPublisher).publish(any(ProfilePicEvent.class));
    }

    @Test
    @DisplayName("Успешное удаление аватара")
    public void whenDeleteUserAvatarThenSuccess() {
        when(userService.getUserById(ID)).thenReturn(updatedUser);

        userProfilePicService.deleteUserAvatar(ID);

        verify(userService).getUserById(ID);
        verify(s3Service, times(2)).deleteFile(KEY);
    }

    @Test
    @DisplayName("Успешное получение аватара")
    public void whenDownloadUserAvatarThenSuccess() {
        when(userService.getUserById(ID)).thenReturn(updatedUser);
        when(s3Service.downloadFile(updatedUser.getUserProfilePic().getFileId()))
                .thenReturn(inputStream);

        InputStream resultInputStream = userProfilePicService.downloadUserAvatar(ID);

        assertEquals(inputStream, resultInputStream);
        verify(userService).getUserById(ID);
        verify(s3Service).downloadFile(updatedUser.getUserProfilePic().getFileId());
    }
}