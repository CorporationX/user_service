package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;

@RequiredArgsConstructor
@RestController
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public void acceptRequest(long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    public void rejectRequest(long requestId, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
