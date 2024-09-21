package school.faang.user_service.validator.httpResponse;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.httpResponse.HttpResponseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class HttpResponseValidatorTest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, List<String>> headers = new HashMap<>();

    @InjectMocks
    private HttpResponseValidator httpResponseValidator;

    private HttpResponseData httpResponseData;

    @Nested
    class NegativeTests {

        @BeforeEach
        void init() {
            httpResponseData = HttpResponseData.builder()
                    .headers(headers)
                    .build();
        }

        @Test
        @DisplayName("Throws ValidationException if headers don't contains Content-Type")
        void whenHeadersNotContainsContentTypeThenThrowsValidationException() {

            assertThrows(ValidationException.class,
                    () -> httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }

        @Test
        @DisplayName("Throws ValidationException if headers don't contains Content-Type but contains Content-Length")
        void whenHeadersNotContainsContentTypeAndContainsContentLengthThenThrowsValidationException() {
            httpResponseData.getHeaders().put(CONTENT_LENGTH, null);

            assertThrows(ValidationException.class,
                    () -> httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }

        @Test
        @DisplayName("Throws ValidationException if headers don't contains Content-Length")
        void whenHeadersNotContainsContentLengthThenThrowsValidationException() {

            assertThrows(ValidationException.class,
                    () -> httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }

        @Test
        @DisplayName("Throws ValidationException if headers don't contains Content-Length but contains Content-Type")
        void whenHeadersNotContainsContentLengthAndContainsContentTypeThenThrowsValidationException() {
            httpResponseData.getHeaders().put(CONTENT_TYPE, null);

            assertThrows(ValidationException.class,
                    () -> httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }

    }

    @Nested
    class PositiveTests {

        @BeforeEach
        void init() {
            headers.put(CONTENT_TYPE, null);
            headers.put(CONTENT_LENGTH, null);

            httpResponseData = HttpResponseData.builder()
                    .headers(headers)
                    .build();
        }

        @Test
        @DisplayName("Not thrown ValidationException if headers contains Content-Type")
        void whenHeadersNotContainsContentTypeThenThrowsValidationException() {
            assertDoesNotThrow(() ->
                    httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }

        @Test
        @DisplayName("Not thrown ValidationException if headers contains Content-Length")
        void whenHeadersNotContainsContentLengthThenThrowsValidationException() {
            assertDoesNotThrow(() ->
                    httpResponseValidator.validateRequiredHeadersForS3PictureUploading(httpResponseData));
        }
    }
}