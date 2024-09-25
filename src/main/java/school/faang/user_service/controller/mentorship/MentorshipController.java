package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.dto.mentee.MenteeDTO;
import java.util.List;

@RestController
public class MentorshipController {

    @Autowired
    private MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public List<MenteeDTO> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentees/{mentorId}/{menteeId}")
    public void deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors/{menteeId}/{mentorId}")
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}