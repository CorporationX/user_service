package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.MenteeDTO;
import java.util.List;

@RestController
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Autowired
    public MentorshipController(MentorshipService mentorshipService) {
        this.mentorshipService = mentorshipService;
    }

    @GetMapping("/mentees/{userId}")
    public List<MenteeDTO> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @DeleteMapping("/mentees/{mentorId}/{menteeId}")
    public void deleteMentee(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentee(menteeId,mentorId);
    }

    @DeleteMapping("/mentors/{menteeId}/{mentorId}")
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}