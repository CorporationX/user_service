package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RejectionDto;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    @Autowired
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(id, rejectionDto);
    }
}
