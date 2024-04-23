package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<User> getMentees(Long idMentor) {
        return mentorshipService.getMentees(idMentor);
    }

    public List<User> getMentors(Long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
    @PutMapping("/addGoal/{menteeId}")
    public MenteeDto addGoalToMentee(@PathVariable Long menteeId, @RequestBody Long goalId, @RequestBody Long mentorId){
        return mentorshipService.addGoalToMenteeFromMentor(menteeId,goalId,mentorId);
    }
}
