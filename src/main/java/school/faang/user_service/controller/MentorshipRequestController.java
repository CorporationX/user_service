package school.faang.user_service.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@AllArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public boolean requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank())
            return false;
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto.getUserId(), mentorshipRequestDto.getMentorId(), mentorshipRequestDto.getDescription());
    }
}
