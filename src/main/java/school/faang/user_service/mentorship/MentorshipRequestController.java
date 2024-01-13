package school.faang.user_service.mentorship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private MentorshipRequestService mentorshipRequestService;

    private void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
