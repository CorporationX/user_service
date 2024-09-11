package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GoalInvitationControllerAdviceTest {
    private static final String TEST_MESSAGE = "test message";

    private final GoalInvitationControllerAdvice goalInvitationControllerAdvice = new GoalInvitationControllerAdvice();

    @Test
    @DisplayName("Handle Entity not found exception")
    void testHandleEntityNotFoundExceptionSuccessful() {
        var invitationEntityNotFoundException = new InvitationEntityNotFoundException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = goalInvitationControllerAdvice
                .handleEntityNotFoundException(invitationEntityNotFoundException);
        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Handle same user exception")
    void testHandInvitationCheckExceptionSuccessful() {
        var invitationCheckException = new InvitationCheckException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = goalInvitationControllerAdvice
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
}