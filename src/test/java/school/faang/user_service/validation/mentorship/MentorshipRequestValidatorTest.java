package school.faang.user_service.validation.mentorship;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.mentorship.MentorshipRequestValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipRequestValidatorTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;
    private MentorshipRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description("description")
                .build();
    }

    @Test
    void validate_shouldNotThrowAnyException() {
        assertDoesNotThrow(() -> mentorshipRequestValidator.validate(requestDto));
    }

    @Test
    void validate_shouldInvokeUserServiceValidateUsers() {
        mentorshipRequestValidator.validate(requestDto);
        verify(userService).validateUsers(REQUESTER_ID, RECEIVER_ID);
    }

    @Test
    void validate_shouldThrowMentorshipRequestValidationException_whenRequesterAndReceiverAreTheSameUser() {
        requestDto.setReceiverId(REQUESTER_ID);

        assertThrows(MentorshipRequestValidationException.class,
                () -> mentorshipRequestValidator.validate(requestDto),
                "Requester and receiver cannot be the same user.");
    }

    @Test
    void validate_shouldThrowMentorshipRequestValidationException_whenRequestIsBeforeThreeMonthsPassed() {
        when(mentorshipRequestRepository.findLatestRequest(REQUESTER_ID, RECEIVER_ID))
                .thenReturn(Optional.of(MentorshipRequest
                        .builder()
                        .createdAt(LocalDateTime.now().minusMonths(2))
                        .build()));

        assertThrows(MentorshipRequestValidationException.class,
                () -> mentorshipRequestValidator.validate(requestDto),
                "Request has already been sent for the last three months.");
    }
}