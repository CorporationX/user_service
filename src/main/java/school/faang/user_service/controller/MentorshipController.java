package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getMentees(@RequestParam long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentor")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getMentors(@RequestParam long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentee")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@RequestParam long menteeId, @RequestParam long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@RequestParam long menteeId, @RequestParam long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
