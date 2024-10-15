package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.service.impl.UserServiceImpl;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UploadCsvFileControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadCsvFile_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "students.csv",
                "text/csv", "name,email\nJohn Doe,johndoe@example.com".getBytes());

        doNothing().when(userService).processCsvFile(any(InputStream.class));

        ResponseEntity<String> response = userController.uploadCsvFile(file);

        assertEquals(ResponseEntity.ok("The file has been successfully uploaded and processed"), response);
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        verify(userService).processCsvFile(inputStreamCaptor.capture());
    }

    @Test
    public void testUploadCsvFile_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/csv", new byte[0]);

        ResponseEntity<String> response = userController.uploadCsvFile(emptyFile);

        assertEquals(ResponseEntity.badRequest().body("The file cannot be empty"), response);
        verify(userService, never()).processCsvFile(any(InputStream.class));
    }

    @Test
    void testUploadCsvFile_FileReadError() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenThrow(new IOException("File read error"));

        ResponseEntity<String> response = userController.uploadCsvFile(mockFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error reading the file", response.getBody());
        verify(userService, never()).processCsvFile(any(InputStream.class));
    }
}