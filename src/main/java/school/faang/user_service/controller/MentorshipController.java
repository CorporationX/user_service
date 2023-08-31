package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Tag(name = "Управление менторством")
@RestController
@RequestMapping("api/v1/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @Operation(summary = "Получить всех менти для пользователя по идентификатору")
    @GetMapping("/mentees/{userId}")
    public List<User> getMentees(@PathVariable @Validated Long userId) {
        return mentorshipService.getMenteesOfUser(userId);
    }

    @Operation(summary = "Получить всех менторов для пользователя по идентификатору")
    @GetMapping("/mentors/{userId}")
    public List<User> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentorsOfUser(userId);
    }

    @Operation(summary = "Удалить менти по идентификатору")
    @DeleteMapping("/mentees/{menteeId}/{mentorId}")
    public void deleteMentee(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation(summary = "Удалить ментора по идентификатору")
    @DeleteMapping("/mentors/{mentorId}/{menteeId}")
    public void deleteMentor(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
