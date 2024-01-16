package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
