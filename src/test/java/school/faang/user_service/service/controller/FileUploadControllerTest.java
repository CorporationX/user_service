package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.fileUpload.FileUploadController;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class FileUploadControllerTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadFile() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[1024]);
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ResponseEntity<String> response = fileUploadController.uploadFile(file, 1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(s3Service, times(2)).uploadFile(any(Path.class), anyString());
        verify(userRepository).save(any(User.class));
    }
}