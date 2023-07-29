package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
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
    private final LocalDateTime THREE_MONTH_AGO = LocalDateTime.now().minusMonths(3);
    private final String DESCRIPTION = "description";
    private MentorshipRequest request;

    @BeforeEach
    void setUp() {
        User requester = new User();
        requester.setId(REQUESTER_ID);
        User receiver = new User();
        receiver.setId(RECEIVER_ID);

        request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(DESCRIPTION);
    }

    @Test
    void testCorrectRequest() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void testRequestWithoutDescription() {
        request.setDescription("");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void testRequesterDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> validator.validate(request));
    }
    @Test
    void testReceiverDoesNotExists() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(false);
        assertThrows(IndexOutOfBoundsException.class, () -> validator.validate(request));
    }

    @Test
    void testReceiverIsRequester() {
        User receiver = new User();
        receiver.setId(REQUESTER_ID);
        request.setReceiver(receiver);

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(request.getReceiver().getId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void testOneRequestAt3Months() {
        request.setUpdatedAt(THREE_MONTH_AGO.plusDays(1));

        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(request));
        assertThrows(RuntimeException.class, () -> validator.validate(request));
    }
}