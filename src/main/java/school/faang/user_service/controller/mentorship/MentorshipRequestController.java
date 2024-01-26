package school.faang.user_service.controller.mentorship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    private void getRequests(RequestFilterDto filter) {
        mentorshipRequestService.getRequests(filter);
    }
}
