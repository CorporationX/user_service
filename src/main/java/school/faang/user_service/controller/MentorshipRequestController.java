package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDtoForResponse requestMentorship(@Valid MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDtoForRequest);
    }

    public List<MentorshipRequestDtoForResponse> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
