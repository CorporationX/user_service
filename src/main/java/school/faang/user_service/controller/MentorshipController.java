package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public List<UserDto> getMentees(@PathVariable("userId") long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<UserDto> getMentors(@PathVariable("userId") long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentee")
    public void deleteMentee(@RequestParam("menteeId") long menteeId, @RequestParam("mentorId") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentor")
    public void deleteMentor(@RequestParam("menteeId") long menteeId, @RequestParam("mentorId") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}