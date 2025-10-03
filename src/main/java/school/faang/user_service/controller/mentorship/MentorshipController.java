package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    // Добавляем связь ментор-менти
    @PostMapping("/{mentorId}/add/{menteeId}")
    public void addMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.addMentorship(mentorId, menteeId);
    }

    // Удалить связь ментор-менти
    @DeleteMapping("/{mentorId}/delete/{menteeId}")
    public void deleteMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentorship(mentorId, menteeId);
    }

    // Посмотреть всех менти пользователя
    @GetMapping("/mentee/{UserId}/")
    public List<UserDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    // Посмотреть всех менторов пользователя
    @GetMapping("/mentor/{UserId}/")
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }
}