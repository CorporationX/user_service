package school.faang.user_service.service.controller.fileUpload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.fileUpload.FileUploadController;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileUploadControllerTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadValidFile() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024l);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new User()));
        doNothing().when(s3Service).uploadFile(any(Path.class), any(String.class));

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Upload successful", response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUploadFileExceedsSizeLimit() {
        when(file.getSize()).thenReturn(6 * 1024 * 1024L);

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("File size exceeds limit (5 MB)", response.getBody());
    }

    @Test
    void testUploadEmptyFile() {
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("File is empty", response.getBody());
    }

    @Test
    void testUploadFileForNonExistentUser() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testUploadFileInternalError() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        doThrow(new IOException()).when(file).transferTo(any(Path.class));

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal server error", response.getBody());
    }

    @Test
    void testGetUserProfilePic() {
        User user = new User();
        UserProfilePic profilePic = new UserProfilePic("fileId", "smallFileId");
        user.setUserProfilePic(profilePic);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        ResponseEntity<UserProfilePic> response = fileUploadController.getUserProfilePic(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(profilePic, response.getBody());
    }

    @Test
    void testGetUserProfilePicForNonExistentUser() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<UserProfilePic> response = fileUploadController.getUserProfilePic(1L);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testDeleteUserProfilePic() {
        User user = new User();
        UserProfilePic profilePic = new UserProfilePic("fileId", "smallFileId");
        user.setUserProfilePic(profilePic);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        doNothing().when(s3Service).deleteFile(any(String.class));

        ResponseEntity<String> response = fileUploadController.deleteUserProfilePic(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Avatar deleted successfully", response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUserProfilePicForNonExistentUser() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<String> response = fileUploadController.deleteUserProfilePic(1L);

        assertEquals(500, response.getStatusCodeValue());
    }
}