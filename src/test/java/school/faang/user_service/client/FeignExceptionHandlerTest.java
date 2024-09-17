package school.faang.user_service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignExceptionHandlerTest {
    private static final String TEST_MESSAGE = "test message";
    private static final String RESPONSE_JSON = "{\"message\":\"We only accept [USD, EUR]\"}";

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FeignExceptionHandler feignExceptionHandler;

    @Test
    @DisplayName("Test http client error exception handler")
    void testHttpClientErrorExceptionHandlerSuccessful() throws IOException {
        var feignException = mock(FeignException.class);
        var jsonNode = mock(JsonNode.class);

        when(feignException.contentUTF8()).thenReturn(RESPONSE_JSON);
        when(objectMapper.readTree(RESPONSE_JSON)).thenReturn(jsonNode);
        when(jsonNode.has("message")).thenReturn(true);
        when(jsonNode.get("message")).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(TEST_MESSAGE);

        ResponseEntity<ProblemDetail> response = feignExceptionHandler.handleFeignException(feignException);
        assertResponse(response, HttpStatus.BAD_REQUEST);
    }

    private void assertResponse(ResponseEntity<ProblemDetail> response, HttpStatus httpStatus) {
        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(httpStatus);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ProblemDetail::getStatus)
                .isEqualTo(httpStatus.value());
        assertThat(response.getBody())
                .extracting(ProblemDetail::getDetail)
                .isEqualTo(TEST_MESSAGE);
    }

}