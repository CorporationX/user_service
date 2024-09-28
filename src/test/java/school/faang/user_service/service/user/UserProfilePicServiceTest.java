package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.picture.PictureValidator;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
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
    private S3Service s3Service;
    @Mock
    private MultipartFile multipartFile;

    private static final long ID = 1L;
    private static final String KEY = "key";

    private User user;
    private UserProfilePic userProfilePic = new UserProfilePic();
    private User updatedUser;
    private InputStream inputStream;
    private byte[] image;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(ID)
                .build();

        userProfilePic.setFileId(KEY);
        userProfilePic.setSmallFileId(KEY);

        updatedUser = User.builder()
                .id(ID)
                .userProfilePic(userProfilePic)
                .build();

        inputStream = InputStream.nullInputStream();
        image = new byte[0];
    }

    @Test
    @DisplayName("Успешное сохранение аватара")
    public void whenUploadUserAvatarThenSuccess() {
        when(userService.findById(ID)).thenReturn(user);
        when(pictureValidator.changeFileScale(multipartFile)).thenReturn(List.of(image, image));
        when(s3Service.uploadAvatar(any(), anyString(), isNull())).thenReturn(KEY);
        when(userService.saveUser(user)).thenReturn(updatedUser);

        userProfilePicService.uploadUserAvatar(ID, multipartFile);

        verify(userService).findById(ID);
        verify(pictureValidator).checkPictureSizeExceeded(multipartFile);
        verify(pictureValidator).changeFileScale(multipartFile);
        verify(s3Service, times(2)).uploadAvatar(any(), anyString(), isNull());
        verify(userService).saveUser(user);
    }

    @Test
    @DisplayName("Успешное удаление аватара")
    public void whenDeleteUserAvatarThenSuccess() {
        when(userService.findById(ID)).thenReturn(updatedUser);

        userProfilePicService.deleteUserAvatar(ID);

        verify(userService).findById(ID);
        verify(s3Service, times(2)).deleteAvatar(KEY);
    }

    @Test
    @DisplayName("Успешное получение аватара")
    public void whenDownloadUserAvatarThenSuccess() {
        when(userService.findById(ID)).thenReturn(updatedUser);
        when(s3Service.downloadAvatar(updatedUser.getUserProfilePic().getFileId()))
                .thenReturn(inputStream);

        InputStream resultInputStream = userProfilePicService.downloadUserAvatar(ID);

        assertEquals(inputStream, resultInputStream);
        verify(userService).findById(ID);
        verify(s3Service).downloadAvatar(updatedUser.getUserProfilePic().getFileId());
    }
}