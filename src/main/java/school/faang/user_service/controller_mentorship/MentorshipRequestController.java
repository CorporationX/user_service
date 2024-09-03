package school.faang.user_service.controller_mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.service_mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    @Autowired
    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().trim().isEmpty()) {
            System.out.println("Description is required");
        }

        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
