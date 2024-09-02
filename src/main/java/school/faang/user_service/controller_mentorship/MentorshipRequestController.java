package school.faang.user_service.controller_mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public static void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequestService.requestMentorship();
    }
}
