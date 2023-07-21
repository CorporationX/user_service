package school.faang.user_service.controllers.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.services.mentorship.MentorshipRequestService;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/mentorship/request")
    public void requestMentorship(@RequestBody MentorshipRequestDto dto) throws Exception {
        mentorshipRequestService.requestMentorship(dto);
    }

    @PostMapping("/mentorship/request/list")
    public List<User> getRequests(@RequestBody long userId) {
        return mentorshipRequestService.getRequests(userId);
    }

    @PostMapping("mentorship/request/{id}/accept")
    public void acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/mentorship/request/{id}/reject")
    public void rejectRequest(@PathVariable long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
