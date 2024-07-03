package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Test
    public void testRequestMentorshipValidationExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("description").build();

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestDescription(mentorshipRequestDto.getDescription());
    }

    @Test
    public void testRequestMentorshipServiceExecution() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .description("description").build();

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestService, times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    @Test
    public void testGetRequestsServiceExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();

        mentorshipRequestController.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestService, times(1))
                .getRequests(mentorshipRequestFilterDto);
    }

    @Test
    public void testAcceptRequestServiceExecution() {
        long requestId = 1L;

        mentorshipRequestController.acceptRequest(requestId);
        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    public void testRejectRequestServiceExecution() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("rejection reason");

        mentorshipRequestController.rejectRequest(requestId, rejectionDto);
        verify(mentorshipRequestService, times(1)).rejectRequest(requestId, rejectionDto);
    }
}