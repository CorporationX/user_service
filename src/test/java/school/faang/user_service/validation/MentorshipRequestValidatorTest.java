package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @InjectMocks
    private MentorshipRequestValidator validator;

    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final long REQUEST_ID = 0L;
    private final LocalDateTime THREE_MONTH_AGO = LocalDateTime.now().minusMonths(3);
    private final String DESCRIPTION = "description";
    private MentorshipRequest request;
    private User requester;
    private User receiver;
    private RejectionDto rejection;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(REQUESTER_ID);
        receiver = new User();
        receiver.setId(RECEIVER_ID);

        request = new MentorshipRequest();
        request.setId(REQUEST_ID);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(DESCRIPTION);

        rejection = RejectionDto.builder()
                .reason("reason")
                .build();
    }

    @Test
    void testCorrectRequest() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        assertDoesNotThrow(() -> validator.validateRequest(request));
    }

    @Test
    void testRequestWithoutDescription() {
        request.setDescription("");
        assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testRequesterDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> validator.validateRequest(request));
    }
    @Test
    void testReceiverDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testReceiverIsRequester() {
        User receiver = new User();
        receiver.setId(REQUESTER_ID);
        request.setReceiver(receiver);

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(request.getReceiver().getId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testOneRequestAt3Months() {
        request.setUpdatedAt(THREE_MONTH_AGO.plusDays(1));

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(request));
        assertThrows(RuntimeException.class, () -> validator.validateRequest(request));
    }

    @Test
    void requestNotExistForAcceptRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> validator.validateAcceptRequest(REQUEST_ID));
    }

    @Test
    void receiverNotMentorForAcceptRequest() {
        requester.setMentors(List.of(receiver));
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        assertThrows(IllegalArgumentException.class, () -> validator.validateAcceptRequest(REQUEST_ID));
    }

    @Test
    void requestNotExistForRejectRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> validator.validateRejectRequest(REQUEST_ID, rejection));
    }

    @Test
    void rejectionHaveReasonForRejectRequest() {
        rejection.setReason("");
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        assertThrows(IllegalArgumentException.class, () -> validator.validateRejectRequest(REQUEST_ID, rejection));
    }
}