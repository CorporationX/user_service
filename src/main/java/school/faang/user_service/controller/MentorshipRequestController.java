package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestMapper;
import school.faang.user_service.service.MentorshipRequestService;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final RequestMapper requestMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(requestMapper.toEntity(filter));
    }

    public void acceptRequest(long id) throws Exception {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
