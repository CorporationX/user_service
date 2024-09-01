package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mentorship_requests")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDtoForResponse requestMentorship
            (@Validated @RequestBody MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDtoForRequest);
    }

    @PostMapping("/filter")
    public List<MentorshipRequestDtoForResponse> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/accept/{id}")
    public MentorshipRequestDtoForResponse acceptRequest(@Positive @PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/reject/{id}")
    public MentorshipRequestDtoForResponse rejectRequest(@Positive @PathVariable long id,
                                                         @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
