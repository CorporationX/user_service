package school.faang.user_service.mentorship.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.mentorship.filter.RequestFilterDto;
import school.faang.user_service.mentorship.service.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private MentorshipRequestService mentorshipRequestService;

    private void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(filter);
    }
}
