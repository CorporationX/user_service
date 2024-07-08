package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filters) {
        return mentorshipRequestService.getRequests(filters);
    }

    @PutMapping("/accept/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MentorshipRequestDto acceptRequest(@PathVariable("id") Long mentorshipRequestId) {
        return mentorshipRequestService.acceptRequest(mentorshipRequestId);
    }

    @PutMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto rejectRequest(@PathVariable("id") Long requestId,
                                              @RequestBody @Valid RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
