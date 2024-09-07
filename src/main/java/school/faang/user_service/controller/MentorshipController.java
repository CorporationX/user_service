package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentorId}")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors/{menteeId}")
    public List<UserDto> getMentors(@PathVariable long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentee/{mentorId}/{menteeId}")
    public Optional<UserDto> deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        return mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/mentor/{menteeId}/{mentorId}")
    public Optional<UserDto> deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        return mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
