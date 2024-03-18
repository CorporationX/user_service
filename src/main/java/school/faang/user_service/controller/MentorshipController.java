package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/get/mentees/{mentorId}")
    public List<UserDto> getMentees(@PathVariable @Min(0) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/get/mentors/{menteeId}")
    public List<UserDto> getMentors(@PathVariable @Min(0) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @PutMapping("delete/mentee")
    public void deleteMentee(@RequestParam long mentorId, @RequestParam long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @PutMapping("delete/mentor")
    public void deleteMentor(@RequestParam long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
