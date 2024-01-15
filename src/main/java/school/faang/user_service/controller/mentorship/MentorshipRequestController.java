package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
@Component
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    @Autowired
    public MentorshipRequestController(MentorshipRequestService mentorshipRequestService) {
        this.mentorshipRequestService = mentorshipRequestService;
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
