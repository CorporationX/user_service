package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}")
    public List<UserDto> getMentees(@PathVariable Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentees/{menteeId}")
    public List<UserDto> getMentors(@PathVariable Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentors/{mentorId}/{menteeId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        mentorshipService.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mentees/{menteeId}/{mentorId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        mentorshipService.deleteByMentorIdAndMenteeId(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

}
