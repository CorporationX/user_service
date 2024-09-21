package school.faang.user_service.service.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.validator.httpResponse.HttpResponseValidator;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoteImageServiceTest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String IMAGE_PNG = "image/png";

    @InjectMocks
    private RemoteImageService remoteImageService;

    @Mock
    private HttpResponseValidator httpResponseValidator;

    private final HttpURLConnection connection = mock(HttpURLConnection.class);

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("When response code different from 200 then throws IOException")
        void whenResponseCodeNotOkThenThrowsIOException() throws IOException {

            when(connection.getResponseCode())
                    .thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

            assertThrows(IOException.class,
                    () -> remoteImageService.getUserProfileImageFromRemoteService(),
                    "Failed to fetch image");
        }
    }
}