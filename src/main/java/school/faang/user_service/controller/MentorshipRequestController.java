package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new DataValidationException("Добавьте описание к запросу на менторство");
        }

        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
