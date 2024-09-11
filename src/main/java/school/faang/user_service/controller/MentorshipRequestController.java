package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) throws Exception {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
