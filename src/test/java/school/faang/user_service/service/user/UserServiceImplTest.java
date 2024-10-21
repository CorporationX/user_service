package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.ProfileViewEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.MessagePublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.AvatarSize;
import school.faang.user_service.service.image.BufferedImagesHolder;
import school.faang.user_service.service.image.ImageProcessor;
import school.faang.user_service.service.s3.S3Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private BufferedImagesHolder bufferedImagesHolder;
    private BufferedImage bufferedImage;

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageProcessor imageProcessor;


    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private MessagePublisher<ProfileViewEvent> profileViewEventPublisher;

    @Mock
    private UserContext userContext;

    @Test
    public void getUser_Success() {
        User user = createUser();
        UserDto userDto = new UserDto(1L, "user", "email", PreferredContact.EMAIL);
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = service.getUser(user.getId());

        assertEquals(result, userDto);
        verify(repository, times(1)).findById(user.getId());
    }

    @Test
    public void getUser_UserNotFound() {
        long id = 1L;

        doThrow(new EntityNotFoundException("User not found")).when(repository).findById(id);

        assertThrows(EntityNotFoundException.class, () -> service.getUser(id));
    }

    @Test
    public void getUsersByIds_Success() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L);
        List<User> users = createUsers();
        UserDto userDto1 = new UserDto(1L, "user1", "email1", PreferredContact.EMAIL);
        UserDto userDto2 = new UserDto(2L, "user2", "email2", PreferredContact.EMAIL);
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        when(repository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = service.getUsersByIds(ids);

        assertEquals(result, userDtos);
        verify(repository, times(1)).findAllById(ids);
    }

    @Test
    public void getUsersByIds_SuccessButUsersNotFound() {
        List<Long> ids = new ArrayList<>();
        List<UserDto> userDtos = new ArrayList<>();

        when(repository.findAllById(ids)).thenReturn(new ArrayList<>());

        List<UserDto> result = service.getUsersByIds(ids);

        assertEquals(result, userDtos);
        verify(repository, times(1)).findAllById(ids);
    }

    @Test
    void uploadUserAvatarTest_Success() {
        User user = createUser();
        user.setUserProfilePic(null);
        createBufferedImage();
        createBufferedImagesHolder();

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        doReturn(bufferedImagesHolder).when(imageProcessor).scaleImage(any());
        when(repository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> service.uploadUserAvatar(user.getId(), any(BufferedImage.class)));

        verify(s3Service, times(2)).uploadFile(any(), anyString());
        verify(s3Service, times(0)).deleteFile(anyString());
        verify(repository).save(any(User.class));
    }

    @Test
    void uploadUserAvatarTest_AndDeletePrevious() {
        User user = createUser();
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        BufferedImagesHolder bufferedImagesHolder = mock(BufferedImagesHolder.class);
        when(bufferedImagesHolder.getBigPic()).thenReturn(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        when(bufferedImagesHolder.getSmallPic()).thenReturn(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        when(imageProcessor.scaleImage(any())).thenReturn(bufferedImagesHolder);
        when(repository.save(any(User.class))).thenReturn(user);
        doNothing().when(s3Service).deleteFile(anyString());

        assertDoesNotThrow(() -> service.uploadUserAvatar(user.getId(), any(BufferedImage.class)));

        verify(s3Service, times(2)).uploadFile(any(), anyString());
        verify(s3Service, times(2)).deleteFile(anyString());
        verify(repository, times(2)).save(any(User.class));
    }

    @Test
    void downloadUserAvatarTest_Success() {
        User user = createUser();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId("smallFileId");
        user.setUserProfilePic(userProfilePic);
        byte[] avatarBytes = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(avatarBytes);
        AvatarSize size = AvatarSize.SMALL;

        when(repository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(s3Service.downloadFile("smallFileId")).thenReturn(inputStream);
        when(userContext.getUserId()).thenReturn(1L);

        Resource result = service.downloadUserAvatar(user.getId(), size);

        assertNotNull(result);
        try {
            assertEquals(avatarBytes.length, result.contentLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(s3Service).downloadFile("smallFileId");
        verify(userContext).getUserId();
        verify(profileViewEventPublisher).publish(any(ProfileViewEvent.class));

    }

    @Test
    void deleteUserAvatarTest_Success() {
        User user = createUser();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("fileId");
        userProfilePic.setSmallFileId("smallFileId");
        user.setUserProfilePic(userProfilePic);

        when(repository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        assertDoesNotThrow(() -> service.deleteUserAvatar(user.getId()));

        verify(s3Service).deleteFile("fileId");
        verify(s3Service).deleteFile("smallFileId");
        verify(repository).save(user);
    }

    @Test
    void uploadFileTest_Success() throws Exception {
        Long userId = 1L;
        ByteArrayOutputStream outputStream = mock(ByteArrayOutputStream.class);
        String fileName = "fileName";

        String result = (String) ReflectionTestUtils.invokeMethod(
                service, "uploadFile", userId, outputStream, fileName);

        assertNotNull(result);
        verify(s3Service).uploadFile(eq(outputStream), anyString());
    }

    @Test
    void downloadUserAvatarTest_Fail() {
        AvatarSize size = AvatarSize.SMALL;
        User user = createUser();
        user.setUserProfilePic(null);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> service.downloadUserAvatar(user.getId(), size));
    }

    @Test
    void deleteUserAvatarTest_Fail() {
        User user = createUser();
        user.setUserProfilePic(null);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> service.deleteUserAvatar(user.getId()));
    }

    @Test
    @DisplayName("Try to ban not founded user")
    public void testBanUserWithNotFoundedId() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.banUserById(1L));
    }

    @Test
    @DisplayName("Successfully ban user by id")
    public void testBanUserSuccess() {
        User user = createUser();
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        service.banUserById(1L);

        verify(repository).save(user);
        assertTrue(user.isBanned());
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("email");
        user.setUserProfilePic(new UserProfilePic("fileId", "smallFileId"));
        user.setContactPreference(new ContactPreference(1, user, PreferredContact.EMAIL));
        return user;
    }

    private List<User> createUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("email1");
        user1.setContactPreference(new ContactPreference(1, user1, PreferredContact.EMAIL));
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("email2");
        user2.setContactPreference(new ContactPreference(1, user2, PreferredContact.EMAIL));
        return List.of(user1, user2);
    }

    private void createBufferedImagesHolder() {
        bufferedImagesHolder = new BufferedImagesHolder(bufferedImage);
    }

    private void createBufferedImage() {
        bufferedImage = new BufferedImage(100, 100, 1);
    }
}