package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

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
    public void testMentorshipRequestDtoWithNullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDto(null));
        assertEquals("Mentorship request cannot be null", exception.getMessage());
    }

    @Test
    public void testMentorshipRequestDtoWithDescriptionNullValue() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDto(mentorshipRequestDto));
        assertEquals("Mentorship request description cannot be null", exception.getMessage());
    }

    @Test
    public void testMentorshipRequestDtoWithLessThanMinimalRequesterId() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("description").build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDto(mentorshipRequestDto));
        assertEquals("Mentorship requester id cannot be less than " +
                mentorshipRequestValidator.getMIN_USER_ID(), exception.getMessage());
    }

    @Test
    public void testMentorshipRequestDtoWithLessThanMinimalReceiverId() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("description")
                .requesterId(1).build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDto(mentorshipRequestDto));
        assertEquals("Mentorship receiver id cannot be less than " +
                mentorshipRequestValidator.getMIN_USER_ID(), exception.getMessage());
    }


    @Test
    public void testMentorshipRequestFilterDtoWithNullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestFilterDto(null));
        assertEquals("Mentorship request filter cannot be null", exception.getMessage());
    }


    @Test
    public void testMentorshipRequestRejectionDtoWithNullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRejectionDto(null));
        assertEquals("Rejection reason cannot be null", exception.getMessage());
    }


    @Test
    public void testMentorshipRequestRejectionDtoWithLessThanMinimalRequestId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestId(0));
        assertEquals("Mentorship request id cannot be less than " +
                mentorshipRequestValidator.getMIN_REQUEST_ID(), exception.getMessage());
    }

    @Test
    public void testValidationWithBlankDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDescription("   "));
        assertEquals("Description cannot ve blank", exception.getMessage());
    }

    @Test
    public void testValidationWithTooLongDescription() {
        StringBuilder description = new StringBuilder();
        IntStream.range(0, mentorshipRequestValidator.getMAX_DESCRIPTION_LENGTH() + 1)
                .forEach(i -> description.append("a"));

        assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateMentorshipRequestDescription(description.toString()));
    }

    @Test
    public void testValidationWithRequesterAbsence() {
        requesterAndReceiverExistenceTest(false, true, 1L, 2L,
                "MentorshipRequest sender is not registered in Database");
    }

    @Test
    public void testValidationWithReceiverAbsence() {
        requesterAndReceiverExistenceTest(true, false, 3L, 4L,
                "MentorshipRequest receiver is not registered in Database");
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
    public void testValidationWithEqualParticipants() {
        long requesterId = 1L;
        IllegalArgumentException reflectionException = assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestValidator.validateRequesterNotEqualToReceiver(requesterId, requesterId));
        assertEquals("MentorshipRequest sender cannot be equal to receiver",
                reflectionException.getMessage());
    }

    @Test
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
    public void testValidationRequestStatusIsPendingWithAcceptedStatus() {
        RequestStatus requestStatus = RequestStatus.ACCEPTED;

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(requestStatus));
        assertEquals("Mentorship Request is already accepted", exception.getMessage());
    }

    @Test
    public void testValidationRequestStatusIsPendingWithRejectedStatus() {
        RequestStatus requestStatus = RequestStatus.REJECTED;

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(requestStatus));
        assertEquals("Mentorship Request is already rejected", exception.getMessage());
    }

    @Test
    public void testValidationRequestStatusIsPendingWithNonPendingStatus() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mentorshipRequestValidator.validateRequestStatusIsPending(null));
        assertEquals("Mentorship Request must be in pending mode", exception.getMessage());
    }
}