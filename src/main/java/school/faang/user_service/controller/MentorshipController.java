package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;


    public List<User> getMentees(@Validated Long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<User> getMentors(Long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long mentorId, Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
