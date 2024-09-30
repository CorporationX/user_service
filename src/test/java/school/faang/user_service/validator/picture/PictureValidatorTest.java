package school.faang.user_service.validator.picture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PictureValidatorTest {

    @InjectMocks
    private PictureValidator pictureValidator;
    @Mock
    private MultipartFile multipartFile;

    private static final long MAX_FILE_SIZE = 5_242_880L;
    private static final long LARGE_SIZE = 6_000_000L;
    private static final long SMALL_SIZE = 4_000_000L;

    @BeforeEach
    public void init() {
        pictureValidator = new PictureValidator(MAX_FILE_SIZE);
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка при превышении допустимого размера")
        public void whenPictureSizeExceededThenThrowException() {
            when(multipartFile.getSize()).thenReturn(LARGE_SIZE);

            assertThrows(MaxUploadSizeExceededException.class, () ->
                    pictureValidator.checkPictureSizeExceeded(multipartFile));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если размер равен допустимому")
        public void whenPictureSizeEqualsAllowedSizeThenSuccess() {
            when(multipartFile.getSize()).thenReturn(MAX_FILE_SIZE);

            assertDoesNotThrow(() -> pictureValidator.checkPictureSizeExceeded(multipartFile));
        }

        @Test
        @DisplayName("Успех если размер меньше допустимого")
        public void whenPictureSizeNotExceededThenSuccess() {
            when(multipartFile.getSize()).thenReturn(SMALL_SIZE);

            assertDoesNotThrow(() -> pictureValidator.checkPictureSizeExceeded(multipartFile));
        }
    }
}