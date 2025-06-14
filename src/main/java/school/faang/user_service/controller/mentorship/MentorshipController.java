package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/{userId}")
@Tag(name = "Mentorship", description = "Operations related to user mentorships")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees")
    @Operation(summary = "Get mentees of a user", description = "Returns a list of mentees for the specified user.")
    public List<MenteeDto> getMentees(@PathVariable Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors")
    @Operation(summary = "Get mentors of a user", description = "Returns a list of mentors for the specified user.")
    public List<MentorDto> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentee/{menteeId}")
    @Operation(summary = "Remove mentee", description = "Removes a mentee relationship for the specified user.")
    public void deleteMentee(@PathVariable Long userId, @PathVariable Long menteeId) {
        mentorshipService.deleteMentee(userId, menteeId);
    }

    @DeleteMapping("/mentor/{mentorId}")
    @Operation(summary = "Remove mentor", description = "Removes a mentor relationship for the specified user.")
    public void deleteMentor(@PathVariable Long userId, @PathVariable Long mentorId) {
        mentorshipService.deleteMentor(userId, mentorId);
    }
}
