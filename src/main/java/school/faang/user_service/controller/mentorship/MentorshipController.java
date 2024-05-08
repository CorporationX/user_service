package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping(path = "/get-mentees")
    public List<UserDto> getMentees(@RequestParam long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping(path = "/get-mentors")
    public List<UserDto> getMentors(@RequestParam long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping(path = "/delete-mentee")
    public void deleteMentee(@RequestParam long menteeId, @RequestParam long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping(path = "/delete-mentor")
    public void deleteMentor(@RequestParam long menteeId, @RequestParam long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

}
