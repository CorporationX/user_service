package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.ErrorResponseDto;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
@Tag(name = "Mentorship controller", description = "Controller which is handling /mentorship/** endpoints")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @Operation(
            summary = "Retrieve mentees of user by user id",
            description = "Get list of mentees of user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content =
                    {@Content(schema = @Schema(implementation = MentorshipUserDto.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})})
    @GetMapping("/{userId}/mentees")
    public List<MentorshipUserDto> getMentees(@PathVariable @Positive Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<MentorshipUserDto> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable Long mentorId,
                             @PathVariable Long menteeId) {
        mentorshipService.deleteMentorship(menteeId, mentorId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable Long menteeId,
                             @PathVariable Long mentorId) {
        mentorshipService.deleteMentorship(menteeId, mentorId);
    }

}
