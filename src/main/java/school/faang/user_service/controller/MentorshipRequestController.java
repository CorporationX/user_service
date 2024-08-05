package school.faang.user_service.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

@RestController
@AllArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestValidator.validateRequest(mentorshipRequestDto))
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return mentorshipRequestDto;
    }
}