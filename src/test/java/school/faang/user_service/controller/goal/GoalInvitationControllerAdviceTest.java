package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.response.ErrorResponse;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;


class GoalInvitationControllerAdviceTest {
    private static final String ENTITY_MESSAGE = "Entity not found";
    private static final String SAME_USERS_MESSAGE = "Users should not be same";

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
    void testHandleEntityNotFoundException() {
        when(invitationEntityNotFoundException.getMessage()).thenReturn(ENTITY_MESSAGE);
        ErrorResponse response = goalInvitationControllerAdvice
                .handleEntityNotFoundException(invitationEntityNotFoundException);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", ENTITY_MESSAGE);
    }

    @Test
    @DisplayName("Handle same user exception")
    void testHandInvitationCheckException() {
        when(invitationCheckException.getMessage()).thenReturn(SAME_USERS_MESSAGE);
        ErrorResponse response = goalInvitationControllerAdvice
                .handleInvitationCheckException(invitationCheckException);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("message", SAME_USERS_MESSAGE);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}