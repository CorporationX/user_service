package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("api/v1/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public List<User> getMentees(@PathVariable @Validated Long userId) {
        return mentorshipService.getMenteesOfUser(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<User> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentorsOfUser(userId);
    }

    @DeleteMapping("/mentees/{menteeId}/{mentorId}")
    public void deleteMentee(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors/{mentorId}/{menteeId}")
    public void deleteMentor(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
