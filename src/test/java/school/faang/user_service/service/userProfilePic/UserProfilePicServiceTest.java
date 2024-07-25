package school.faang.user_service.service.userProfilePic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.userProfilePic.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfilePicServiceTest {

    @InjectMocks
    private UserProfilePicService userProfilePicService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserProfilePicMapper userProfilePicMapper;
    private MultipartFile multipartFile;
    private InputStream inputStream;
    private User user;
    private UserProfileDto userProfileDto;
    private UserProfilePic userProfilePic;
    private final Long userId = 1L;

    @BeforeEach
    void init() {
        userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("1");
        userProfilePic.setSmallFileId("2");

        multipartFile = Mockito.mock(MultipartFile.class);
        inputStream = Mockito.mock(InputStream.class);

        user = User.builder()
                .id(1L)
                .username("Vasa")
                .userProfilePic(userProfilePic)
                .build();

        userProfileDto = UserProfileDto.builder()
                .id(1L)
                .username("Vasa")
                .build();

    }

    @Test
    @DisplayName("checkTheUserInTheDatabaseException")
    void testCheckTheUserInTheDatabaseFalse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> userProfilePicService.addImageInProfile(userId, multipartFile));
    }

    @Test
    @DisplayName("uploadProfileException")
    void testAddImageInProfileUploadProfileException() throws IOException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(s3Service.uploadProfile(any(MultipartFile.class), anyString())).thenThrow(new IOException("exception"));

        Exception exception = assertThrows(IOException.class, () ->
                userProfilePicService.addImageInProfile(userId, multipartFile));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("addImageInProfileToDtoValid")
    void testAddImageInProfileToDto() throws IOException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(s3Service.uploadProfile(any(MultipartFile.class), any(String.class))).thenReturn(userProfilePic);
        when(userProfilePicMapper.toDto(any(User.class))).thenReturn(userProfileDto);

        UserProfileDto result = userProfilePicService.addImageInProfile(userId, multipartFile);

        verify(userRepository, times(1)).findById(anyLong());
        verify(s3Service, times(1)).uploadProfile(any(MultipartFile.class), anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfilePicMapper, times(1)).toDto(any(User.class));

        assertNotNull(result);
    }

    @Test
    @DisplayName("getImageFromProfileException")
    void testGetImageFromProfileException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(s3Service.downloadingByteImage(anyString())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                userProfilePicService.getImageFromProfile(userId));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("GetImageFromProfileValid")
    void testGetImageFromProfile() throws IOException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(s3Service.downloadingByteImage(anyString())).thenReturn(inputStream);

        userProfilePicService.getImageFromProfile(userId);

        verify(userRepository, times(1)).findById(anyLong());
        verify(s3Service, times(1)).downloadingByteImage(anyString());
    }

    @Test
    @DisplayName("deleteImageException")
    void testDeleteImageFromProfileException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        doThrow(new RuntimeException("exception")).when(s3Service).deleteImage(any(UserProfilePic.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                userProfilePicService.deleteImageFromProfile(userId));


        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("saveImageException")
    void testDeleteImageFromProfileSaveException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        doNothing().when(s3Service).deleteImage(any(UserProfilePic.class));
        doThrow(new RuntimeException("exception")).when(userRepository).save(any(User.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                userProfilePicService.deleteImageFromProfile(userId));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("dtoInUserProfileDtoValid")
    void testDeleteImageFromProfileToDtoValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        doNothing().when(s3Service).deleteImage(any(UserProfilePic.class));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userProfilePicMapper.toDto(any(User.class))).thenReturn(userProfileDto);

        userProfilePicService.deleteImageFromProfile(userId);

        verify(userRepository, times(1)).findById(anyLong());
        verify(s3Service, times(1)).deleteImage(any(UserProfilePic.class));
        verify(userProfilePicMapper, times(1)).toDto(any(User.class));
    }
}