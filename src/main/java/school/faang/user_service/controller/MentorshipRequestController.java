package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDtoForResponse requestMentorship(@Validated @RequestBody
                                                                 MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDtoForRequest);
    }

    @GetMapping
    public List<MentorshipRequestDtoForResponse> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public MentorshipRequestDtoForResponse acceptRequest(@Positive long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    public MentorshipRequestDtoForResponse rejectRequest(@Positive long id, RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
