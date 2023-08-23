package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto dto) {
        return mentorshipRequestService.requestMentorship(dto);
    }

    @PostMapping("/request/list")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filterDto) {
        return mentorshipRequestService.getRequests(filterDto);
    }

    @PostMapping("/request/{id}/accept")
    public void acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/request/{id}/reject")
    public void rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
