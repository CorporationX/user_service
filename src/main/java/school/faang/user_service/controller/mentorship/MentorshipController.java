package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long userId) {
        List<UserDto> mentees = mentorshipService.getMentees(userId);
        return mentees;
    }

    public List<UserDto> getMentors(long userId) {
        List<UserDto> mentors = mentorshipService.getMentors(userId);
        return mentors;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
