package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable @Min(1L) long mentorId) {
        log.debug("Received new request to get mentees for mentor with id:{}", mentorId);
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable @Min(1L) long menteeId) {
        log.debug("Received new request to get mentors for mentee with id:{}", menteeId);
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable @Min(1L) long mentorId, @PathVariable @Min(1L) long menteeId) {
        log.debug("Received new request to delete mentee with id:{} from mentor with id:{}", menteeId, mentorId);
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable @Min(1L) long menteeId, @PathVariable @Min(1L) long mentorId) {
        log.debug("Received new request to delete mentor with id:{} from mentee with id:{}", mentorId, menteeId);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
