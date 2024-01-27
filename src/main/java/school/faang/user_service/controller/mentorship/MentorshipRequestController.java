package school.faang.user_service.controller.mentorship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    private void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.commonCheck(mentorshipRequestDto);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
