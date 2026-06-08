package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    @Operation(
            summary = "Get user's Mentees",
            description = "Gets a list of Mentees for the user with the specified ID."
    )
    public List<UserDto> getMentees(@PathVariable("userId") long userId) {
        log.info("GET /mentees — request to get mentees for userId: {}", userId);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    @Operation(
            summary = "Get user's Mentors",
            description = "Gets a list of Mentors for the user with the specified ID."
    )
    public List<UserDto> getMentors(@PathVariable("userId") long userId) {
        log.info("GET /mentors — request to get mentors for userId: {}", userId);
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{userId}/mentees/{menteeId}")
    @Operation(
            summary = "Delete user's Mentee",
            description = "Deletes Mentee for the user with the specified ID."
    )
    public void deleteMentee(@PathVariable("userId") long userId, @PathVariable("menteeId") long menteeId) {
        log.info("DELETE /mentees — request to delete menteeId: {} for userId: {}", menteeId, userId);
        mentorshipService.deleteMentee(userId, menteeId);
    }

    @DeleteMapping("/{userId}/mentors/{mentorId}")
    @Operation(
            summary = "Delete user's Mentor",
            description = "Deletes Mentor for the user with the specified ID."
    )
    public void deleteMentor(@PathVariable("userId") long userId, @PathVariable("mentorId") long mentorId) {
        log.info("DELETE /mentors — request to delete mentorId: {} for userId: {}", mentorId, userId);
        mentorshipService.deleteMentor(userId, mentorId);
    }
}
