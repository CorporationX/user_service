package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping
    public ResponseEntity<MentorshipDto> create(@RequestBody MentorshipDto mentorshipDto) {
        return new ResponseEntity<>(mentorshipService.create(mentorshipDto), HttpStatus.OK);
    }


    @Operation(
            summary = "Get Mentees",
            description = "Get list of mentees by user identifier"
    )
    @GetMapping("/mentor/{id}")
    public List<UserDto> getMentees(@PathVariable @Positive(message = "Id должен быть положительным") long id) {
        return mentorshipService.getMentees(id);
    }

    @Operation(
            summary = "Get Mentors",
            description = "Get list of mentors by user identifier"
    )
    @GetMapping("/mentee/{id}")
    public List<UserDto> getMentors(@PathVariable @Positive(message = "Id должен быть положительным") long id) {
        return mentorshipService.getMentors(id);
    }

    @Operation(
            summary = "Delete Mentee",
            description = "Delete mentee by mentor's and mentee's identifier"
    )
    @DeleteMapping("/mentor/{mentorId}/mentee/{menteeId}")
    public void deleteMentee(@PathVariable @Positive(message = "Id должен быть положительным") long menteeId,
                             @PathVariable @Positive(message = "Id должен быть положительным") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation(
            summary = "Delete Mentor",
            description = "Delete mentor by mentor's and mentee's identifier"
    )
    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public void deleteMentor(@PathVariable @Positive(message = "Id должен быть положительным") long menteeId,
                             @PathVariable @Positive(message = "Id должен быть положительным") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
