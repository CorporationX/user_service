package school.faang.user_service.controller.mentorship;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.mentorship.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Component
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    private void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getId() == null || mentorshipRequestDto.getRequester() == null
                || mentorshipRequestDto.getReceiver() == null || mentorshipRequestDto.getDescription() == null) {
            throw new DataValidationException("Incorrect data");
        }
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
