package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
@Component
@RequiredArgsConstructor
@RestController
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    @PostMapping("/requestMentorship")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
