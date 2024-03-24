package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mentorship")
@Tag(name = "Mentorship", description = "Endpoints for managing mentorship system")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserContext userContext;

    @Operation(summary = "Get list of user's mentees")
    @GetMapping("/mentees")
    public List<UserDto> getMentees() {
        long userId = userContext.getUserId();
        return mentorshipService.getMentees(userId);
    }

    @Operation(summary = "Get list of mentors by menteeId")
    @GetMapping("/mentors")
    public List<UserDto> getMentors() {
        long userId = userContext.getUserId();
        return mentorshipService.getMentors(userId);
    }

    @Operation(summary = "Delete mentee from mentor's list of mentees")
    @DeleteMapping("/mentee/{menteeId}")
    public void deleteMentee(@PathVariable long menteeId) {
        long mentorId = userContext.getUserId();
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation(summary = "Delete mentor from mentee's list of mentors")
    @DeleteMapping("/mentor/{mentorId}")
    public void deleteMentor(@PathVariable long mentorId) {
        long menteeId = userContext.getUserId();
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
