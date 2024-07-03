package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
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
        MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto();

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestValidator, times(1))
                .validateMentorshipRequestDescription(mentorshipRequestDto.getDescription());
    }

    @Test
    public void testRequestMentorshipServiceExecution() {
        MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto();

        mentorshipRequestController.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestService, times(1))
                .requestMentorship(mentorshipRequestDto);
    }

    private MentorshipRequestDto getMentorshipRequestDto() {
        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setDescription("description");
        return mentorshipRequestDto;
    }

    @Test
    public void testGetRequestsServiceExecution() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();

        mentorshipRequestController.getRequests(mentorshipRequestFilterDto);
        verify(mentorshipRequestService, times(1))
                .getRequests(mentorshipRequestFilterDto);
    }
}