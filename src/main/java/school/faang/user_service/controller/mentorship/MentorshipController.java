package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    @GetMapping("mentee/{id}")
    public List<UserDto> getMentees(@PathVariable long id) {
        mentorshipValidator.idValidator(id);
        return mentorshipService.getMentees(id);
    }

    @GetMapping("mentor/{id}")
    public List<UserDto> getMentors(@PathVariable long id) {
        mentorshipValidator.idValidator(id);
        return mentorshipService.getMentors(id);
    }

    @DeleteMapping("/mentee/{id}/mentor/{id}")
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipValidator.equalsIdValidator(mentorId, menteeId);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentor/{id}/mentee/{id}")
    public void deleteMentor(long mentorId, long menteeId) {
        mentorshipValidator.equalsIdValidator(mentorId, menteeId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
