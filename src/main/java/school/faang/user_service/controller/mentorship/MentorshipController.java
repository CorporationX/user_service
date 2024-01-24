package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    @GetMapping("/users/mentees")
    public List<UserDto> getMentees(@PathVariable Long userId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/users/mentors")
    public List<UserDto> getMentors(@PathVariable Long userId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(userId);
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/users/mentors/{mentorId}/mentees/{menteeId}")
    public void removeMentorsMentee(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(mentorId);
        mentorshipValidator.validationForNullOrLessThenOneUserId(menteeId);
        mentorshipValidator.validationForIdsNotEqual(mentorId, menteeId);
        mentorshipService.removeMenteeOfMentor(mentorId, menteeId);
    }

    @DeleteMapping("/users/mentors/{mentorId}/mentees/{menteeId}")
    public void removeMentorOfMentee(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(mentorId);
        mentorshipValidator.validationForNullOrLessThenOneUserId(menteeId);
        mentorshipValidator.validationForIdsNotEqual(mentorId, menteeId);
        mentorshipService.removeMentorOfMentee(mentorId, menteeId);
    }
}
