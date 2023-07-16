package school.faang.user_service.service;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.validation.MentorshipRequestValidator;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
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

    private RequestFilterDto requestFilterDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Wanna code like you bruh")
                .build();

        badRequest = MentorshipRequestDto.builder() // Because requesterId == receiverId
                .requesterId(1L)
                .receiverId(1L)
                .build();

        requestFilterDto = RequestFilterDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .description("Wanna be a good coder")
                .requestStatus(RequestStatus.ACCEPTED)
                .build();
    }

    @Test
    @DisplayName("Test: Mentorship request - positive scenario")
    public void testRequestMentorshipIsOk() {
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Test: Mentorship request with requester & receiver being same person")
    public void testRequestMentorshipBadRequest() {
        when(mentorshipRequestService.requestMentorship(badRequest)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(badRequest));
    }

    @Test
    @DisplayName("Test: Get requests - positive scenario")
    public void testGetRequestsIsOk() {
        Assertions.assertDoesNotThrow(() -> mentorshipRequestService.getRequests(requestFilterDto));
    }

    @Test
    @DisplayName("Test: Get requests - filter request is null")
    public void testGetRequestsBadRequest() {
        when(mentorshipRequestService.getRequests(null)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> mentorshipRequestService.getRequests(null));
    }
}