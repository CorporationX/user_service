package school.faang.user_service.requestformentoring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.requestformentoring.services.MentorshipRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto request) {
        mentorshipRequestService.requestMentorship(request);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
