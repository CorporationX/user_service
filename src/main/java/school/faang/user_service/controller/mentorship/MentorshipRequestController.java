package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestRejectionDto;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship/request")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(
            @RequestBody MentorshipRequestCreationDto mentorshipRequestCreationDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestCreationDto);
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        return mentorshipRequestService.getRequests(requestFilterDto);
    }

    @PatchMapping("{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable Long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @PatchMapping("{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable Long requestId,
                                              @RequestBody MentorshipRequestRejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(requestId, rejectionDto);
    }
}
