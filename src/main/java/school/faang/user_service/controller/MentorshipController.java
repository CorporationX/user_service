package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("{mentorId}/mentees")
    @Operation(description = "Get all mentees from mentor")
    public List<UserDto> getMentees(@PathVariable @Min(0) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("{menteeId}/mentors")
    @Operation(description = "Get all mentors from mentee")
    public List<UserDto> getMentors(@PathVariable @Min(0) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("mentor/{mentorId}/deleteMentee/{menteeId} ")
    @Operation(description = "Delete mentee from a mentor's list of mentees")
    public void deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("mentee/{mentorId}/deleteMentor/{menteeId} ")
    @Operation(description = "Delete mentor from a mentee's list of mentors")
    public void deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
