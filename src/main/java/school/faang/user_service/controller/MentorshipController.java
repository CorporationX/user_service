package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipValidator mentorshipValidator;

    @GetMapping("/mentors/{id}")
    public List<UserDto> getMentors(@PathVariable @Min(1) long id) {
        return mentorshipService.getMentors(id);
    }

    @GetMapping("/mentees/{id}")
    public List<UserDto> getMentees(@PathVariable @Min(1) long id) {
        return mentorshipService.getMentees(id);
    }

    @DeleteMapping("/mentors")
    public void deleteMentor(@RequestParam @Min(1) long menteeId, @RequestParam @Min(1) long mentorId) {
        mentorshipValidator.validateMentorshipIds(mentorId, menteeId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @DeleteMapping("/mentees")
    public void deleteMentee(@RequestParam @Min(1) long mentorId, @RequestParam @Min(1) long menteeId) {
        mentorshipValidator.validateMentorshipIds(mentorId, menteeId);
        mentorshipService.deleteMentee(mentorId, menteeId);
    }
}