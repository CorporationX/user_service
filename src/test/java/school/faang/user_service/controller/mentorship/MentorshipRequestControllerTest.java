package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Test
    @DisplayName("testing requestMentorship service execution")
    public void testRequestMentorshipServiceRequestMentorshipExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("description").build();

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestService, times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    @Test
    @DisplayName("testing getRequests service execution")
    public void testGetRequestsServiceGetRequestsExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();

        mentorshipRequestController.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestService, times(1))
                .getRequests(mentorshipRequestFilterDto);
    }

    @Test
    @DisplayName("testing acceptRequest with null requestId")
    public void testAcceptRequestWithNullRequestId() {
        assertThrows(NullPointerException.class,
                () -> mentorshipRequestController.acceptRequest(null));
    }

    @Test
    @DisplayName("testing acceptRequest service execution")
    public void testAcceptRequestServiceAcceptRequestExecution() {
        long requestId = 1L;

        mentorshipRequestController.acceptRequest(requestId);
        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    @DisplayName("testing rejectRequest with null requestId")
    public void testRejectRequestWithNullRequestId() {
        assertThrows(NullPointerException.class,
                () -> mentorshipRequestController.rejectRequest(null, new RejectionDto()));
    }

    @Test
    @DisplayName("testing rejectRequest service execution")
    public void testRejectRequestServiceRejectRequestExecution() {
        long requestId = 1L;
        RejectionDto rejectionDto = RejectionDto.builder()
                .rejectionReason("Rejection Reason").build();

        mentorshipRequestController.rejectRequest(requestId, rejectionDto);
        verify(mentorshipRequestService, times(1)).rejectRequest(requestId, rejectionDto);
    }
}