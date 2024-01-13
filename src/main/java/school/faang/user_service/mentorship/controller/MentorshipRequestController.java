package school.faang.user_service.mentorship.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.service.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private MentorshipRequestService mentorshipRequestService;

    private void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
