package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    public List<MentorshipUserDto> getMentees(@PathVariable @Positive Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<MentorshipUserDto> getMentors(@PathVariable @Positive Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{mentorId}/mentees/{menteeId}")
    public Map<String, String> deleteMentee(@PathVariable @Positive Long mentorId,
                                            @PathVariable @Positive Long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
        return Map.of(
                "message",
                "Менти с id %d у ментора с id %d удален успешно".formatted(menteeId, mentorId)
        );
    }

    @DeleteMapping("/{menteeId}/mentors/{mentorId}")
    public Map<String, String> deleteMentor(@PathVariable @Positive Long menteeId,
                                            @PathVariable @Positive Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
        return Map.of(
                "message",
                "Ментор с id %d удален у пользователя с id %d".formatted(mentorId, menteeId)
        );
    }

}
