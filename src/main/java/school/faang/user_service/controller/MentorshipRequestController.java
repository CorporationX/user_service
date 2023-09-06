package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/{id}/accept")
    public void acceptRequest(@PathVariable long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    @PostMapping("/{id}/reject")
    public void rejectRequest(long requestId, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
