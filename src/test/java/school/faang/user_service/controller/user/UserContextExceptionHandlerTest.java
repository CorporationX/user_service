package school.faang.user_service.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.user.UserContextException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserContextExceptionHandlerTest {
    private static final String TEST_MESSAGE = "test message";

    private final UserContextExceptionHandler userContextExceptionHandler = new UserContextExceptionHandler();

    @Test
    @DisplayName("Test user context exception handler")
    void testHandleUserContextExceptionSuccessful() {
        var userContextException = new UserContextException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = userContextExceptionHandler
                .handleUserContextException(userContextException);
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