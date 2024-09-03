package school.faang.user_service.controller;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

@Controller
public class MentorshipRequestController {
    MentorshipRequestService service;

    public void requestMentorship(MentorshipRequestDto requestDto) {
        service.requestMentorship(requestDto);
    }
}
