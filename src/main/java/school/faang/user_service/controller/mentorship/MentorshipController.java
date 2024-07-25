package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/mentorship")
@Tag(name = "Mentorship API", description = "API for managing mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees")
    @Operation(summary = "Get mentees", description = "Retrieves a list of mentees for a given mentor.")
    public List<UserDto> getMentees(@RequestParam @Parameter(description = "ID of the mentor") long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors")
    @Operation(summary = "Get mentors", description = "Retrieves a list of mentors for a given mentee.")
    public List<UserDto> getMentors(@RequestParam @Parameter(description = "ID of the mentee") long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentees")
    @Operation(summary = "Delete mentee", description = "Deletes a mentee from a mentor's list.")
    public void deleteMentee(@RequestParam @Parameter(description = "ID of the mentor") long mentorId,
                             @RequestParam @Parameter(description = "ID of the menеуу") long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentors")
    @Operation(summary = "Delete mentor", description = "Deletes a mentor from a mentee's list.")
    public void deleteMentor(@RequestParam @Parameter(description = "ID of the mentee") long menteeId,
                             @RequestParam @Parameter(description = "ID of the mentor") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}