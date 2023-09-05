package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public void acceptRequest(long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }
}
