package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.dto.mentee.MentorDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<MenteeDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }
    public List<MentorDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }
}
