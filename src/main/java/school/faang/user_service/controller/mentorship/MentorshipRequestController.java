package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.exception.mentorship.MentorshipRequestException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    private MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestService.rejectRequest(id, rejection);
        if (mentorshipRequestDto.getDescription() != null) {
            return mentorshipRequestDto;
        } else {
            throw new MentorshipRequestException("There is no description in RejectionDto");
        }
    }
}