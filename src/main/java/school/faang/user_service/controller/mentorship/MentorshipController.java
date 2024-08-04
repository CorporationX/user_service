package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
@Tag(name = "Mentorship controller endpoint")
public class MentorshipController {
    private static final String MENTEE = "/mentee";
    private static final String MENTOR = "/mentor";
    private final UserContext userContext;
    private final MentorshipService mentorshipService;

    @GetMapping(MENTEE)
    @Operation(summary = "Get user's mentees by User id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with mentees",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MentorshipUserDto.class, type = "array")) }) })
    public List<MentorshipUserDto> getMentees(@RequestParam long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping(MENTOR)
    @Operation(summary = "Get user's mentors by User id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with mentors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MentorshipUserDto.class, type = "array")) }) })
    public List<MentorshipUserDto> getMentors(@RequestParam long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping(MENTEE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user's mentee by mentee id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentee successful deleted")})
    public void deleteMentee(@RequestParam long menteeId) {
        mentorshipService.deleteMentorshipRelations(
                userContext.getUserId(), menteeId);
    }

    @DeleteMapping(MENTOR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user's mentor by mentor id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor successful deleted")})
    public void deleteMentor(@RequestParam long mentorId) {
        mentorshipService.deleteMentorshipRelations(
                mentorId, userContext.getUserId());
    }

}
