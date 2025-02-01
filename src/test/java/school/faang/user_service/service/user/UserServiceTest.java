package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.dto.user.UserAvatarSize;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PersonToUserMapper personToUserMapper;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private UserService userService;

    @Test
    void getUsersByIdsSuccessTest() {
        List<User> userList = List.of(User.builder().id(1L).username("Alex").email("alex@mail.ru").build(),
                User.builder().id(3L).username("Ann").email("ann@mail.ru").build());
        List<UserDto> userDtoList = List.of(UserDto.builder().id(1L).username("Alex").email("alex@mail.ru").build(),
                UserDto.builder().id(3L).username("Ann").email("ann@mail.ru").build());
        when(userRepository.findAllById(List.of(1L, 3L))).thenReturn(userList);
        when(userMapper.userListToUserDtoList(userList)).thenReturn(userDtoList);
        assertDoesNotThrow(() -> {
            List<UserDto> users = userService.getUsersByIds(List.of(1L, 3L));
            assertEquals(2, users.size());
            assertEquals(1, users.get(0).id());
            assertEquals("alex@mail.ru", users.get(0).email());
            assertEquals("Alex", users.get(0).username());
            assertEquals(3, users.get(1).id());
            assertEquals("ann@mail.ru", users.get(1).email());
            assertEquals("Ann", users.get(1).username());
        });
        verify(userRepository, times(1)).findAllById(List.of(1L, 3L));
        verify(userMapper, times(1)).userListToUserDtoList(userList);
    }

    @Test
    void getUsersByIdsWithNegativeUserIdFailTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.getUsersByIds(List.of(-1L,2L)));
        String expectedMessage = "Invalid user ID passed. User ID must not be less than 1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(0)).findAllById(List.of(-1L, 2L));
    }

    @Test
    void getUserSuccessTest() {
        User user = User.builder().id(2L).username("Alex").email("alex@mail.ru").build();
        UserDto userDto = UserDto.builder().id(2L).username("Alex").email("alex@mail.ru").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        assertDoesNotThrow(() -> {
            UserDto result = userService.getUser(2L);
            assertEquals(2, result.id());
            assertEquals("alex@mail.ru", result.email());
            assertEquals("Alex", result.username());
        });
        verify(userRepository, times(1)).findById(2L);
        verify(userMapper, times(1)).userToUserDto(user);
    }

    @Test
    void getUserForNonExistentUserFailTest() {
        when(userRepository.findById(200L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(200L));
        verify(userRepository, times(1)).findById(200L);
    }

    @Test
    void updateUserAvatarSuccessTest() throws IOException {
        FileInputStream input = new FileInputStream("src/test/resources/avatar/avatar.jpg");
        MultipartFile multipartFile = new MockMultipartFile("avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, IOUtils.toByteArray(input));
        ReflectionTestUtils.setField(userService, "maxImageSizeMb", 5);
        when(userRepository.findUserProfilePicByUserId(1L)).thenReturn(UserProfilePic.builder().fileId("key").smallFileId("smallKey").build());
        when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.updateUserAvatar(1L, multipartFile));
        verify(userRepository, times(1)).findUserProfilePicByUserId(1L);
        verify(s3Service, times(1)).deleteFile("key");
        verify(s3Service, times(1)).deleteFile("smallKey");
        verify(userRepository, times(1)).saveUserProfilePic(1L, UserProfilePic.builder()
                .smallFileId("users/1/avatar/small-avatar.jpg")
                .fileId("users/1/avatar/large-avatar.jpg")
                .build());
    }

    @Test
    void updateUserAvatarWithFileSizeGreaterThan5MbFailTest() {
        MultipartFile multipartFile = new MockMultipartFile("avatar", "avatar/avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[(int) 6]);
        ReflectionTestUtils.setField(userService, "maxImageSizeMb", 5);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserAvatar(1L, multipartFile));
        verify(userRepository, times(0)).findUserProfilePicByUserId(1L);
    }

    @Test
    void updateUserAvatarWithNonExistentUserFailTest() {
        MultipartFile multipartFile = new MockMultipartFile("avatar", "avatar/avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[(int) 2]);
        ReflectionTestUtils.setField(userService, "maxImageSizeMb", 5);
        when(userRepository.existsById(100L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserAvatar(100L, multipartFile));
        verify(userRepository, times(0)).findUserProfilePicByUserId(100L);
    }

    @Test
    void getUserAvatarSuccessTest() {
        byte[] avatarBytes = "avatar".getBytes();
        InputStream avatarStream = new ByteArrayInputStream(avatarBytes);
        when(userRepository.findUserProfilePicByUserId(1L)).thenReturn(UserProfilePic.builder().fileId("key").smallFileId("smallKey").build());
        when(s3Service.downloadFile("key")).thenReturn(avatarStream);
        assertDoesNotThrow(() -> {
            byte[] result = userService.getUserAvatar(1L, UserAvatarSize.LARGE);
            assertArrayEquals(avatarBytes, result);
        });
        verify(userRepository, times(1)).findUserProfilePicByUserId(1L);
        verify(s3Service, times(1)).downloadFile("key");
        verify(s3Service, times(0)).downloadFile("smallKey");
    }

    @Test
    void getUserAvatarForNonExistentAvatarFailTest() {
        when(userRepository.findUserProfilePicByUserId(1L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.getUserAvatar(1L, UserAvatarSize.LARGE));
        verify(userRepository, times(1)).findUserProfilePicByUserId(1L);
        verify(s3Service, times(0)).downloadFile("key");
    }

    @Test
    void deleteUserAvatarSuccessTest() {
        when(userRepository.findUserProfilePicByUserId(1L)).thenReturn(UserProfilePic.builder().fileId("key").smallFileId("smallKey").build());
        assertDoesNotThrow(() -> userService.deleteUserAvatar(1L));
        verify(userRepository, times(1)).findUserProfilePicByUserId(1L);
        verify(s3Service, times(1)).deleteFile("key");
        verify(s3Service, times(1)).deleteFile("smallKey");
    }

    @Test
    void deleteUserAvatarForNonExistentAvatarFailTest() {
        when(userRepository.findUserProfilePicByUserId(1L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserAvatar(1L));
        verify(userRepository, times(1)).findUserProfilePicByUserId(1L);
        verify(s3Service, times(0)).deleteFile("key");
        verify(s3Service, times(0)).deleteFile("smallKey");
    }

    @Test
    void banUserSuccessTest() {
        User user = User.builder().id(2L).username("Alex").email("alex@mail.ru").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.banUser(2L));
        verify(userRepository).findById(2L);
    }

    @Test
    void banUserForNonExistentUserFailTest() {
        when(userRepository.findById(200L)).thenThrow(EntityNotFoundException.class);
        assertThrows((EntityNotFoundException.class), () -> userService.banUser(200L));
        verify(userRepository).findById(200L);
    }
}
