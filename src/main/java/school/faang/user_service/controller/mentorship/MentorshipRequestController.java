package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateMentorshipRequestDto(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        mentorshipRequestValidator.validateMentorshipRequestFilterDto(filters);
        return mentorshipRequestService.getRequests(filters);
    }

    public MentorshipRequestDto acceptRequest(long mentorshipRequestId) {
        mentorshipRequestValidator.validateMentorshipRequestId(mentorshipRequestId);
        return mentorshipRequestService.acceptRequest(mentorshipRequestId);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestValidator.validateMentorshipRejectionDto(rejection);
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
