package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Test
    @DisplayName("testing requester absence")
    public void testValidationWithRequesterAbsence() {
        long requesterId = 1L;
        requesterAndReceiverExistenceTest(false, true, requesterId, 2L,
                "MentorshipRequest sender with ID: " + requesterId + " not registered in Database");
    }

    @Test
    @DisplayName("testing receiver absence")
    public void testValidationWithReceiverAbsence() {
        long receiverId = 4L;
        requesterAndReceiverExistenceTest(true, false, 3L, receiverId,
                "MentorshipRequest receiver with ID: " + receiverId + " not registered in Database");
    }

    private void requesterAndReceiverExistenceTest(boolean requesterExistence, boolean receiverExistence,
                                                   long requesterId, long receiverId, String exceptionMessage) {
        lenient().when(userRepository.existsById(requesterId)).thenReturn(requesterExistence);
        lenient().when(userRepository.existsById(receiverId)).thenReturn(receiverExistence);

        IllegalArgumentException absenceException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator
                        .validateMentorshipRequestParticipantsExistence(requesterId, receiverId));
        assertEquals(exceptionMessage, absenceException.getMessage());
    }

    @Test
    @DisplayName("testing requester and receiver are not equal by ids")
    public void testValidationWithEqualParticipants() {
        long requesterId = 1L;
        IllegalArgumentException reflectionException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateRequesterNotEqualToReceiver(requesterId, requesterId));
        assertEquals("MentorshipRequest sender with id: " + requesterId + " should not be equal to receiver",
                reflectionException.getMessage());
    }

    @Test
    @DisplayName("testing request frequency with non-valid value")
    public void testValidationWithRequestFrequency() {
        long requesterId = 1L;
        long receiverId = 2L;
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastMentorshipRequestDate =
                currentDate.minusDays(mentorshipRequestValidator.getMENTORSHIP_REQUEST_FREQUENCY_IN_DAYS() - 1);
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .createdAt(lastMentorshipRequestDate).build();

        when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(mentorshipRequest));
        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestValidator
                .validateMentorshipRequestFrequency(requesterId, receiverId, currentDate));
    }

    @Test
    @DisplayName("testing if status is PENDING validation with ACCEPTED status")
    public void testValidationRequestStatusIsPendingWithAcceptedStatus() {
        RequestStatus requestStatus = RequestStatus.ACCEPTED;

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(requestStatus));
        assertEquals("Mentorship Request is already accepted", exception.getMessage());
    }

    @Test
    @DisplayName("testing if status is PENDING validation with REJECTED status")
    public void testValidationRequestStatusIsPendingWithRejectedStatus() {
        RequestStatus requestStatus = RequestStatus.REJECTED;

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(requestStatus));
        assertEquals("Mentorship Request is already rejected", exception.getMessage());
    }

    @Test
    @DisplayName("testing if status is PENDING validation with NON-PENDING status")
    public void testValidationRequestStatusIsPendingWithNonPendingStatus() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(null));
        assertEquals("Mentorship Request must be in pending mode", exception.getMessage());
    }

    @Test
    @DisplayName("testing validation receiver is not mentor of requester")
    public void testValidationReceiverIsNotMentorOfRequester() {
        User requesterUser = User.builder().id(1L).build();
        User receiverUser = User.builder().id(2L).build();
        requesterUser.setMentors(List.of(receiverUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateReceiverIsNotMentorOfRequester(requesterUser, receiverUser));
        assertEquals("User with ID: 2 is already the mentor of User with ID: 1", exception.getMessage());
    }
}