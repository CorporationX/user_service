package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    public final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentor_id}")
    public List<MentorshipDto> getMentees(@PathVariable("mentor_id") long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors/{mentee_id}")
    public List<MentorshipDto> getMentors(@PathVariable("mentee_id") long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentees")
    public void deleteMentee(@RequestParam("mentee_id") long menteeId,
                             @RequestParam("mentor_id") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors")
    public void deleteMentor(@RequestParam("mentee_id") long menteeId,
                             @RequestParam("mentor_id") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
