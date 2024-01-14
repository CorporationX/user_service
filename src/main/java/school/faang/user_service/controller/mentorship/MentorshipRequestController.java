package school.faang.user_service.controller.mentorship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private MentorshipRequestService mentorshipRequestService;

    private void acceptRequest(long id) {
        mentorshipRequestService.acceptRequest(id);
    }
}
