package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import school.faang.user_service.dto.event.RejectionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.filter.RequestFilterDto;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/mentorship/request")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequest);
    }

    @GetMapping("/mentorship/request")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getRequests(@Valid @RequestBody RequestFilterDto requestFilter) {
        return mentorshipRequestService.getRequests(requestFilter);
    }

    @PutMapping("/mentorship/request/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto acceptRequest(@PathVariable Long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/mentorship/request/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public MentorshipRequestDto rejectRequest(@PathVariable @RequestBody Long id, RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
