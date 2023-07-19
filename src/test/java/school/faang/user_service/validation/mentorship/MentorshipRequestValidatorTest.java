package school.faang.user_service.validation.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipRequestValidatorTest {
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long INCORRECT_ID = 3L;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipRequestValidator mentorshipRequestValidator;
    private MentorshipRequestDto requestDto;

    @BeforeEach
    void setUp() {
        when(userRepository.existsById(REQUESTER_ID)).thenReturn(true);
        when(userRepository.existsById(RECEIVER_ID)).thenReturn(true);
        when(userRepository.existsById(INCORRECT_ID)).thenReturn(false);

        requestDto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description("description")
                .build();
    }

    @Test
    void validate_shouldNotThrowException() {
        mentorshipRequestValidator.validate(requestDto);
    }

    @Test
    void validate_shouldThrowEntityNotFoundException() {
        requestDto.setRequesterId(INCORRECT_ID);
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestValidator.validate(requestDto),
                "Requester with id " + INCORRECT_ID + " not found.");
    }
}