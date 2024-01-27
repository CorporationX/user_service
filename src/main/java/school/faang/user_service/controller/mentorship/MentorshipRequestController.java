package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    private MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (StringUtils.isEmpty(rejection.getReason())) {
            throw new DataNotFoundException("There is no description in RejectionDto");
        }
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}