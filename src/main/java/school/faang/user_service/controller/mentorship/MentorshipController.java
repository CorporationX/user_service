package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    public List<UserDto> getMentees(Long userId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(Long userId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(userId);
        return mentorshipService.getMentors(userId);
    }

    public void removeMentorsMentee(Long mentorId, Long menteeId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(mentorId);
        mentorshipValidator.validationForNullOrLessThenOneUserId(menteeId);
        mentorshipValidator.validationForIdsNotEqual(mentorId, menteeId);
        mentorshipService.removeMentorsMentee(mentorId, menteeId);
    }

    public void removeMentorOfMentee(Long mentorId, Long menteeId) {
        mentorshipValidator.validationForNullOrLessThenOneUserId(mentorId);
        mentorshipValidator.validationForNullOrLessThenOneUserId(menteeId);
        mentorshipValidator.validationForIdsNotEqual(mentorId, menteeId);
        mentorshipService.removeMentorOfMentee(mentorId, menteeId);
    }
}
