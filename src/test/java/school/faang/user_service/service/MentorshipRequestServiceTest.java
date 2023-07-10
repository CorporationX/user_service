package school.faang.user_service.service;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.validation.MentorshipRequestValidator;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    private MentorshipRequestDto badRequest;
    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Code")
                .build();

        badRequest = MentorshipRequestDto.builder() // Because requesterId == receiverId
                .requesterId(1L)
                .receiverId(1L)
                .build();
    }

    @Test
    @DisplayName("Test: Positive scenario")
    public void testRequestMentorshipPositive() {
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Test: Mentorship request with requester & receiver being same person")
    public void testRequestMentorshipNegative() {
        when(mentorshipRequestService.requestMentorship(badRequest)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(badRequest));
    }
}