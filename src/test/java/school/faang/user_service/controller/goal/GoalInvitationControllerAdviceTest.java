package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


class GoalInvitationControllerAdviceTest {
    private static final String TEST_MESSAGE = "test message";

    @Mock
    private InvitationEntityNotFoundException invitationEntityNotFoundException;

    @Mock
    private InvitationCheckException invitationCheckException;

    @InjectMocks
    private GoalInvitationControllerAdvice goalInvitationControllerAdvice;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("Handle Entity not found exception")
    void testHandleEntityNotFoundExceptionSuccessful() {
        when(invitationEntityNotFoundException.getMessage()).thenReturn(TEST_MESSAGE);
        var response = goalInvitationControllerAdvice
                .handleEntityNotFoundException(invitationEntityNotFoundException);
        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Handle same user exception")
    void testHandInvitationCheckExceptionSuccessful() {
        when(invitationCheckException.getMessage()).thenReturn(TEST_MESSAGE);
        var response = goalInvitationControllerAdvice
                .handleInvitationCheckException(invitationCheckException);
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

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}