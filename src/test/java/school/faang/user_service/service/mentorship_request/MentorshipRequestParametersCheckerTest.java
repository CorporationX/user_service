package school.faang.user_service.service.mentorship_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.EMPTY_DESCRIPTION;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.ONCE_EVERY_THREE_MONTHS;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_IS_ACCEPTED_BEFORE;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_TO_HIMSELF;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestParametersCheckerTest {
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestParametersChecker checker;

    @Test
    void testUserSendRequestToHimself() {
        long requesterId = 1L;
        long receiverId = 1L;
        String description = "description";

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(REQUEST_TO_HIMSELF, exception.getMessage());
    }

    @Test
    void testDescriptionIsNull() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "  ";

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(EMPTY_DESCRIPTION, exception.getMessage());
    }

    @Test
    void testRequesterNotFound() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, false);
        whenExistById(receiverId, true);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        String expected = String.format(USER_NOT_FOUND, requesterId);
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testReceiverNotFound() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, true);
        whenExistById(receiverId, false);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        String expected = String.format(USER_NOT_FOUND, receiverId);
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testRequestWithinTheLastThreeMonths() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, true);
        whenExistById(receiverId, true);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        LocalDateTime minusTwoMonth = LocalDateTime.now().minusMonths(2);
        mentorshipRequest.setCreatedAt(minusTwoMonth);
        whenFindLatestRequest(mentorshipRequest, requesterId, receiverId);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(ONCE_EVERY_THREE_MONTHS, exception.getMessage());
    }

    @Test
    void testExistAcceptedRequest() {
        long requesterId = 1L;
        long receiverId = 2L;
        when(mentorshipRequestRepository
                .existAcceptedRequest(requesterId, receiverId))
                .thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> checker.checkExistAcceptedRequest(requesterId, receiverId));
        String expected = String.format(REQUEST_IS_ACCEPTED_BEFORE, requesterId, receiverId);
        assertEquals(expected, exception.getMessage());
    }

    private RuntimeException assertException(long requesterId, long receiverId, String description) {
        return assertThrows(RuntimeException.class,
                () -> checker.checkRequestParams(requesterId, receiverId, description));
    }

    private void whenExistById(long id, boolean exist) {
        lenient().when(userRepository
                        .existsById(id))
                .thenReturn(exist);
    }

    private void whenFindLatestRequest(MentorshipRequest mentorshipRequest, long requesterId, long receiverId) {
        when(mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(mentorshipRequest));
    }
}