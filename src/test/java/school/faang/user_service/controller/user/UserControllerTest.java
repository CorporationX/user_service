package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.image.ImageProcessor;
import school.faang.user_service.service.user.parse.DataFromFileService;
import school.faang.user_service.testData.TestData;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.message.ExceptionMessage.INPUT_OUTPUT_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NO_FILE_IN_REQUEST;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private DataFromFileService dataFromFileService;

    @Captor
    private ArgumentCaptor<InputStream> inputStreamArgumentCaptor;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private InputStream inputStream;

    private UserDto userDto;
    private BufferedImage bufferedImage;

    @BeforeEach
    void setUp() {
        TestData testData = new TestData();
        userDto = testData.getUserDto();
        bufferedImage = testData.getBufferedImage();
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call userService.createUser when valid userDto is passed")
        @Test
        void createUserTest() {
            assertDoesNotThrow(() -> userController.createUser(userDto));

            verify(userService).createUser(userDto);
        }

        @DisplayName("should call userService.uploadUserAvatar when passed file is appropriate size")
        @Test
        void uploadUserAvatarTest() {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getSize()).thenReturn(100L);
            when(imageProcessor.getBufferedImage(file)).thenReturn(bufferedImage);

            userController.uploadUserAvatar(1L, file);

            verify(imageProcessor).getBufferedImage(file);
            verify(userService).uploadUserAvatar(1L, bufferedImage);
        }

        @DisplayName("should return userAvatar with image contentType")
        @Test
        void downloadUserAvatarTest() {
            byte[] avatarInBytes = {0};
            when(userService.downloadUserAvatar(anyLong())).thenReturn(avatarInBytes);

            var responseEntity = assertDoesNotThrow(() -> userController.downloadUserAvatar(anyLong()));

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        }

        @DisplayName("should call userService.deleteAvatar")
        @Test
        void deleteUserAvatarTest() {
            assertDoesNotThrow(() -> userController.deleteUserAvatar(anyLong()));

            verify(userService).deleteUserAvatar(anyLong());
        }

        @DisplayName("should call dataFromFileService.saveUsersFromFile when passed")
        @Test
        void uploadDataTest() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenReturn(inputStream);

            userController.uploadData(multipartFile);

            verify(dataFromFileService).saveUsersFromFile(inputStreamArgumentCaptor.capture());
            assertEquals(inputStream, inputStreamArgumentCaptor.getValue());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throwException when passed file isn't appropriate size")
        @Test
        void uploadUserAvatarTest() {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getSize()).thenReturn(Long.MAX_VALUE);

            assertThrows(DataValidationException.class, () -> userController.uploadUserAvatar(1L, file));

            verifyNoInteractions(imageProcessor);
            verifyNoInteractions(userService);
        }

        @DisplayName("should throw exception when multipartFile.isEmpty() == true")
        @Test
        void uploadDataWhenEmptyFileTest() {
            when(multipartFile.isEmpty()).thenReturn(true);
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> userController.uploadData(multipartFile));
            assertEquals(NO_FILE_IN_REQUEST.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when multipartFile.getInputStream()")
        @Test
        void uploadDataWhenIOExceptionTest() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenThrow(new IOException());
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> userController.uploadData(multipartFile));
            assertEquals(INPUT_OUTPUT_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}