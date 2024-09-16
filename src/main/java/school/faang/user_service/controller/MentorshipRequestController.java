package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.*;
import school.faang.user_service.mapper.RejectionMapper;
import school.faang.user_service.mapper.RequestMapper;
import school.faang.user_service.service.MentorshipRequestService;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final RequestMapper requestMapper;
    private final RejectionMapper rejectionMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(requestMapper.toEntity(filter));
    }

    public void acceptRequest(long id) throws Exception {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejectionDto) {
        mentorshipRequestService.rejectRequest(id, rejectionMapper.toEntity(rejectionDto));
    }
}
