package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Objects.requireNonNull(mentorshipRequestDto,
                "MentorshipRequestDto cant be null.");
        if (mentorshipRequestDto.getDescription().isEmpty() || mentorshipRequestDto.getDescription().length() < 10) {
            throw new IllegalArgumentException("Description is required and " +
                    "should be at least 10 characters long.");
        }
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }
}
