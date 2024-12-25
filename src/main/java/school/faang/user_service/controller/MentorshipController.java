package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.service.user.MentorshipService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserValidator userValidator;

    public List<MenteeResponseDto> getMentees(long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentees(userId);
    }

    public List<MenteeResponseDto> getMentors(long userId) {
        userValidator.validateUserId(userId);
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        userValidator.validateUserId(menteeId);
        userValidator.validateUserId(mentorId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
