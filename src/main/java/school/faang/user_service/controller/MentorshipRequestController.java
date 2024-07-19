package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDtoForResponse requestMentorship(@Validated MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDtoForRequest);
    }

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
